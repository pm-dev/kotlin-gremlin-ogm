package org.apache.tinkerpop.gremlin.ogm.exceptions

internal class VertexNotFound(
        id: Any,
        label: String
) : AnnotationException(
        description = "Unable to find '$label' vertex in the graph for object with id '$id'. Make sure you're " +
                "not setting the field marked with the @ID annotation in your own code."
)
