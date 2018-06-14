package org.apache.tinkerpop.gremlin.ogm.exceptions

import kotlin.reflect.KClass

internal class ToVertexParameterMissing(
        kClass: KClass<*>,
        annotationType: AnnotationType
) : AnnotationException(
        description = "Must annotate a $annotationType. with @ToVertex. Class: $kClass"
)
