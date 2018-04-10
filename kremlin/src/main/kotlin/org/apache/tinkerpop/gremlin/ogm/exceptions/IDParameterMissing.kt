package org.apache.tinkerpop.gremlin.ogm.exceptions

import kotlin.reflect.KClass

internal class IDParameterMissing(
        kClass: KClass<*>,
        annotationType: AnnotationType
) : AnnotationException("Must annotate a $annotationType. with @ID. Class: $kClass")
