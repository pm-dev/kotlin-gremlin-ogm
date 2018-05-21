package org.apache.tinkerpop.gremlin.ogm.exceptions

import kotlin.reflect.KClass

internal class ConflictingAnnotations(
        kClass: KClass<*>,
        name: String?,
        annotationType: AnnotationType
) : AnnotationException(
        description = "${annotationType.name.capitalize()} can be annotated with only one of @ID, @Property, @InVertex, OutVertex. " +
        "${annotationType.name.capitalize()}: $name. Class: $kClass"
)
