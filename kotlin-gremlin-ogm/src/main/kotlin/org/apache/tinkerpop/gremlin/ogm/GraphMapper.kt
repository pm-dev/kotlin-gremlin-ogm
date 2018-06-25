@file:Suppress("unused")

package org.apache.tinkerpop.gremlin.ogm

import org.apache.tinkerpop.gremlin.ogm.elements.BasicEdge
import org.apache.tinkerpop.gremlin.ogm.elements.Edge
import org.apache.tinkerpop.gremlin.ogm.elements.Vertex
import org.apache.tinkerpop.gremlin.ogm.exceptions.*
import org.apache.tinkerpop.gremlin.ogm.extensions.getProperties
import org.apache.tinkerpop.gremlin.ogm.extensions.setProperties
import org.apache.tinkerpop.gremlin.ogm.extensions.toMultiMap
import org.apache.tinkerpop.gremlin.ogm.mappers.BiMapper
import org.apache.tinkerpop.gremlin.ogm.mappers.Mapper
import org.apache.tinkerpop.gremlin.ogm.mappers.PropertyBiMapper
import org.apache.tinkerpop.gremlin.ogm.mappers.SerializedProperty
import org.apache.tinkerpop.gremlin.ogm.paths.Path
import org.apache.tinkerpop.gremlin.ogm.paths.relationships.Relationship
import org.apache.tinkerpop.gremlin.ogm.paths.steps.StepTraverser
import org.apache.tinkerpop.gremlin.ogm.reflection.*
import org.apache.tinkerpop.gremlin.process.traversal.dsl.graph.GraphTraversal
import org.apache.tinkerpop.gremlin.process.traversal.dsl.graph.GraphTraversalSource
import org.slf4j.LoggerFactory
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
        scalarMappers: Map<KClass<*>, PropertyBiMapper<*, *>> = mapOf()
) {

    private val graphDescription = GraphDescription(vertices, relationships, nestedObjects, scalarMappers)

    private val edgeMapper = object : BiMapper<Edge<Vertex, Vertex>, org.apache.tinkerpop.gremlin.structure.Edge> {
        override fun forwardMap(from: Edge<Vertex, Vertex>): org.apache.tinkerpop.gremlin.structure.Edge = serializeE(from)
        override fun inverseMap(from: org.apache.tinkerpop.gremlin.structure.Edge): Edge<Vertex, Vertex> = deserializeE(from)
    }

    private val vertexMapper = object : BiMapper<Vertex, org.apache.tinkerpop.gremlin.structure.Vertex> {
        override fun forwardMap(from: Vertex): org.apache.tinkerpop.gremlin.structure.Vertex = serializeV(from)
        override fun inverseMap(from: org.apache.tinkerpop.gremlin.structure.Vertex): Vertex = deserializeV(from)
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
                    deserializeV<V>(vertex.get())
                }
    }

    /**
     * Get a traversal that emits all vertices for class V (which has been registered as a vertex with this GraphMapper).
     * V may be a superclass of classes registered as a vertex.
     */
    fun <V : Vertex> V(kClass: KClass<V>): GraphTraversal<*, V> {
        val labels = graphDescription.vertexDescriptions.filterKeys { vertexKClass ->
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
            deserializeV<V>(vertex.get())
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
                    deserializeE<FROM, TO, E>(edge.get())
                }
    }

    /**
     * Get a traversal that emits all edges for class E (which has been registered with a relationship as
     * an edge with this GraphMapper).
     */
    fun <FROM : Vertex, TO : Vertex, E : Edge<FROM, TO>> E(kClass: KClass<E>): GraphTraversal<*, E> {
        val edgeDescription = graphDescription.edgeDescriptions[kClass] ?: throw UnregisteredClass(kClass)
        logger.debug("Will get all edges with label ${edgeDescription.label}")
        return g.E()
                .hasLabel(edgeDescription.label)
                .map { vertex ->
                    deserializeE<FROM, TO, E>(vertex.get())
                }
    }

    /**
     * Saves an object annotated with @Element to the graph. If property annotated with @ID is null,
     * a new vertex will be created, otherwise this object will overwrite the current vertex with that id.
     * The returned object will always have a non-null @ID. If the property annotated with @ID is non-null,
     * but the vertex cannot be found, an exception is thrown.
     */
    fun <V : Vertex> saveV(deserialized: V): V {
        val serialized = serializeV(deserialized)
        logger.debug("Saved vertex with id ${serialized.id()}")
        return deserializeV(serialized)
    }

    /**
     * Saves vertices to the graph. If the property annotated with @ID is null,
     * a new vertex will be created, otherwise this object will overwrite the current vertex with that id.
     * The returned object will always have a non-null @ID. If the property annotated with @ID is non-null,
     * but the vertex cannot be found, an exception is thrown.
     */
    fun <V : Vertex> saveV(objs: Iterable<V>): List<V> = objs.map { saveV(it) }

    /**
     * Saves edges to the graph. If the property annotated with @ID is null,
     * a new edge will be created, otherwise this object will overwrite the current edge with that id.
     * The returned object will always have a non-null @ID.
     */
    fun <FROM : Vertex, TO : Vertex, E : Edge<FROM, TO>> saveE(edge: E): E {
        val serialized = serializeE(edge)
        logger.debug("Saved edge with id ${serialized.id()}")
        return deserializeE(serialized)
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

    fun <FROM : Vertex, TO : Vertex, E : Edge<FROM, TO>> deserializeE(edge: org.apache.tinkerpop.gremlin.structure.Edge): E {
        val deserializer = EdgeDeserializer<FROM, TO, E>(
                graphDescription.getEdgeDescription(edge.label()),
                graphDescription.getVertexDescription(edge.outVertex().label())
                        ?: throw UnregisteredLabel(edge.outVertex()),
                graphDescription.getVertexDescription(edge.inVertex().label())
                        ?: throw UnregisteredLabel(edge.inVertex()))
        return deserializer(edge)
    }


    fun <FROM : Vertex, TO : Vertex, E : Edge<FROM, TO>> serializeE(edge: E): org.apache.tinkerpop.gremlin.structure.Edge {
        val serializer = EdgeSerializer(
                g,
                graphDescription.getEdgeDescription(edge::class),
                graphDescription.getVertexDescription(edge.from::class) ?: throw UnregisteredClass(edge.from),
                graphDescription.getVertexDescription(edge.to::class) ?: throw UnregisteredClass(edge.to))
        return serializer(edge)
    }

    fun <V : Vertex> serializeV(deserialized: V): org.apache.tinkerpop.gremlin.structure.Vertex {
        val serializer = VertexSerializer(g, graphDescription.getVertexDescription(deserialized::class)
                ?: throw UnregisteredClass(deserialized))
        return serializer(deserialized)
    }

    fun <V : Vertex> deserializeV(serialized: org.apache.tinkerpop.gremlin.structure.Vertex): V {
        val deserializer = VertexDeserializer(graphDescription.getVertexDescription<V>(serialized.label())
                ?: throw UnregisteredLabel(serialized))
        return deserializer(serialized)
    }

    private fun serializeProperty(property: Any?, deserializedClass: KClass<out Any>): SerializedProperty? {
        if (property == null) {
            return null
        }
        val serialized = getScalarPropertyBiMapper(deserializedClass)?.forwardMap(property)
        if (serialized != null) {
            return serialized
        }
        val description = graphDescription.getNestedObjectDescription(deserializedClass)
        if (description != null) {
            val serializer = ObjectSerializer(description)
            return serializer(property)
        }
        throw ObjectSerializerMissing(property)
    }

    private fun deserializeProperty(property: SerializedProperty?, deserializedClass: KClass<out Any>): Any? = when (property) {
        null -> null
        is Map<*, *> -> {
            val description = graphDescription.getNestedObjectDescription(deserializedClass)
                    ?: throw ObjectDeserializerMissing(property, deserializedClass)
            val deserializer = ObjectDeserializer(description)
            deserializer(property)
        }
        else -> getScalarPropertyBiMapper(deserializedClass)?.inverseMap(property)
                ?: throw PropertyDeserializerMissing(property, deserializedClass)
    }

    private fun <T : Any> getScalarPropertyBiMapper(deserializedClass: KClass<out T>): PropertyBiMapper<T, SerializedProperty>? =
            graphDescription.getScalarMapper(deserializedClass)
                    ?: graphDescription.getDefaultPropertyMapper(deserializedClass)

    private inner class EdgeSerializer<out FROM : Vertex, out TO : Vertex, in E : Edge<FROM, TO>>(
            private val g: GraphTraversalSource,
            private val edgeDescription: EdgeDescription<FROM, TO, E>?,
            private val fromVertexDescription: VertexDescription<FROM>,
            private val toVertexDescription: VertexDescription<TO>
    ) : Mapper<E, org.apache.tinkerpop.gremlin.structure.Edge> {

        override fun invoke(from: E): org.apache.tinkerpop.gremlin.structure.Edge {
            val objectSerializer = edgeDescription?.let { ObjectSerializer(it) }
            val fromVertex = from.from
            val toVertex = from.to
            val relationship = graphDescription.edgeDescriptions[from::class]?.relationship ?: {
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

    private inner class EdgeDeserializer<out FROM : Vertex, out TO : Vertex, out E : Edge<FROM, TO>>(
            private val edgeDescription: EdgeDescription<FROM, TO, E>?,
            private val fromVertexDescription: VertexDescription<FROM>,
            private val toVertexDescription: VertexDescription<TO>
    ) : Mapper<org.apache.tinkerpop.gremlin.structure.Edge, E> {

        override fun invoke(from: org.apache.tinkerpop.gremlin.structure.Edge): E {
            val objectDeserializer = edgeDescription?.let {
                ObjectDeserializer(
                        edgeDescription,
                        idTag to edgeDescription.id,
                        toVertexTag to edgeDescription.toVertex,
                        fromVertexTag to edgeDescription.fromVertex)
            }
            val fromVertexDeserializer = VertexDeserializer(fromVertexDescription)
            val toVertexDeserializer = VertexDeserializer(toVertexDescription)
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
                val relationship = graphDescription.relationshipsByName[from.label()] as Relationship<FROM, TO>?
                        ?: throw UnregisteredLabel(from)
                @Suppress("UNCHECKED_CAST")
                BasicEdge(fromVertex, toVertex, relationship) as E
            }()
        }
    }

    private inner class VertexSerializer<in T : Vertex>(
            private val g: GraphTraversalSource,
            private val vertexDescription: VertexDescription<T>
    ) : Mapper<T, org.apache.tinkerpop.gremlin.structure.Vertex> {

        override fun invoke(from: T): org.apache.tinkerpop.gremlin.structure.Vertex {
            val objectSerializer = ObjectSerializer(vertexDescription)
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

    private inner class VertexDeserializer<out T : Vertex>(
            vertexDescription: VertexDescription<T>
    ) : Mapper<org.apache.tinkerpop.gremlin.structure.Vertex, T> {

        val objectDeserializer = ObjectDeserializer(vertexDescription, Pair(idTag, vertexDescription.id))

        override fun invoke(from: org.apache.tinkerpop.gremlin.structure.Vertex): T {
            val serializedProperties = from.getProperties() + Pair(idTag, from.id())
            return objectDeserializer(serializedProperties)
        }
    }

    private inner class ObjectSerializer<in T : Any>(
            private val objectDescription: ObjectDescription<T>
    ) : Mapper<T, Map<*, *>> {

        override fun invoke(from: T): Map<String, SerializedProperty?> =
                objectDescription.properties.mapValues {
                    val propertyDescription = it.value
                    val unserializedPropertyValue = propertyDescription.property.get(from)
                    val serializer = PropertySerializer(propertyDescription)
                    serializer(unserializedPropertyValue)
                }
    }

    private inner class ObjectDeserializer<out T : Any>(
            private val objectDescription: ObjectDescription<T>,
            private val idProperty: Pair<String, PropertyDescription<T, *>>? = null,
            private val fromVertexParameter: Pair<String, KParameter>? = null,
            private val toVertexParameter: Pair<String, KParameter>? = null
    ) : Mapper<Map<*, *>, T> {

        override fun invoke(from: Map<*, *>): T {
            val constructorParameters = mutableMapOf<KParameter, Any?>()
            constructorParameters.putAll(objectDescription.properties.entries.associate { keyValue ->
                val propertyKey = keyValue.key
                val propertyDescription = keyValue.value
                val serializedPropertyValue = from[propertyKey]
                val deserializer = PropertyDeserializer(propertyDescription)
                val deserializedPropertyValue = deserializer(serializedPropertyValue)
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

    private inner class PropertySerializer<in T>(
            private val propertyDescription: PropertyDescription<*, T>
    ) : Mapper<Any?, SerializedProperty?> {

        override fun invoke(from: Any?): SerializedProperty? {
            if (propertyDescription.mapper != null && from != null) {
                return propertyDescription.mapper.forwardMap(from)
            }
            return when (from) {
                null -> null
                is Iterable<*> -> {
                    val fromClass by lazy {
                        propertyDescription.property.returnType.arguments.single().type?.classifier as? KClass<out Any> ?: throw IncompatibleIterable(propertyDescription)
                    }
                    from.map {
                        serializeProperty(it, fromClass)
                    }
                }
                is Map<*, *> -> {
                    val mapTypeParameters by lazy { propertyDescription.property.returnType.arguments }
                    val keyClass by lazy {
                        mapTypeParameters.first().type?.classifier as? KClass<out Any>
                                ?: throw IncompatibleMap(propertyDescription)
                    }
                    val valueClass by lazy {
                        mapTypeParameters.last().type?.classifier as? KClass<out Any>
                                ?: throw IncompatibleMap(propertyDescription)
                    }
                    from.entries.associate {
                        serializeProperty(it.key, keyClass) to serializeProperty(it.value, valueClass)
                    }
                }
                else -> serializeProperty(from, propertyDescription.kClass)
            }
        }
    }

    private inner class PropertyDeserializer<out T>(
            private val propertyDescription: PropertyDescription<T, *>
    ) : Mapper<SerializedProperty?, Any?> {

        override fun invoke(from: SerializedProperty?): Any? {
            if (propertyDescription.mapper != null && from != null) {
                return propertyDescription.mapper.inverseMap(from)
            }
            return when (from) {
                null -> null
                is Iterable<*> -> {
                    val toClass by lazy {
                        propertyDescription.property.returnType.arguments.single().type?.classifier as? KClass<out Any>
                                ?: throw IncompatibleIterable(propertyDescription)
                    }
                    when {
                        propertyDescription.kClass.isSubclassOf(Set::class) -> from.map { deserializeProperty(it, toClass) }.toSet()
                        propertyDescription.kClass.isSubclassOf(List::class) -> from.map { deserializeProperty(it, toClass) }
                        else -> throw IterableNotSupported(propertyDescription.kClass)
                    }
                }
                is Map<*, *> -> {
                    if (propertyDescription.kClass.isSubclassOf(Map::class)) {
                        val mapTypeParameters = propertyDescription.property.returnType.arguments
                        val keyClass by lazy {
                            mapTypeParameters.first().type?.classifier as? KClass<out Any>
                                    ?: throw IncompatibleMap(propertyDescription)
                        }
                        val valueClass by lazy {
                            mapTypeParameters.last().type?.classifier as? KClass<out Any>
                                    ?: throw IncompatibleMap(propertyDescription)
                        }
                        from.entries.associate { deserializeProperty(it.key, keyClass) to deserializeProperty(it.value, valueClass) }
                    } else {
                        deserializeProperty(from, propertyDescription.kClass)
                    }
                }
                else -> deserializeProperty(from, propertyDescription.kClass)
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
    }
}
