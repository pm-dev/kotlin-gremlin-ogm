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
import org.apache.tinkerpop.gremlin.ogm.paths.Path
import org.apache.tinkerpop.gremlin.ogm.paths.bound.BoundPathToMany
import org.apache.tinkerpop.gremlin.ogm.paths.bound.BoundPathToOptional
import org.apache.tinkerpop.gremlin.ogm.paths.bound.BoundPathToSingle
import org.apache.tinkerpop.gremlin.ogm.paths.bound.SingleBoundPath
import org.apache.tinkerpop.gremlin.ogm.paths.relationships.BaseEdge
import org.apache.tinkerpop.gremlin.ogm.paths.relationships.Relationship
import org.apache.tinkerpop.gremlin.ogm.paths.steps.StepTraverser
import org.apache.tinkerpop.gremlin.ogm.reflection.EdgeDescription
import org.apache.tinkerpop.gremlin.ogm.reflection.ObjectDescription
import org.apache.tinkerpop.gremlin.ogm.reflection.PropertyDescription
import org.apache.tinkerpop.gremlin.ogm.reflection.VertexDescription
import org.apache.tinkerpop.gremlin.process.traversal.dsl.graph.GraphTraversal
import org.apache.tinkerpop.gremlin.process.traversal.dsl.graph.GraphTraversalSource
import org.apache.tinkerpop.gremlin.structure.Edge
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
open class GraphMapper(
        val g: GraphTraversalSource,
        vertexClasses: Set<KClass<*>>,
        relationships: Map<Relationship<*, *>, KClass<out BaseEdge<*, *>>?>,
        nestedObjectClasses: Set<KClass<*>> = setOf(),
        private val scalarMappers: Map<KClass<*>, PropertyBiMapper<*, *>> = mapOf()
) {

    private val vertexDescriptions: Map<KClass<*>, VertexDescription<*>> =
            vertexClasses.associate { it to VertexDescription.describe(it) }

    private val edgeDescriptions: Map<Relationship<*, *>, EdgeDescription<out BaseEdge<*, *>>?> =
            relationships.mapValues { entry -> entry.value?.let { EdgeDescription.describe(entry.key, it) } }

    private val nestedObjectDescriptions: Map<KClass<*>, ObjectDescription<*>> =
            nestedObjectClasses.associate { it to ObjectDescription.describe(it) }

    private val vertexDescriptionsByLabel: Map<String, VertexDescription<*>> =
            vertexDescriptions.mapKeys { it.value.label }

    private val relationshipNameToRelationship: Map<String, Relationship<*, *>> =
            edgeDescriptions.keys.associateBy { it.name }

    @Suppress("UNCHECKED_CAST")
    private val edgeKClassToRelationship: Map<KClass<out BaseEdge<*, *>>, Relationship<*, *>> =
            relationships.filterValues { it != null }.entries.associate { it.value to it.key } as Map<KClass<out BaseEdge<*, *>>, Relationship<*, *>>

    private val edgeMapper = object : BiMapper<BaseEdge<Any, Any>, Edge> {
        override fun forwardMap(from: BaseEdge<Any, Any>): Edge = from.edgeMapper().forwardMap(from)
        override fun inverseMap(from: Edge): BaseEdge<Any, Any> = from.edgeMapper<Any, Any, BaseEdge<Any, Any>>().inverseMap(from)
    }

    private val vertexMapper = object : BiMapper<Any, Vertex> {
        override fun forwardMap(from: Any): Vertex = from.vertexMapper().forwardMap(from)
        override fun inverseMap(from: Vertex): Any = from.vertexMapper<Any>().inverseMap(from)
    }

    /**
     * Fetch all vertices of vertex type V. For this function, V may be a superclass of
     * classes registered as a vertex.
     */
    inline fun <reified V : Any> fetchV(): List<V> = getV(V::class).toList()

    /**
     * Fetches vertices with a given id. The returned list will contain null
     * for ids that that don't correspond with a vertex to the graph.
     */
    fun <V : Any> fetchV(id: Any): V? = fetchV<V>(listOf(id)).single()

    /**
     * Fetches vertices with a given id. The returned list will contain null
     * for ids that that don't correspond with a vertex to the graph.
     */
    fun <V : Any> fetchV(vararg ids: Any): List<V?> = fetchV(ids.asList())

    /**
     * Fetches vertices with a given id. The returned list will contain null
     * for ids that that don't correspond with a vertex to the graph.
     */
    fun <V : Any> fetchV(ids: Collection<Any>): List<V?> {
        if (ids.none()) {
            return emptyList()
        }
        val objectsByID = getVInternal<V>(ids).asSequence().associate { it }
        return ids.map { id ->
            val obj = objectsByID[id]
            if (obj == null) {
                logger.debug("Unable to fetch object for id $id")
            } else {
                logger.debug("Fetched object for id $id")
            }
            obj
        }
    }

    /**
     * Gets a graph traversal that returns a vertex with a given id.
     */
    fun <V : Any> getV(id: Any): GraphTraversal<*, V> = getV(listOf(id))

    /**
     * Gets a graph traversal that returns vertices with a given id.
     */
    fun <V : Any> getV(vararg ids: Any): GraphTraversal<*, V> = getV(ids.toList())

    /**
     * Gets a graph traversal that returns vertices with a given id.
     */
    fun <V : Any> getV(ids: Collection<Any>): GraphTraversal<*, V> = getVInternal<V>(ids).map { it.get().second }

    /**
     * Get a traversal that returns all vertices of vertex type V. For this function, V may be a superclass of
     * classes registered as a vertex.
     */
    inline fun <reified V : Any> getV(): GraphTraversal<*, V> = getV(V::class)

    /**
     * Get a traversal that returns all vertices of vertex type V. For this function, V may be a superclass of
     * classes registered as a vertex.
     */
    fun <V : Any> getV(kClass: KClass<V>): GraphTraversal<*, V> {
        val labels = vertexDescriptions.filterKeys { vertexKClass ->
            vertexKClass.isSubclassOf(kClass)
        }.values.map { vertexObjectDescription ->
            vertexObjectDescription.label
        }
        if (labels.isEmpty()) {
            throw UnregisteredClass(kClass)
        }
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
     * Fetch all edges of edge type E. E must have been registered with a relationship.
     */
    inline fun <reified FROM : Any, reified TO : Any, reified E : BaseEdge<FROM, TO>> fetchE(): List<E> =
            getE(E::class).toList()

    /**
     * Fetches an edge with a given id. Null is returned if no edge exists to the graph
     * with the given id.
     */
    fun <FROM : Any, TO : Any, E : BaseEdge<FROM, TO>> fetchE(id: Any): E? = fetchE<FROM, TO, E>(listOf(id)).single()

    /**
     * Fetches edges with a given id. The returned list will contain null
     * for ids that that don't correspond with an edge to the graph.
     */
    fun <FROM : Any, TO : Any, E : BaseEdge<FROM, TO>> fetchE(vararg ids: Any): List<E?> = fetchE<FROM, TO, E>(ids.asList())

    /**
     * Fetches edges with a given id. The returned list will contain null
     * for ids that that don't correspond with an edge to the graph.
     */
    fun <FROM : Any, TO : Any, E : BaseEdge<FROM, TO>> fetchE(ids: Collection<Any>): List<E?> {
        if (ids.none()) {
            return emptyList()
        }
        val edgesByID = getEInternal<FROM, TO, E>(ids).asSequence().associate { it }
        return ids.map { id ->
            val edge = edgesByID[id]
            if (edge == null) {
                logger.debug("Unable to fetch object for id $id")
            } else {
                logger.debug("Fetched object for id $id")
            }
            edge
        }
    }

    /**
     * Gets a graph traversal that returns an edge with a given id.
     */
    fun <FROM : Any, TO : Any, E : BaseEdge<FROM, TO>> getE(id: Any): GraphTraversal<*, E> = getE(listOf(id))

    /**
     * Gets a graph traversal that returns edges with a given id.
     */
    fun <FROM : Any, TO : Any, E : BaseEdge<FROM, TO>> getE(vararg ids: Any):GraphTraversal<*, E> = getE(ids.asList())

    /**
     * Gets a graph traversal that returns edges with a given id.
     */
    fun <FROM : Any, TO : Any, E : BaseEdge<FROM, TO>> getE(ids: Collection<Any>): GraphTraversal<*, E> =
            getEInternal<FROM, TO, E>(ids).map { it.get().second }

    /**
     * Get a traversal that returns all edges of edge type E. E must have been registered with a relationship.
     */
    inline fun <reified FROM : Any, reified TO : Any, reified E : BaseEdge<FROM, TO>> getE(): GraphTraversal<*, E> =
            getE(E::class)

    /**
     * Get a traversal that returns all edges of edge type E. E must have been registered with a relationship.
     */
    fun <FROM : Any, TO : Any, E : BaseEdge<FROM, TO>> getE(kClass: KClass<E>): GraphTraversal<*, E> {
        val relationship = edgeKClassToRelationship[kClass] ?: throw UnregisteredClass(kClass)
        logger.debug("Will get all edges with label ${relationship.name}")
        return g.E().hasLabel(relationship.name)
                .map { vertex ->
                    vertex.get().edgeMapper<FROM, TO, E>().inverseMap(vertex.get())
                }
    }

    /**
     * Saves a objects annotated with @Vertex to the graph. If property annotated with @ID is null,
     * a new vertex will be created, otherwise this object will overwrite the current vertex with that id.
     * The returned object will always have a non-null @ID
     */
    fun <V : Any> saveV(vararg objs: V): List<V> = objs.map { saveV(it) }

    /**
     * Saves a objects annotated with @Vertex to the graph. If property annotated with @ID is null,
     * a new vertex will be created, otherwise this object will overwrite the current vertex with that id.
     * The returned object will always have a non-null @ID
     */
    fun <V : Any> saveV(objs: List<V>): List<V> = objs.map { saveV(it) }

    /**
     * Saves an object annotated with @Vertex to the graph. If property annotated with @ID is null,
     * a new vertex will be created, otherwise this object will overwrite the current vertex with that id.
     * The returned object will always have a non-null @ID
     */
    fun <V : Any> saveV(obj: V): V {
        val mapper = obj.vertexMapper()
        val vertex = mapper.forwardMap(obj)
        logger.debug("Saved vertex with id ${vertex.id()}")
        return mapper.inverseMap(vertex)
    }

    /**
     * Saves edges to the graph
     */
    fun <FROM : Any, TO : Any, E : BaseEdge<FROM, TO>> saveE(vararg edges: E) : List<E> = edges.map { saveE(it) }

    /**
     * Saves edges to the graph
     */
    fun <FROM : Any, TO : Any, E : BaseEdge<FROM, TO>> saveE(edges: Iterable<E>) : List<E> = edges.map { saveE(it) }

    /**
     * Saves an edge to the graph. This is a no-op if the edge already exists.
     */
    fun <FROM : Any, TO : Any, E : BaseEdge<FROM, TO>> saveE(edge: E): E {
        val mapper = edge.edgeMapper()
        val serialized = mapper.forwardMap(edge)
        logger.debug("Saved edge with id ${serialized.id()}")
        return mapper.inverseMap(serialized)
    }

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
    fun <FROM : Any, TO> traverse(boundStep: BoundPathToSingle<FROM, TO>): Map<FROM, TO> =
            traverse(boundStep.froms, boundStep.path).entries.associate { it.key to it.value.single() }

    /**
     * Traverses from multiple objects to the path's optional destination vertex for each origin object.
     */
    fun <FROM : Any, TO> traverse(boundStep: BoundPathToOptional<FROM, TO>): Map<FROM, TO?> =
            traverse(boundStep.froms, boundStep.path).entries.associate { it.key to it.value.singleOrNull() }

    /**
     * Traverses from multiple objects to the path's destination vertices for each origin object.
     */
    fun <FROM : Any, TO> traverse(boundStep: BoundPathToMany<FROM, TO>): Map<FROM, List<TO>> =
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

    private fun <V : Any> getVInternal(ids: Collection<Any>): GraphTraversal<*, Pair<Any, V>> =
            ids
                    .map { id ->
                        g.V(id)
                    }
                    .reduce { traversal1, traversal2 ->
                        traversal1.union(traversal2)
                    }
                    .map { vertex ->
                        vertex.get().id() to vertex.get().vertexMapper<V>().inverseMap(vertex.get())
                    }

    private fun <FROM : Any, TO : Any, E : BaseEdge<FROM, TO>> getEInternal(ids: Collection<Any>): GraphTraversal<*, Pair<Any, E>> =
            ids
                    .map { id ->
                        g.E(id)
                    }
                    .reduce { traversal1, traversal2 ->
                        traversal1.union(traversal2)
                    }
                    .map { edge ->
                        edge.get().id() to edge.get().edgeMapper<FROM, TO, E>().inverseMap(edge.get())
                    }

    @Suppress("UNCHECKED_CAST")
    private fun <FROM : Any, TO : Any, E : BaseEdge<FROM, TO>> Edge.edgeMapper(): EdgeMapper<FROM, TO, E> {
        val relationship = relationshipNameToRelationship[label()]
        val edgeDescription = edgeDescriptions[relationship] as EdgeDescription<E>?
        val fromVertexDescription = vertexDescriptionsByLabel[outVertex().label()] as VertexDescription<FROM>? ?: throw UnregisteredLabel(outVertex())
        val toVertexDescription = vertexDescriptionsByLabel[inVertex().label()] as VertexDescription<TO>? ?: throw UnregisteredLabel(inVertex())
        return EdgeMapper(g, edgeDescription, fromVertexDescription, toVertexDescription)
    }

    @Suppress("UNCHECKED_CAST")
    private fun <FROM : Any, TO : Any, E : BaseEdge<FROM, TO>> E.edgeMapper(): EdgeMapper<FROM, TO, E> {
        val edgeDescription = edgeDescriptions[relationship] as EdgeDescription<E>?
        val fromVertexDescription = vertexDescriptions[from::class] as VertexDescription<FROM>? ?: throw UnregisteredClass(from)
        val toVertexDescription = vertexDescriptions[to::class] as VertexDescription<TO>? ?: throw UnregisteredClass(to)
        return EdgeMapper(g, edgeDescription, fromVertexDescription, toVertexDescription)
    }

    private fun <T : Any> Vertex.vertexMapper(): VertexMapper<T> {
        @Suppress("UNCHECKED_CAST")
        val vertexDescription = vertexDescriptionsByLabel[label()] as VertexDescription<T>?
                ?: throw UnregisteredLabel(this)
        return VertexMapper(g, vertexDescription)
    }

    private fun <T : Any> T.vertexMapper(): VertexMapper<T> {
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

    private inner class EdgeMapper<FROM : Any, TO : Any, E : BaseEdge<FROM, TO>> private constructor(
            val edgeSerializer: EdgeSerializer<FROM, TO, E>,
            val edgeDeserializer: EdgeDeserializer<FROM, TO, E>
    ) : BiMapper<E, Edge>{

        constructor(
                g: GraphTraversalSource,
                edgeDescription: EdgeDescription<E>?,
                fromVertexDescription: VertexDescription<FROM>,
                toVertexDescription: VertexDescription<TO>
        ) : this(
                EdgeSerializer(g, edgeDescription, fromVertexDescription, toVertexDescription),
                EdgeDeserializer(edgeDescription, fromVertexDescription, toVertexDescription)
        )

        override fun forwardMap(from: E): Edge = edgeSerializer(from)
        override fun inverseMap(from: Edge): E = edgeDeserializer(from)
    }

    private inner class EdgeSerializer<FROM : Any, TO : Any, E : BaseEdge<FROM, TO>> private constructor(
            private val g: GraphTraversalSource,
            private val objectSerializer: ObjectSerializer<E>?,
            private val fromVertexDescription: VertexDescription<FROM>,
            private val toVertexDescription: VertexDescription<TO>
    ) : Mapper<E, Edge> {

        constructor(
                g: GraphTraversalSource,
                edgeDescription: EdgeDescription<E>?,
                fromVertexDescription: VertexDescription<FROM>,
                toVertexDescription: VertexDescription<TO>
        ) : this(
                g,
                edgeDescription?.let { ObjectSerializer(it) },
                fromVertexDescription,
                toVertexDescription)

        override fun invoke(from: E): Edge {
            val fromVertex = from.from
            val toVertex = from.to
            val relationship = from.relationship
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
            }.map { edge ->
                objectSerializer?.let {
                    val serializedProperties = it(from)
                    edge.get().setProperties(serializedProperties)
                } ?: edge.get()
            }
            val createOrGetEdge = g.inject<Any>(0).coalesce(existingEdge.sideEffect {
                logger.debug("BaseEdge ${relationship.name} from $fromVertex to $toVertex already exists. Save edge is a no-op.")
            }, createEdge.sideEffect {
                logger.debug("Creating edge ${relationship.name} from $fromVertex to $toVertex.")
            })
            if (!createOrGetEdge.hasNext()) throw ConflictingEdge(fromVertex, toVertex, relationship.name)
            return createOrGetEdge.toList().single()
        }

        private infix fun GraphTraversal<*, Vertex>.out(relationship: Relationship<*, *>): GraphTraversal<*, Vertex> =
                when (relationship.direction) {
                    Relationship.Direction.FORWARD -> out(relationship.name)
                    Relationship.Direction.BACKWARD -> `in`(relationship.name)
                    null -> both(relationship.name)
                }

        private infix fun GraphTraversal<*, Vertex>.`in`(relationship: Relationship<*, *>): GraphTraversal<*, Vertex> =
                when (relationship.direction) {
                    Relationship.Direction.FORWARD -> `in`(relationship.name)
                    Relationship.Direction.BACKWARD -> out(relationship.name)
                    null -> both(relationship.name)
                }

        private infix fun GraphTraversal<*, Vertex>.outE(relationship: Relationship<*, *>): GraphTraversal<*, Edge> =
                when (relationship.direction) {
                    Relationship.Direction.FORWARD -> outE(relationship.name)
                    Relationship.Direction.BACKWARD -> inE(relationship.name)
                    null -> bothE(relationship.name)
                }

        private infix fun GraphTraversal<*, Vertex>.inE(relationship: Relationship<*, *>): GraphTraversal<*, Edge> =
                when (relationship.direction) {
                    Relationship.Direction.FORWARD -> inE(relationship.name)
                    Relationship.Direction.BACKWARD -> outE(relationship.name)
                    null -> bothE(relationship.name)
                }
    }

    private inner class EdgeDeserializer<FROM : Any, TO : Any, E : BaseEdge<FROM, TO>> private constructor(
            private val objectDeserializer: ObjectDeserializer<E>?,
            private val fromVertexDeserializer: VertexDeserializer<FROM>,
            private val toVertexDeserializer: VertexDeserializer<TO>
    ) : Mapper<Edge, E> {

        constructor(
                edgeDescription: EdgeDescription<E>?,
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

        override fun invoke(from: Edge): E {
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
                val relationship = relationshipNameToRelationship[from.label()] as Relationship<FROM, TO>? ?: throw UnregisteredLabel(from)
                @Suppress("UNCHECKED_CAST")
                BaseEdge(fromVertex, toVertex, relationship) as E
            }()
        }
    }

    private inner class VertexMapper<T : Any> private constructor(
            val vertexSerializer: VertexSerializer<T>,
            val vertexDeserializer: VertexDeserializer<T>
    ) : BiMapper<T, Vertex> {

        constructor(
                g: GraphTraversalSource,
                vertexDescription: VertexDescription<T>
        ) : this(
                VertexSerializer(g, vertexDescription),
                VertexDeserializer(vertexDescription)
        )

        override fun forwardMap(from: T): Vertex = vertexSerializer(from)
        override fun inverseMap(from: Vertex): T = vertexDeserializer(from)
    }

    private inner class VertexSerializer<in T : Any> private constructor(
            private val g: GraphTraversalSource,
            private val vertexDescription: VertexDescription<T>,
            private val objectSerializer: ObjectSerializer<T>
    ) : Mapper<T, Vertex> {

        constructor(
                g: GraphTraversalSource,
                vertexDescription: VertexDescription<T>
        ) : this(
                g,
                vertexDescription,
                ObjectSerializer(vertexDescription)
        )

        override fun invoke(from: T): Vertex {
            val id = vertexDescription.id.property.get(from)
            val traversal = when (id) {
                null -> g.addV(vertexDescription.label)
                else -> g.V(id)
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

        constructor(vertexDescription: VertexDescription<T>)
                : this(ObjectDeserializer(vertexDescription, Pair(idTag, vertexDescription.id)))

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
            private val idProperty: Pair<String, PropertyDescription<T>>? = null,
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
