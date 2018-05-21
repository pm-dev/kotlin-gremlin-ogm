package org.apache.tinkerpop.gremlin.ogm.exceptions

import kotlin.reflect.KClass

internal class DuplicateOutVertex(
        kClass: KClass<*>,
        name1: String?,
        name2: String?,
        annotationType: AnnotationType
) : AnnotationException(
        description = "Only one $annotationType may be annotated with @OutVertex. " +
                "${annotationType.name.capitalize()}: $name1, $name2. Class: $kClass."
)
