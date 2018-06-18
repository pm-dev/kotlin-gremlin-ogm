package org.apache.tinkerpop.gremlin.ogm

import org.apache.tinkerpop.gremlin.ogm.elements.BasicEdge
import org.apache.tinkerpop.gremlin.ogm.elements.Edge
import org.apache.tinkerpop.gremlin.ogm.elements.Vertex
import org.apache.tinkerpop.gremlin.ogm.exceptions.*
import org.apache.tinkerpop.gremlin.ogm.extensions.filterNullValues
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
import org.apache.tinkerpop.gremlin.ogm.paths.Path
import org.apache.tinkerpop.gremlin.ogm.paths.relationships.Relationship
import org.apache.tinkerpop.gremlin.ogm.paths.steps.StepTraverser
import org.apache.tinkerpop.gremlin.ogm.reflection.*
import org.apache.tinkerpop.gremlin.process.traversal.dsl.graph.GraphTraversal
import org.apache.tinkerpop.gremlin.process.traversal.dsl.graph.GraphTraversalSource
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
 * Remember to register classes annotated with @Element using the 'vertexClasses' parameter as this
 * library does not scan for those objects automatically.
 */
open class GraphMapper(
        val g: GraphTraversalSource,
        vertices: Set<KClass<out Vertex>>,
        relationships: Map<Relationship<out Vertex, out Vertex>, KClass<out Edge<Vertex, Vertex>>?> = mapOf(),
        nestedObjects: Set<KClass<*>> = setOf(),
        private val scalarMappers: Map<KClass<*>, PropertyBiMapper<*, *>> = mapOf()
) {

    private val vertexDescriptions: Map<KClass<out Vertex>, VertexDescription<out Vertex>> = vertices.associate { it to VertexDescription(it) }

    private val edgeDescriptions: Map<KClass<out Edge<Vertex, Vertex>>, EdgeDescription<Vertex, Vertex, out Edge<Vertex, Vertex>>> =
            relationships.filterNullValues().entries.associate { it.value to EdgeDescription(it.key, it.value) }

    private val relationshipsByName: Map<String, Relationship<out Vertex, out Vertex>> = relationships.keys.associateBy { it.name }

    private val nestedObjectDescriptions: Map<KClass<*>, NestedObjectDescription<*>> = nestedObjects.associate { it to NestedObjectDescription(it) }

    private val vertexDescriptionsByLabel: Map<String, VertexDescription<*>> = vertexDescriptions.mapKeys { it.value.label }

    private val edgeDescriptionsByLabel: Map<String, EdgeDescription<*, *, *>> = edgeDescriptions.mapKeys { it.value.label }

    private val edgeMapper = object : BiMapper<Edge<Vertex, Vertex>, org.apache.tinkerpop.gremlin.structure.Edge> {
        override fun forwardMap(from: Edge<Vertex, Vertex>): org.apache.tinkerpop.gremlin.structure.Edge = from.edgeMapper().forwardMap(from)
        override fun inverseMap(from: org.apache.tinkerpop.gremlin.structure.Edge): Edge<Vertex, Vertex> = from.edgeMapper<Vertex, Vertex, Edge<Vertex, Vertex>>().inverseMap(from)
    }

    private val vertexMapper = object : BiMapper<Vertex, org.apache.tinkerpop.gremlin.structure.Vertex> {
        override fun forwardMap(from: Vertex): org.apache.tinkerpop.gremlin.structure.Vertex = from.vertexMapper().forwardMap(from)
        override fun inverseMap(from: org.apache.tinkerpop.gremlin.structure.Vertex): Vertex = from.vertexMapper<Vertex>().inverseMap(from)
    }

    /**
     * Gets a graph traversal that emits vertices for given ids.
     * No exception is thrown for ids that don't correspond to a vertex, thus the number of vertices the traversal emits
     * may be less than the number of ids.
     */
    fun <V : Vertex> V(ids: Collection<Any>): GraphTraversal<*, V> {
        if (ids.isEmpty()) {
            return g.inject<V>()
        }
        return ids
                .map { id ->
                    g.V(id)
                }
                .reduce { traversal1, traversal2 ->
                    traversal1.union(traversal2)
                }
                .map { vertex ->
                    vertex.get().vertexMapper<V>().inverseMap(vertex.get())
                }
    }

    /**
     * Get a traversal that emits all vertices for class V (which has been registered as a vertex with this GraphMapper).
     * V may be a superclass of classes registered as a vertex.
     */
    fun <V : Vertex> V(kClass: KClass<V>): GraphTraversal<*, V> {
        val labels = vertexDescriptions.filterKeys { vertexKClass ->
            vertexKClass.isSubclassOf(kClass)
        }.values.map { vertexObjectDescription ->
            vertexObjectDescription.label
        }
        if (labels.isEmpty()) throw UnregisteredClass(kClass)
        logger.debug("Will get all vertices with labels $labels")
        return labels.map { label ->
            g.V().hasLabel(label)
        }.reduce { traversal1, traversal2 ->
            g.V().union(traversal1, traversal2)
        }.map { vertex ->
            vertex.get().vertexMapper<V>().inverseMap(vertex.get())
        }
    }

    /**
     * Gets a graph traversal that emits edges for given ids.
     * No exception is thrown for ids that don't correspond to an edge, thus the number of edges the traversal emits
     * may be less than the number of ids.
     */
    fun <FROM : Vertex, TO : Vertex, E : Edge<FROM, TO>> E(ids: Collection<Any>): GraphTraversal<*, E> {
        if (ids.isEmpty()) {
            return g.inject<E>()
        }
        return ids
                .map { id ->
                    g.E(id)
                }
                .reduce { traversal1, traversal2 ->
                    traversal1.union(traversal2)
                }
                .map { edge ->
                    edge.get().edgeMapper<FROM, TO, E>().inverseMap(edge.get())
                }
    }

    /**
     * Get a traversal that emits all edges for class E (which has been registered with a relationship as
     * an edge with this GraphMapper).
     */
    fun <FROM : Vertex, TO : Vertex, E : Edge<FROM, TO>> E(kClass: KClass<E>): GraphTraversal<*, E> {
        val edgeDescription = edgeDescriptions[kClass] ?: throw UnregisteredClass(kClass)
        logger.debug("Will get all edges with label ${edgeDescription.label}")
        return g.E()
                .hasLabel(edgeDescription.label)
                .map { vertex ->
                    vertex.get().edgeMapper<FROM, TO, E>().inverseMap(vertex.get())
                }
    }

    /**
     * Saves an object annotated with @Element to the graph. If property annotated with @ID is null,
     * a new vertex will be created, otherwise this object will overwrite the current vertex with that id.
     * The returned object will always have a non-null @ID. If the property annotated with @ID is non-null,
     * but the vertex cannot be found, an exception is thrown.
     */
    fun <V : Vertex> saveV(obj: V): V {
        val mapper = obj.vertexMapper()
        val vertex = mapper.forwardMap(obj)
        logger.debug("Saved vertex with id ${vertex.id()}")
        return mapper.inverseMap(vertex)
    }

    /**
     * Saves edges to the graph. If the property annotated with @ID is null,
     * a new edge will be created, otherwise this object will overwrite the current edge with that id.
     * The returned object will always have a non-null @ID.
     */
    fun <FROM : Vertex, TO : Vertex, E : Edge<FROM, TO>> saveE(edge: E): E {
        val mapper = edge.edgeMapper()
        val serialized = mapper.forwardMap(edge)
        logger.debug("Saved edge with id ${serialized.id()}")
        return mapper.inverseMap(serialized)
    }

    fun <FROM : Vertex, TO> traverse(froms: Iterable<FROM>, path: Path<FROM, TO>): Map<FROM, List<TO>> {
        if (froms.none()) {
            return emptyMap()
        }
        val traversalStart = froms.fold(initial = g.inject<FROM>()) { traversal, from ->
            traversal.inject(from).`as`(fromKey)
        }
        @Suppress("UNCHECKED_CAST")
        val traversed = path.path().fold(initial = traversalStart as GraphTraversal<Any, Any>) { traversal, step ->
            step as Path<Any, Any>
            step(StepTraverser(traversal, vertexMapper, edgeMapper)) as GraphTraversal<Any, Any>
        }
        @Suppress("UNCHECKED_CAST")
        return traversed.`as`(toKey).select<Any>(fromKey, toKey).toMultiMap(froms) {
            val from = it[fromKey] as FROM
            val to = it[toKey] as TO
            logger.debug("Traversed from $from to $to")
            from to to
        }
    }

    private fun <FROM : Vertex, TO : Vertex, E : Edge<FROM, TO>> org.apache.tinkerpop.gremlin.structure.Edge.edgeMapper(): EdgeMapper<FROM, TO, E> {
        @Suppress("UNCHECKED_CAST")
        val edgeDescription = edgeDescriptionsByLabel[label()] as EdgeDescription<FROM, TO, E>?
        @Suppress("UNCHECKED_CAST")
        val fromVertexDescription = vertexDescriptionsByLabel[outVertex().label()] as VertexDescription<FROM>?
                ?: throw UnregisteredLabel(outVertex())
        @Suppress("UNCHECKED_CAST")
        val toVertexDescription = vertexDescriptionsByLabel[inVertex().label()] as VertexDescription<TO>?
                ?: throw UnregisteredLabel(inVertex())
        return EdgeMapper(g, edgeDescription, fromVertexDescription, toVertexDescription)
    }

    private fun <FROM : Vertex, TO : Vertex, E : Edge<FROM, TO>> E.edgeMapper(): EdgeMapper<FROM, TO, E> {
        @Suppress("UNCHECKED_CAST")
        val edgeDescription = edgeDescriptions[this::class] as EdgeDescription<FROM, TO, E>?
        @Suppress("UNCHECKED_CAST")
        val fromVertexDescription = vertexDescriptions[from::class] as VertexDescription<FROM>?
                ?: throw UnregisteredClass(from)
        @Suppress("UNCHECKED_CAST")
        val toVertexDescription = vertexDescriptions[to::class] as VertexDescription<TO>? ?: throw UnregisteredClass(to)
        return EdgeMapper(g, edgeDescription, fromVertexDescription, toVertexDescription)
    }

    private fun <T : Vertex> org.apache.tinkerpop.gremlin.structure.Vertex.vertexMapper(): VertexMapper<T> {
        @Suppress("UNCHECKED_CAST")
        val vertexDescription = vertexDescriptionsByLabel[label()] as VertexDescription<T>?
                ?: throw UnregisteredLabel(this)
        return VertexMapper(g, vertexDescription)
    }

    private fun <T : Vertex> T.vertexMapper(): VertexMapper<T> {
        @Suppress("UNCHECKED_CAST")
        val vertexDescription = vertexDescriptions[this::class] as VertexDescription<T>?
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

    private inner class EdgeMapper<FROM : Vertex, TO : Vertex, E : Edge<FROM, TO>> private constructor(
            val edgeSerializer: EdgeSerializer<FROM, TO, E>,
            val edgeDeserializer: EdgeDeserializer<FROM, TO, E>
    ) : BiMapper<E, org.apache.tinkerpop.gremlin.structure.Edge> {

        constructor(
                g: GraphTraversalSource,
                edgeDescription: EdgeDescription<FROM, TO, E>?,
                fromVertexDescription: VertexDescription<FROM>,
                toVertexDescription: VertexDescription<TO>
        ) : this(
                EdgeSerializer(g, edgeDescription, fromVertexDescription, toVertexDescription),
                EdgeDeserializer(edgeDescription, fromVertexDescription, toVertexDescription)
        )

        override fun forwardMap(from: E): org.apache.tinkerpop.gremlin.structure.Edge = edgeSerializer(from)
        override fun inverseMap(from: org.apache.tinkerpop.gremlin.structure.Edge): E = edgeDeserializer(from)
    }

    private inner class EdgeSerializer<FROM : Vertex, TO : Vertex, E : Edge<FROM, TO>> private constructor(
            private val g: GraphTraversalSource,
            private val objectSerializer: ObjectSerializer<E>?,
            private val fromVertexDescription: VertexDescription<FROM>,
            private val toVertexDescription: VertexDescription<TO>
    ) : Mapper<E, org.apache.tinkerpop.gremlin.structure.Edge> {

        constructor(
                g: GraphTraversalSource,
                edgeDescription: EdgeDescription<FROM, TO, E>?,
                fromVertexDescription: VertexDescription<FROM>,
                toVertexDescription: VertexDescription<TO>
        ) : this(
                g,
                edgeDescription?.let { ObjectSerializer(it) },
                fromVertexDescription,
                toVertexDescription)

        override fun invoke(from: E): org.apache.tinkerpop.gremlin.structure.Edge {
            val fromVertex = from.from
            val toVertex = from.to
            val relationship = edgeDescriptions[from::class]?.relationship ?: {
                if (from is BasicEdge<*, *>) from.relationship else null
            }() ?: throw UnregisteredClass(from::class)
            val fromID = fromVertexDescription.id.property.get(fromVertex) ?: throw ObjectNotSaved(fromVertex)
            val toID = toVertexDescription.id.property.get(toVertex) ?: throw ObjectNotSaved(toVertex)
            val existingEdge = ((g.V(fromID) out relationship).hasId(toID) `in` relationship).hasId(fromID) outE relationship
            val conflictingFrom = if (relationship is Relationship.ToOne) g.V(fromID) outE relationship else null
            val conflictingTo = if (relationship is Relationship.FromOne) g.V(toID) inE relationship else null
            val createEdge = when (relationship.direction) {
                Relationship.Direction.BACKWARD -> g.V(toID)
                        .let { if (conflictingFrom == null) it else it.not(conflictingFrom) }
                        .let { if (conflictingTo == null) it else it.not(conflictingTo) }
                        .addE(relationship.name).to(g.V(fromID))
                else -> g.V(fromID)
                        .let { if (conflictingFrom == null) it else it.not(conflictingFrom) }
                        .let { if (conflictingTo == null) it else it.not(conflictingTo) }
                        .addE(relationship.name).to(g.V(toID))
            }
            val createOrGetEdge = g.inject<Any>(0).coalesce(existingEdge.sideEffect {
                logger.debug("Updating edge ${relationship.name} from $fromVertex to $toVertex.")
            }, createEdge.sideEffect {
                logger.debug("Creating edge ${relationship.name} from $fromVertex to $toVertex.")
            }).map { edge ->
                objectSerializer?.let {
                    val serializedProperties = it(from)
                    edge.get().setProperties(serializedProperties)
                } ?: edge.get()
            }
            if (!createOrGetEdge.hasNext()) throw ConflictingEdge(fromVertex, toVertex, relationship.name)
            return createOrGetEdge.toList().single()
        }

        private infix fun GraphTraversal<*, org.apache.tinkerpop.gremlin.structure.Vertex>.out(relationship: Relationship<*, *>): GraphTraversal<*, org.apache.tinkerpop.gremlin.structure.Vertex> =
                when (relationship.direction) {
                    Relationship.Direction.FORWARD -> out(relationship.name)
                    Relationship.Direction.BACKWARD -> `in`(relationship.name)
                    null -> both(relationship.name)
                }

        private infix fun GraphTraversal<*, org.apache.tinkerpop.gremlin.structure.Vertex>.`in`(relationship: Relationship<*, *>): GraphTraversal<*, org.apache.tinkerpop.gremlin.structure.Vertex> =
                when (relationship.direction) {
                    Relationship.Direction.FORWARD -> `in`(relationship.name)
                    Relationship.Direction.BACKWARD -> out(relationship.name)
                    null -> both(relationship.name)
                }

        private infix fun GraphTraversal<*, org.apache.tinkerpop.gremlin.structure.Vertex>.outE(relationship: Relationship<*, *>): GraphTraversal<*, org.apache.tinkerpop.gremlin.structure.Edge> =
                when (relationship.direction) {
                    Relationship.Direction.FORWARD -> outE(relationship.name)
                    Relationship.Direction.BACKWARD -> inE(relationship.name)
                    null -> bothE(relationship.name)
                }

        private infix fun GraphTraversal<*, org.apache.tinkerpop.gremlin.structure.Vertex>.inE(relationship: Relationship<*, *>): GraphTraversal<*, org.apache.tinkerpop.gremlin.structure.Edge> =
                when (relationship.direction) {
                    Relationship.Direction.FORWARD -> inE(relationship.name)
                    Relationship.Direction.BACKWARD -> outE(relationship.name)
                    null -> bothE(relationship.name)
                }
    }

    private inner class EdgeDeserializer<FROM : Vertex, TO : Vertex, E : Edge<FROM, TO>> private constructor(
            private val objectDeserializer: ObjectDeserializer<E>?,
            private val fromVertexDeserializer: VertexDeserializer<FROM>,
            private val toVertexDeserializer: VertexDeserializer<TO>
    ) : Mapper<org.apache.tinkerpop.gremlin.structure.Edge, E> {

        constructor(
                edgeDescription: EdgeDescription<FROM, TO, E>?,
                fromVertexDescription: VertexDescription<FROM>,
                toVertexDescription: VertexDescription<TO>
        ) : this(
                edgeDescription?.let {
                    ObjectDeserializer(
                            edgeDescription,
                            idTag to edgeDescription.id,
                            toVertexTag to edgeDescription.toVertex,
                            fromVertexTag to edgeDescription.fromVertex)
                },
                VertexDeserializer(fromVertexDescription),
                VertexDeserializer(toVertexDescription))

        override fun invoke(from: org.apache.tinkerpop.gremlin.structure.Edge): E {
            val toVertex = toVertexDeserializer(from.inVertex())
            val fromVertex = fromVertexDeserializer(from.outVertex())
            return objectDeserializer?.let {
                val serializedProperties = from.getProperties() +
                        (idTag to from.id()) +
                        (toVertexTag to toVertex) +
                        (fromVertexTag to fromVertex)
                return it(serializedProperties)
            } ?: {
                @Suppress("UNCHECKED_CAST")
                val relationship = relationshipsByName[from.label()] as Relationship<FROM, TO>?
                        ?: throw UnregisteredLabel(from)
                @Suppress("UNCHECKED_CAST")
                BasicEdge(fromVertex, toVertex, relationship) as E
            }()
        }
    }

    private inner class VertexMapper<T : Vertex> private constructor(
            val vertexSerializer: VertexSerializer<T>,
            val vertexDeserializer: VertexDeserializer<T>
    ) : BiMapper<T, org.apache.tinkerpop.gremlin.structure.Vertex> {

        constructor(
                g: GraphTraversalSource,
                vertexDescription: VertexDescription<T>
        ) : this(
                VertexSerializer(g, vertexDescription),
                VertexDeserializer(vertexDescription)
        )

        override fun forwardMap(from: T): org.apache.tinkerpop.gremlin.structure.Vertex = vertexSerializer(from)
        override fun inverseMap(from: org.apache.tinkerpop.gremlin.structure.Vertex): T = vertexDeserializer(from)
    }

    private inner class VertexSerializer<in T : Vertex> private constructor(
            private val g: GraphTraversalSource,
            private val vertexDescription: VertexDescription<T>,
            private val objectSerializer: ObjectSerializer<T>
    ) : Mapper<T, org.apache.tinkerpop.gremlin.structure.Vertex> {

        constructor(
                g: GraphTraversalSource,
                vertexDescription: VertexDescription<T>
        ) : this(
                g,
                vertexDescription,
                ObjectSerializer(vertexDescription)
        )

        override fun invoke(from: T): org.apache.tinkerpop.gremlin.structure.Vertex {
            val id = vertexDescription.id.property.get(from)
            val traversal = when (id) {
                null -> g.addV(vertexDescription.label)
                else -> g.V(id)
            }
            return traversal.map { vertex ->
                val serializedProperties = objectSerializer(from)
                vertex.get().setProperties(serializedProperties)
            }.toList().singleOrNull() ?: throw IDNotFound(from, id)
        }
    }

    private inner class VertexDeserializer<out T : Vertex> private constructor(
            private val objectDeserializer: ObjectDeserializer<T>
    ) : Mapper<org.apache.tinkerpop.gremlin.structure.Vertex, T> {

        constructor(vertexDescription: VertexDescription<T>)
                : this(ObjectDeserializer(vertexDescription, Pair(idTag, vertexDescription.id)))

        override fun invoke(from: org.apache.tinkerpop.gremlin.structure.Vertex): T {
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
            private val idProperty: Pair<String, PropertyDescription<T, *>>? = null,
            private val fromVertexParameter: Pair<String, KParameter>? = null,
            private val toVertexParameter: Pair<String, KParameter>? = null
    ) : Mapper<Map<String, SerializedProperty?>, T> {

        override fun invoke(from: Map<String, SerializedProperty?>): T {
            val constructorParameters = mutableMapOf<KParameter, Any?>()
            constructorParameters.putAll(objectDescription.properties.entries.associate { keyValue ->
                val propertyKey = keyValue.key
                val propertyDescription = keyValue.value
                val serializedPropertyValue = from[propertyKey]
                val mapper = PropertyMapper(propertyDescription)
                val deserializedPropertyValue = mapper.inverseMap(serializedPropertyValue)
                propertyDescription.parameter to deserializedPropertyValue
            })
            constructorParameters.putAll(objectDescription.nullConstructorParameters.associate { it to null })
            if (idProperty != null) {
                val id = from[idProperty.first]
                constructorParameters[idProperty.second.parameter] = id
            }
            if (fromVertexParameter != null) {
                val fromVertex = from[fromVertexParameter.first]
                constructorParameters[fromVertexParameter.second] = fromVertex
            }
            if (toVertexParameter != null) {
                val toVertex = from[toVertexParameter.first]
                constructorParameters[toVertexParameter.second] = toVertex
            }
            return objectDescription.constructor.callBy(constructorParameters)
        }
    }

    private inner class PropertyMapper<T> private constructor(
            private val propertySerializer: PropertySerializer<T>,
            private val propertyDeserializer: PropertyDeserializer<T>
    ) : BiMapper<Any?, SerializedProperty?> {

        constructor(propertyDescription: PropertyDescription<T, *>)
                : this(PropertySerializer(propertyDescription), PropertyDeserializer(propertyDescription))

        override fun forwardMap(from: Any?): SerializedProperty? = propertySerializer(from)
        override fun inverseMap(from: SerializedProperty?): Any? = propertyDeserializer(from)
    }

    private inner class PropertySerializer<T>(
            private val propertyDescription: PropertyDescription<T, *>
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
            private val propertyDescription: PropertyDescription<T, *>
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
        internal const val toVertexTag = "CE1C7396-A7D6-4584-98DA-B0E965A35034"
        internal const val fromVertexTag = "4A5F116C-B3BB-47AB-B1E3-7DBC24148BED"

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
