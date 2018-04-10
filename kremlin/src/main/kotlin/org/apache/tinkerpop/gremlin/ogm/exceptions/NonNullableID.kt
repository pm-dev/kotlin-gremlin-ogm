package org.apache.tinkerpop.gremlin.ogm.exceptions

import kotlin.reflect.KClass

internal class NonNullableID(
        kClass: KClass<*>,
        name: String?,
        annotationType: AnnotationType
) : AnnotationException("${annotationType.name.capitalize()} annotated with @ID must be nullable for when the " +
        "object has not yet been persisted. " +
        "Clients may choose to have another $annotationType used for identification that is non-null. " +
        "${annotationType.name.capitalize()}: $name. Class $kClass")
