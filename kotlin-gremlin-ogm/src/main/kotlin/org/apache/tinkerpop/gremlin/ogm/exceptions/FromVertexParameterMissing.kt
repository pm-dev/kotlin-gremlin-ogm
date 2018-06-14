package org.apache.tinkerpop.gremlin.ogm.exceptions

import kotlin.reflect.KClass

internal class FromVertexParameterMissing(
        kClass: KClass<*>,
        annotationType: AnnotationType
) : AnnotationException(
        description = "Must annotate a $annotationType. with @FromVertex. Class: $kClass"
)
