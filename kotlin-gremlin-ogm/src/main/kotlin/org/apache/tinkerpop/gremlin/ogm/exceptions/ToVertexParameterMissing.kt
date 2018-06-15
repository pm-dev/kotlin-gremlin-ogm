package org.apache.tinkerpop.gremlin.ogm.exceptions

import kotlin.reflect.KClass

internal class ToVertexParameterMissing(
        kClass: KClass<*>
) : AnnotationException(
        "Classes registered as an edge must have a parameter annotated with @ToVertex. Class: $kClass"
)
