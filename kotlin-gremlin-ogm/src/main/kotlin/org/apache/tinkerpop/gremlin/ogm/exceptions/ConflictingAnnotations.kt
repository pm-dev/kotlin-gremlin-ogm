package org.apache.tinkerpop.gremlin.ogm.exceptions

import kotlin.reflect.KClass

internal class ConflictingAnnotations(
        kClass: KClass<*>,
        name: String?,
        annotationType: AnnotationType
) : AnnotationException(
        description = "${annotationType.name.capitalize()} can be annotated with only one of @ID, @Property, @ToVertex, FromVertex. " +
        "${annotationType.name.capitalize()}: $name. Class: $kClass"
)
