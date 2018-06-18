package org.apache.tinkerpop.gremlin.ogm.exceptions

internal class IDNotFound(
        obj: Any,
        id: Any?
) : ClientException(
        "Attempting to save element '$obj' whose @ID property is non-null: '$id', " +
                "however that id was not found in the graph."
)
