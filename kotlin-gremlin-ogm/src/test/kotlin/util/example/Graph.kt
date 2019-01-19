package util.example

import org.apache.commons.configuration.BaseConfiguration
import org.apache.tinkerpop.gremlin.ogm.GraphMapper
import org.apache.tinkerpop.gremlin.ogm.reflection.CachedGraphDescription
import org.apache.tinkerpop.gremlin.process.traversal.dsl.graph.GraphTraversalSource
import org.apache.tinkerpop.gremlin.tinkergraph.structure.TinkerGraph
import util.example.IntToBoolEdge.Companion.fromIntToBool

internal fun exampleGraphMapper() = object : GraphMapper {

    val graph = kotlin.run {
        val idManager = TinkerGraph.DefaultIdManager.LONG.name
        val conf = BaseConfiguration()
        conf.setProperty(TinkerGraph.GREMLIN_TINKERGRAPH_VERTEX_ID_MANAGER, idManager)
        conf.setProperty(TinkerGraph.GREMLIN_TINKERGRAPH_EDGE_ID_MANAGER, idManager)
        conf.setProperty(TinkerGraph.GREMLIN_TINKERGRAPH_VERTEX_PROPERTY_ID_MANAGER, idManager)
        TinkerGraph.open(conf)
    }

    override val g: GraphTraversalSource get() = graph.traversal()

    override val graphDescription = CachedGraphDescription(
            vertices = setOf(
                    VertexWithBoolean::class,
                    VertexWithByte::class,
                    VertexWithInt::class,
                    VertexWithDouble::class,
                    VertexWithFloat::class,
                    VertexWithString::class,
                    VertexWithInstant::class,
                    VertexWithUUID::class,
                    VertexWithURL::class,
                    VertexWithLong::class,
                    VertexWithDoubleNested::class,
                    VertexWithObjectList::class,
                    VertexWithObjectMap::class,
                    VertexWithPrimitiveSet::class,
                    VertexWithPrimitiveList::class,
                    VertexWithPrimitiveMap::class,
                    VertexWithNullablePrimitiveMap::class,
                    VertexWithEnum::class,
                    VertexWithNumber::class,
                    VertexWithCustomMapper::class,
                    VertexWithNullable::class,
                    VertexWithInt::class,
                    VertexWithTransient::class
            ),
            edgeSpecs = mapOf(
                    asymmetricManyToMany to null,
                    asymmetricOptionalToMany to null,
                    asymmetricOptionalToOptional to null,
                    asymmetricOptionalToSingle to null,
                    asymmetricSingleToMany to null,
                    asymmetricSingleToOptional to null,
                    asymmetricSingleToSingle to null,
                    symmetricManyToMany to null,
                    symmetricOptionalToOptional to null,
                    symmetricSingleToSingle to null,
                    fromIntToBool to IntToBoolEdge::class
            ),
            objectProperties = setOf(
                    Nested::class,
                    ObjectWithInt::class
            ),
            scalarProperties = mapOf(
                    Number::class to NumberToStringMapper(),
                    Sport::class to Sport
            ))
}
