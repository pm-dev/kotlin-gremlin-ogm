package framework

import org.apache.tinkerpop.gremlin.ogm.elements.Edge
import org.apache.tinkerpop.gremlin.ogm.elements.Vertex
import org.janusgraph.graphdb.relations.RelationIdentifier

internal abstract class BaseEdge<out FROM : Vertex, out TO : Vertex> : BaseElement<RelationIdentifier>(), Edge<FROM, TO>
