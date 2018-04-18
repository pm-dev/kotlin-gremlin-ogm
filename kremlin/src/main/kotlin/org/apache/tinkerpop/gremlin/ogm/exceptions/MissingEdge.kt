package org.apache.tinkerpop.gremlin.ogm.exceptions

import org.apache.tinkerpop.gremlin.structure.Vertex

internal class MissingEdge(
        fromVertex: Vertex,
        relationshipName: String
) : ClientException(
        description = "Unable to find required (aka ToSingle) edge " +
                "from vertex ${fromVertex.label()} with id ${fromVertex.id()} to $relationshipName."
)
