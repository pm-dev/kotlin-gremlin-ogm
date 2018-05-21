package org.apache.tinkerpop.gremlin.ogm.exceptions

import kotlin.reflect.KClass

internal class OutVertexParameterMissing(
        kClass: KClass<*>,
        annotationType: AnnotationType
) : AnnotationException(
        description = "Must annotate a $annotationType. with @OutVertex. Class: $kClass"
)
