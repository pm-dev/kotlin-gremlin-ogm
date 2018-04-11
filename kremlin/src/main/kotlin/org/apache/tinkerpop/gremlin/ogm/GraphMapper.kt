package org.apache.tinkerpop.gremlin.ogm

import org.apache.tinkerpop.gremlin.ogm.exceptions.*
import org.apache.tinkerpop.gremlin.ogm.extensions.getProperties
import org.apache.tinkerpop.gremlin.ogm.extensions.setProperties
import org.apache.tinkerpop.gremlin.ogm.extensions.toMultiMap
import org.apache.tinkerpop.gremlin.ogm.mappers.BiMapper
import org.apache.tinkerpop.gremlin.ogm.mappers.Mapper
import org.apache.tinkerpop.gremlin.ogm.mappers.PropertyBiMapper
import org.apache.tinkerpop.gremlin.ogm.mappers.SerializedProperty
import org.apache.tinkerpop.gremlin.ogm.mappers.scalar.InstantPropertyMapper
import org.apache.tinkerpop.gremlin.ogm.mappers.scalar.UUIDPropertyMapper
import org.apache.tinkerpop.gremlin.ogm.mappers.scalar.identity.*
import org.apache.tinkerpop.gremlin.ogm.reflection.ObjectDescription
import org.apache.tinkerpop.gremlin.ogm.reflection.PropertyDescription
import org.apache.tinkerpop.gremlin.ogm.reflection.VertexObjectDescription
import org.apache.tinkerpop.gremlin.ogm.relationships.Edge
import org.apache.tinkerpop.gremlin.ogm.relationships.Relationship
import org.apache.tinkerpop.gremlin.ogm.relationships.bound.MultiBoundPath
import org.apache.tinkerpop.gremlin.ogm.relationships.bound.SingleBoundPath
import org.apache.tinkerpop.gremlin.ogm.relationships.steps.Path
import org.apache.tinkerpop.gremlin.ogm.relationships.steps.StepTraverser
import org.apache.tinkerpop.gremlin.process.traversal.dsl.graph.GraphTraversal
import org.apache.tinkerpop.gremlin.process.traversal.dsl.graph.GraphTraversalSource
import org.apache.tinkerpop.gremlin.structure.Vertex
import org.slf4j.LoggerFactory
import java.time.Instant
import java.util.*
import kotlin.reflect.KClass
import kotlin.reflect.KParameter
import kotlin.reflect.full.isSubclassOf

/**
 * The main object a client's application will interact with.
 * This class provides the ability to map objects from a clients domain to the graph and back.
 *
 * Remember to register classes annotated with @Vertex using the 'vertexClasses' parameter as this
 * library does not scan for those objects automatically.
 */
open class GraphMapper private constructor(
        val g: GraphTraversalSource,
        private val vertexObjectDescriptions: Map<KClass<*>, VertexObjectDescription<*>>,
        private val nestedObjectDescriptions: Map<KClass<*>, ObjectDescription<*>>,
        private val scalarMappers: Map<KClass<*>, PropertyBiMapper<*, *>>
) {
    constructor(
            g: GraphTraversalSource,
            vertexClasses: Set<KClass<*>>,
            nestedObjectClasses: Set<KClass<*>> = setOf(),
            scalarMappers: Map<KClass<*>, PropertyBiMapper<*, *>> = mapOf()
    ) : this(
            g,
            vertexClasses.associate { it to VertexObjectDescription.describe(it) },
            nestedObjectClasses.associate { it to ObjectDescription.describe(it) },
            scalarMappers
    )

    private val vertexObjectDescriptionsByLabel: Map<String, VertexObjectDescription<*>> =
            vertexObjectDescriptions.mapKeys { it.value.label }

    private val genericMapper = object : BiMapper<Any, Vertex> {
        override fun forwardMap(from: Any): Vertex = from.vertexMapper().forwardMap(from)
        override fun inverseMap(from: Vertex): Any = from.vertexMapper<Any>().inverseMap(from)
    }

    /**
     * Load Vertex
     */

    /**
     * Queries the graph for a vertex with a given id. Null is returned if no vertex exists in the graph
     * with the given id.
     */
    fun <T : Any> load(id: Any): T? = load<T>(listOf(id)).single()

    /**
     * Queries the graph for vertices with a given id. The returned list will return null
     * for ids that that don't correspond with a vertex in the graph.
     */
    fun <T : Any> load(vararg ids: Any): List<T?> = load(ids.asList())

    /**
     * Queries the graph for vertices with a given id. The returned list will return null
     * for ids that that don't correspond with a vertex in the graph.
     */
    fun <T : Any> load(ids: Collection<Any>): List<T?> {
        if (ids.none()) {
            return emptyList()
        }
        val objectsById = ids
                .map { g.V(it) }
                .reduce { first, second -> first.union(second) }
                .map { vertex -> vertex.get().id() to vertex.get().vertexMapper<T>().inverseMap(vertex.get()) }
                .asSequence()
                .associate { it }
        return ids.map { id ->
            val obj = objectsById[id]
            if (obj == null) logger.debug("Unable to load object for id $id") else logger.debug("Loaded object for id $id")
            obj
        }
    }

    /**
     * Load all verticies of vertex type T. For this function, T may be a superclass of
     * classes registered as a vertex.
     */
    inline fun <reified T : Any> loadAll(): Iterable<T> = loadAll(T::class)

    /**
     * Load all verticies of vertex type T. For this function, T may be a superclass of
     * classes registered as a vertex.
     */
    fun <T : Any> loadAll(kClass: KClass<T>): Iterable<T> {
        val labels = vertexObjectDescriptions.filterKeys { it.isSubclassOf(kClass) }.values.map { it.label }
        if (labels.isEmpty()) {
            throw UnregisteredClass(kClass)
        }
        logger.debug("Will load all vertices with labels $labels")
        val traversal = labels.map { g.V().hasLabel(it) }.reduce { first, second ->
            first.union(second)
        }
        return loadTraversal(traversal)
    }

    /**
     * This function loads an arbitrary traversal that ends at a vertex that can be mapped to T
     */
    fun <T : Any> loadTraversal(traversal: GraphTraversal<*, Vertex>): Iterable<T> {
        return Iterable {
            traversal.map { vertex -> vertex.get().vertexMapper<T>().inverseMap(vertex.get()) }
        }
    }

    /**
     * Save Vertex
     */

    /**
     * Saves a objects annotated with @Vertex to the graph. If property annotated with @ID is null,
     * a new vertex will be created, otherwise this object will overwrite the current vertex with that id.
     * The returned object will always have a non-null @ID
     */
    fun <T : Any> saveV(vararg objs: T): List<T> = saveV(objs.asList())

    /**
     * Saves an object annotated with @Vertex to the graph. If property annotated with @ID is null,
     * a new vertex will be created, otherwise this object will overwrite the current vertex with that id.
     * The returned object will always have a non-null @ID
     */
    fun <T : Any> saveV(obj: T): T = saveV(listOf(obj)).single()

    /**
     * Saves a objects annotated with @Vertex to the graph. If property annotated with @ID is null,
     * a new vertex will be created, otherwise this object will overwrite the current vertex with that id.
     * The returned object will always have a non-null @ID
     */
    fun <T : Any> saveV(objs: List<T>): List<T> = objs.map { obj ->
        val mapper = obj.vertexMapper()
        val vertex = mapper.forwardMap(obj)
        logger.debug("Saved vertex with id ${vertex.id()}")
        mapper.inverseMap(vertex)
    }

    /**
     * Traverse Edges
     */

    /**
     * Traverses from a single object to the path's required destination vertex.
     */
    fun <FROM : Any, TO> traverse(boundStep: SingleBoundPath.ToSingle<FROM, TO>): TO =
            traverse(boundStep.froms, boundStep.path)[boundStep.from]!!.single()

    /**
     * Traverses from a single object to the path's optional destination vertex.
     */
    fun <FROM : Any, TO> traverse(boundStep: SingleBoundPath.ToOptional<FROM, TO>): TO? =
            traverse(boundStep.froms, boundStep.path)[boundStep.from]!!.singleOrNull()

    /**
     * Traverses from a single object to the path's destination vertices.
     */
    fun <FROM : Any, TO> traverse(boundStep: SingleBoundPath.ToMany<FROM, TO>): List<TO> =
            traverse(boundStep.froms, boundStep.path)[boundStep.from]!!

    /**
     * Traverses from multiple objects to the path's required destination vertex for each origin object.
     */
    fun <FROM : Any, TO> traverse(boundStep: MultiBoundPath.ToSingle<FROM, TO>): Map<FROM, TO> =
            traverse(boundStep.froms, boundStep.path).entries.associate { it.key to it.value.single() }

    /**
     * Traverses from multiple objects to the path's optional destination vertex for each origin object.
     */
    fun <FROM : Any, TO> traverse(boundStep: MultiBoundPath.ToOptional<FROM, TO>): Map<FROM, TO?> =
            traverse(boundStep.froms, boundStep.path).entries.associate { it.key to it.value.singleOrNull() }

    /**
     * Traverses from multiple objects to the path's destination vertices for each origin object.
     */
    fun <FROM : Any, TO> traverse(boundStep: MultiBoundPath.ToMany<FROM, TO>): Map<FROM, List<TO>> =
            traverse(boundStep.froms, boundStep.path)

    private fun <FROM : Any, TO> traverse(
            froms: Iterable<FROM>,
            path: Path<FROM, TO>
    ): Map<FROM, List<TO>> {
        if (froms.none()) {
            return emptyMap()
        }
        val traversalStart = froms.fold(initial = g.inject<FROM>()) { traversal, from ->
            traversal.inject(from).`as`(fromKey)
        }
        @Suppress("UNCHECKED_CAST")
        val traversed = path.path().fold(initial = traversalStart as GraphTraversal<Any, Any>) { traversal, step ->
            step as Path<Any, Any>
            step(StepTraverser(traversal, genericMapper)) as GraphTraversal<Any, Any>
        }
        @Suppress("UNCHECKED_CAST")
        return traversed.`as`(toKey).select<Any>(fromKey, toKey).toMultiMap(froms) {
            val from = it[fromKey] as FROM
            val to = it[toKey] as TO
            logger.debug("Traversed from $from to $to")
            from to to
        }
    }

    /**
     * Save Edge
     */

    /**
     * Saves edges to the graph
     */
    fun <FROM : Any, TO : Any> saveE(edges: Iterable<Edge<FROM, TO>>) = edges.forEach { edge ->
        saveE(edge.from, edge.to, edge.relationship)
    }

    /**
     * Saves an edge to the graph. This is a no-op if the edge already exists.
     */
    fun <FROM : Any, TO : Any> saveE(edge: Edge<FROM, TO>) = saveE(edge.from, edge.to, edge.relationship)

    /**
     * Saves an edge to the graph. This is a no-op if the edge already exists.
     */
    private fun <FROM : Any, TO : Any> saveE(from: FROM, to: TO, relationship: Relationship<FROM, TO>) {
        val fromID = from.vertexMapper().vertexObjectDescription.id.property.get(from) ?: throw ObjectNotSaved(from)
        val toID = to.vertexMapper().vertexObjectDescription.id.property.get(to) ?: throw ObjectNotSaved(to)
        val existingEdge = g.V(fromID).out(relationship).hasId(toID)
        val conflictingFrom = if (relationship is Relationship.ToOne) g.V(fromID).out(relationship) else null
        val conflictingTo = if (relationship is Relationship.FromOne) g.V(toID).`in`(relationship) else null
        val createEdge = when (relationship.direction) {
            Relationship.Direction.BACKWARD ->
                g.V(toID)
                        .let { if (conflictingFrom == null) it else it.not(conflictingFrom) }
                        .let { if (conflictingTo == null) it else it.not(conflictingTo) }
                        .addE(relationship.name).to(g.V(fromID)).outV()
            else ->
                g.V(fromID)
                        .let { if (conflictingFrom == null) it else it.not(conflictingFrom) }
                        .let { if (conflictingTo == null) it else it.not(conflictingTo) }
                        .addE(relationship.name).to(g.V(toID)).inV()
        }
        val createOrGetEdge = g.V().coalesce(existingEdge.sideEffect {
            logger.debug("Edge ${relationship.name} from $from to $to already exists. Save edge is a no-op.")
        }, createEdge.sideEffect {
            logger.debug("Creating edge ${relationship.name} from $from to $to.")
        })
        if (!createOrGetEdge.hasNext()) throw ConflictingEdge(from, to, relationship.name)
    }

    private fun GraphTraversal<*, Vertex>.out(relationship: Relationship<*, *>): GraphTraversal<*, Vertex> =
            when (relationship.direction) {
                Relationship.Direction.FORWARD -> out(relationship.name)
                Relationship.Direction.BACKWARD -> `in`(relationship.name)
                null -> both(relationship.name)
            }

    private fun GraphTraversal<*, Vertex>.`in`(relationship: Relationship<*, *>): GraphTraversal<*, Vertex> =
            when (relationship.direction) {
                Relationship.Direction.FORWARD -> `in`(relationship.name)
                Relationship.Direction.BACKWARD -> out(relationship.name)
                null -> both(relationship.name)
            }

    private fun <T : Any> Vertex.vertexMapper(): VertexMapper<T> {
        @Suppress("UNCHECKED_CAST")
        val vertexDescription = vertexObjectDescriptionsByLabel[label()] as VertexObjectDescription<T>?
                ?: throw UnregisteredLabel(this)
        return VertexMapper(g, vertexDescription)
    }

    private fun <T : Any> T.vertexMapper(): VertexMapper<T> {
        @Suppress("UNCHECKED_CAST")
        val vertexDescription = vertexObjectDescriptions[this::class] as VertexObjectDescription<T>?
                ?: throw UnregisteredClass(this)
        return VertexMapper(g, vertexDescription)
    }

    private fun <T : Any> KClass<T>.nestedObjectMapper(): ObjectMapper<T>? {
        @Suppress("UNCHECKED_CAST")
        val nestedObjectDescription = nestedObjectDescriptions[this] as ObjectDescription<T>? ?: return null
        return ObjectMapper(nestedObjectDescription)
    }

    private fun <T : Any> KClass<T>.scalarMapper(): PropertyBiMapper<T, SerializedProperty>? {
        @Suppress("UNCHECKED_CAST")
        return (scalarMappers[this] ?: defaultPropertyMappers[this]) as PropertyBiMapper<T, SerializedProperty>?
    }

    private inner class VertexMapper<T : Any> private constructor(
            val vertexObjectDescription: VertexObjectDescription<T>,
            val vertexSerializer: VertexSerializer<T>,
            val vertexDeserializer: VertexDeserializer<T>
    ) : BiMapper<T, Vertex> {

        constructor(
                g: GraphTraversalSource,
                vertexObjectDescription: VertexObjectDescription<T>
        ) : this(
                vertexObjectDescription,
                VertexSerializer(g, vertexObjectDescription),
                VertexDeserializer(vertexObjectDescription)
        )

        override fun forwardMap(from: T): Vertex = vertexSerializer(from)
        override fun inverseMap(from: Vertex): T = vertexDeserializer(from)
    }

    private inner class VertexSerializer<in T : Any> private constructor(
            private val g: GraphTraversalSource,
            private val vertexObjectDescription: VertexObjectDescription<T>,
            private val objectSerializer: ObjectSerializer<T>
    ) : Mapper<T, Vertex> {

        constructor(
                g: GraphTraversalSource,
                vertexObjectDescription: VertexObjectDescription<T>
        ) : this(
                g,
                vertexObjectDescription,
                ObjectSerializer(vertexObjectDescription)
        )

        override fun invoke(from: T): Vertex {
            val unserializedId = vertexObjectDescription.id.property.get(from)
            val idMapper = PropertyMapper(vertexObjectDescription.id)
            val serializedId = idMapper.forwardMap(unserializedId)
            val traversal = when (serializedId) {
                null -> g.addV(vertexObjectDescription.label)
                else -> g.V(serializedId)
            }
            return traversal.map { vertex ->
                val serializedProperties = objectSerializer(from)
                vertex.get().setProperties(serializedProperties)
            }.toList().single()
        }
    }

    private inner class VertexDeserializer<out T : Any> private constructor(
            private val objectDeserializer: ObjectDeserializer<T>
    ) : Mapper<Vertex, T> {

        constructor(vertexObjectDescription: VertexObjectDescription<T>)
                : this(ObjectDeserializer(vertexObjectDescription, Pair(idTag, vertexObjectDescription.id)))

        override fun invoke(from: Vertex): T {
            val serializedProperties = from.getProperties() + Pair(idTag, from.id())
            return objectDeserializer(serializedProperties)
        }
    }

    private inner class ObjectMapper<T : Any> private constructor(
            private val serializer: ObjectSerializer<T>,
            private val deserializer: ObjectDeserializer<T>
    ) : BiMapper<T, Map<String, SerializedProperty?>> {

        constructor(objectDescription: ObjectDescription<T>) :
                this(ObjectSerializer(objectDescription), ObjectDeserializer(objectDescription))

        override fun forwardMap(from: T): Map<String, SerializedProperty?> = serializer(from)
        override fun inverseMap(from: Map<String, SerializedProperty?>): T = deserializer(from)
    }

    private inner class ObjectSerializer<in T : Any>(
            private val objectDescription: ObjectDescription<T>
    ) : Mapper<T, Map<String, SerializedProperty?>> {

        override fun invoke(from: T): Map<String, SerializedProperty?> =
                objectDescription.properties.mapValues { keyValue ->
                    val propertyDescription = keyValue.value
                    val unserializedPropertyValue = propertyDescription.property.get(from)
                    val mapper = PropertyMapper(propertyDescription)
                    mapper.forwardMap(unserializedPropertyValue)
                }
    }

    private inner class ObjectDeserializer<out T : Any>(
            private val objectDescription: ObjectDescription<T>,
            private val idProperty: Pair<String, PropertyDescription<T>>? = null
    ) : Mapper<Map<String, SerializedProperty?>, T> {

        private val properties: Map<String, PropertyDescription<T>> get() = objectDescription.properties.let { if (idProperty != null) it + idProperty else it }
        private val otherConstructorParameters: Map<KParameter, Any?> get() = objectDescription.nullConstructorParameters.associate { it to null }

        override fun invoke(from: Map<String, SerializedProperty?>): T {
            val constructorParameters = properties.entries.associate { keyValue ->
                val propertyKey = keyValue.key
                val propertyDescription = keyValue.value
                val serializedPropertyValue = from[propertyKey]
                val mapper = PropertyMapper(propertyDescription)
                val deserializedPropertyValue = mapper.inverseMap(serializedPropertyValue)
                propertyDescription.parameter to deserializedPropertyValue
            }
            return objectDescription.constructor.callBy(constructorParameters + otherConstructorParameters)
        }
    }

    private inner class PropertyMapper<T> private constructor(
            private val propertySerializer: PropertySerializer<T>,
            private val propertyDeserializer: PropertyDeserializer<T>
    ) : BiMapper<Any?, SerializedProperty?> {

        constructor(propertyDescription: PropertyDescription<T>)
                : this(PropertySerializer(propertyDescription), PropertyDeserializer(propertyDescription))

        override fun forwardMap(from: Any?): SerializedProperty? = propertySerializer(from)
        override fun inverseMap(from: SerializedProperty?): Any? = propertyDeserializer(from)
    }

    private inner class PropertySerializer<T>(
            private val propertyDescription: PropertyDescription<T>
    ) : Mapper<Any?, SerializedProperty?> {

        override fun invoke(from: Any?): SerializedProperty? {
            if (from == null) {
                return null
            }
            if (propertyDescription.mapper != null) {
                return propertyDescription.mapper.forwardMap(from)
            }
            return when (from) {
                is Iterable<*> -> from.map {
                    @Suppress("UNCHECKED_CAST")
                    val fromClass = propertyDescription.property.returnType.arguments.single().type?.classifier as? KClass<Any>
                            ?: throw IncompatibleIterable(propertyDescription)
                    fromClass.serialize(it)
                }
                is Map<*, *> -> {
                    val mapTypeParameters = propertyDescription.property.returnType.arguments
                    @Suppress("UNCHECKED_CAST")
                    val keyClass = mapTypeParameters.first().type?.classifier as? KClass<Any>
                            ?: throw IncompatibleMap(propertyDescription)
                    @Suppress("UNCHECKED_CAST")
                    val valueClass = mapTypeParameters.last().type?.classifier as? KClass<Any>
                            ?: throw IncompatibleMap(propertyDescription)
                    from.entries.associate {
                        keyClass.serialize(it.key) to valueClass.serialize(it.value)
                    }
                }
                else -> {
                    @Suppress("UNCHECKED_CAST")
                    val kClass = propertyDescription.kClass as KClass<Any>
                    kClass.serialize(from)
                }
            }
        }

        private fun KClass<Any>.serialize(property: Any?): SerializedProperty? {
            if (property == null) {
                return null
            }
            @Suppress("UNCHECKED_CAST")
            return this.scalarMapper()?.forwardMap(property)
                    ?: this.nestedObjectMapper()?.forwardMap(property)
                    ?: throw ObjectSerializerMissing(property)
        }
    }

    private inner class PropertyDeserializer<T>(
            private val propertyDescription: PropertyDescription<T>
    ) : Mapper<SerializedProperty?, Any?> {

        override fun invoke(from: SerializedProperty?): Any? {
            @Suppress("UNCHECKED_CAST")
            val objectClass = propertyDescription.kClass as KClass<Any>
            if (from == null) {
                return if (objectClass.isSubclassOf(Map::class)) emptyMap<Any, Any>() else null
            }
            if (propertyDescription.mapper != null) {
                return propertyDescription.mapper.inverseMap(from)
            }
            return when (from) {
                is Iterable<*> -> {
                    @Suppress("UNCHECKED_CAST")
                    val toClass = propertyDescription.property.returnType.arguments.single().type?.classifier as? KClass<Any>
                            ?: throw IncompatibleIterable(propertyDescription)
                    when {
                        objectClass.isSubclassOf(Set::class) -> from.map { toClass.deserialize(it) }.toSet()
                        objectClass.isSubclassOf(List::class) -> from.map { toClass.deserialize(it) }
                        else -> throw IterableNotSupported(objectClass)
                    }
                }
                is Map<*, *> -> {
                    @Suppress("UNCHECKED_CAST")
                    from as Map<String, SerializedProperty?>
                    if (objectClass.isSubclassOf(Map::class)) {
                        val mapTypeParameters = propertyDescription.property.returnType.arguments
                        @Suppress("UNCHECKED_CAST")
                        val keyClass = mapTypeParameters.first().type?.classifier as? KClass<Any>
                                ?: throw IncompatibleMap(propertyDescription)
                        @Suppress("UNCHECKED_CAST")
                        val valueClass = mapTypeParameters.last().type?.classifier as? KClass<Any>
                                ?: throw IncompatibleMap(propertyDescription)
                        from.entries.associate { keyClass.deserialize(it.key) to valueClass.deserialize(it.value) }
                    } else {
                        objectClass.deserialize(from)
                    }
                }
                else -> objectClass.deserialize(from)
            }
        }

        private fun KClass<Any>.deserialize(property: SerializedProperty?): Any? = when (property) {
            null -> null
            is Map<*, *> -> {
                @Suppress("UNCHECKED_CAST")
                property as Map<String, SerializedProperty?>
                nestedObjectMapper()?.inverseMap(property) ?: throw ObjectDeserializerMissing(property, this)
            }
            else -> {
                val sm = scalarMapper()
                sm?.inverseMap(property) ?: throw PropertyDeserializerMissing(property, this)
            }
        }
    }

    companion object {

        /**
         * This is a reserved property key used to mark the property annotated with @ID.
         * This means Clients may not use @Property(name = "id")
         */
        internal const val idTag = "id"

        private const val fromKey = "from"

        private const val toKey = "to"

        private val logger = LoggerFactory.getLogger(GraphMapper::class.java)

        private val defaultPropertyMappers = mapOf<KClass<*>, PropertyBiMapper<*, *>>(
                String::class to StringPropertyMapper,
                Byte::class to BytePropertyMapper,
                Float::class to FloatPropertyMapper,
                Double::class to DoublePropertyManager,
                Long::class to LongPropertyMapper,
                Int::class to IntegerPropertyMapper,
                Boolean::class to BooleanPropertyMapper,
                Instant::class to InstantPropertyMapper,
                UUID::class to UUIDPropertyMapper)
    }
}
