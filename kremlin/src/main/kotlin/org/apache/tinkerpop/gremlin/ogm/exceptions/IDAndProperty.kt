package org.apache.tinkerpop.gremlin.ogm.exceptions

import kotlin.reflect.KClass

internal class IDAndProperty(
        kClass: KClass<*>,
        name: String?,
        annotationType: AnnotationType
) : AnnotationException(
        description = "${annotationType.name.capitalize()} can be annotated with @ID or @Property, but not both. " +
        "${annotationType.name.capitalize()}: $name. Class: $kClass"
)
