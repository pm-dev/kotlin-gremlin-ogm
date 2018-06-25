package starwars.models

import org.apache.tinkerpop.gremlin.ogm.elements.Edge
import org.apache.tinkerpop.gremlin.ogm.elements.Vertex
import org.apache.tinkerpop.gremlin.process.traversal.dsl.graph.GraphTraversal.Symbols.id
import org.janusgraph.graphdb.relations.RelationIdentifier

internal abstract class BaseEdge<FROM : Vertex, TO : Vertex>(

        id: RelationIdentifier?,

        override val from: FROM,

        override val to: TO

) : BaseElement<RelationIdentifier>(id), Edge<FROM, TO>
