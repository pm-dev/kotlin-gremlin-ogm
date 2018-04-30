package org.apache.tinkerpop.gremlin.ogm.exceptions

import kotlin.reflect.KClass

internal class InVertexParameterMissing(
        kClass: KClass<*>,
        annotationType: AnnotationType
) : AnnotationException(
        description = "Must annotate a $annotationType. with @InVertex. Class: $kClass"
)
