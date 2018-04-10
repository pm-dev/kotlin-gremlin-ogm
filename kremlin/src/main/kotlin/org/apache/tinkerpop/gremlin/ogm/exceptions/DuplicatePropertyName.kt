package org.apache.tinkerpop.gremlin.ogm.exceptions

import kotlin.reflect.KClass

internal class DuplicatePropertyName(
        kClass: KClass<*>,
        annotationType: AnnotationType
) : AnnotationException("Duplicated name on @Property annotation for $annotationType. Class: $kClass")
