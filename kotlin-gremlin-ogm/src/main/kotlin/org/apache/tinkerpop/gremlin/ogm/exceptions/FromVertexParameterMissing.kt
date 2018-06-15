package org.apache.tinkerpop.gremlin.ogm.exceptions

import kotlin.reflect.KClass


internal class FromVertexParameterMissing(
        kClass: KClass<*>
) : AnnotationException(
        "Classes registered as an edge must have a parameter annotated with @FromVertex. Class: $kClass"
)
