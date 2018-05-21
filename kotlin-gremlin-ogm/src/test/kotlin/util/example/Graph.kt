package util.example

import org.apache.commons.configuration.BaseConfiguration
import org.apache.tinkerpop.gremlin.ogm.GraphMapper
import org.apache.tinkerpop.gremlin.tinkergraph.structure.TinkerGraph
import util.example.IntToBoolEdge.Companion.fromIntToBool

private fun exampleGraph(): TinkerGraph {
    val idManager = TinkerGraph.DefaultIdManager.LONG.name
    val conf = BaseConfiguration()
    conf.setProperty(TinkerGraph.GREMLIN_TINKERGRAPH_VERTEX_ID_MANAGER, idManager)
    conf.setProperty(TinkerGraph.GREMLIN_TINKERGRAPH_EDGE_ID_MANAGER, idManager)
    conf.setProperty(TinkerGraph.GREMLIN_TINKERGRAPH_VERTEX_PROPERTY_ID_MANAGER, idManager)
    return TinkerGraph.open(conf)
}

internal fun exampleGraphMapper() = GraphMapper(
        g = exampleGraph().traversal(),
        vertexClasses = setOf(
                VertexWithBoolean::class,
                VertexWithByte::class,
                VertexWithInt::class,
                VertexWithDouble::class,
                VertexWithFloat::class,
                VertexWithString::class,
                VertexWithInstant::class,
                VertexWithUUID::class,
                VertexWithLong::class,
                VertexWithDoubleNested::class,
                VertexWithObjectList::class,
                VertexWithObjectMap::class,
                VertexWithPrimitiveSet::class,
                VertexWithPrimitiveList::class,
                VertexWithPrimitiveMap::class,
                VertexWithEnum::class,
                VertexWithNumber::class,
                VertexWithCustomMapper::class,
                VertexWithNullable::class,
                VertexWithInt::class,
                VertexWithTransient::class
        ),
        relationships = mapOf(
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
        nestedObjectClasses = setOf(
                Nested::class,
                ObjectWithInt::class
        ),
        scalarMappers = mapOf(
                Number::class to NumberToStringMapper(),
                Sport::class to Sport
        ))
