package org.apache.tinkerpop.gremlin.ogm.exceptions

import kotlin.reflect.KClass

internal class ClassInheritanceMismatch(
        lowerBound: KClass<*>,
        upperBound: KClass<*>
) : AnnotationException(
        description = "$lowerBound must be a subclass of $upperBound"
)
