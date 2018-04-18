package org.apache.tinkerpop.gremlin.ogm.exceptions

import org.apache.tinkerpop.gremlin.structure.Vertex

internal class UnregisteredLabel(
        vertex: Vertex
) : ClientException(
        description = "Attempting to deserialize a vertex with label ${vertex.label()}, but no " +
                "class has been registered with GraphMapper whose @Vertex annotation has label ${vertex.label()}."
)
