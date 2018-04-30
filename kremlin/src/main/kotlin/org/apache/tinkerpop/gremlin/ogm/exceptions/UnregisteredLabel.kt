package org.apache.tinkerpop.gremlin.ogm.exceptions

import org.apache.tinkerpop.gremlin.structure.Element

internal class UnregisteredLabel(
        element: Element
) : ClientException(
        description = "Attempting to deserialize an element with label ${element.label()}, but no " +
                "class has been registered with GraphMapper whose @Vertex annotation has label ${element.label()} " +
                "or whose Edge was registered with a relationship with name ${element.label()}."
)
