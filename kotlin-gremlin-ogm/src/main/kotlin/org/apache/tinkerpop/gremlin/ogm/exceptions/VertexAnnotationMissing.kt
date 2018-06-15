package org.apache.tinkerpop.gremlin.ogm.exceptions

import kotlin.reflect.KClass

internal class VertexAnnotationMissing(
        kClass: KClass<*>
) : AnnotationException(
        "Could not find @Vertex annotation on class that was registered as a Vertex. Class: $kClass"
)
