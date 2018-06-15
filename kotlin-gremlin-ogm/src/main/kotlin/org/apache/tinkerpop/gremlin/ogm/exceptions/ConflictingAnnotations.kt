package org.apache.tinkerpop.gremlin.ogm.exceptions

import kotlin.reflect.KClass
import kotlin.reflect.KParameter

internal class ConflictingAnnotations(
        kClass: KClass<*>,
        param: KParameter
) : AnnotationException(
        "Param '${param.name}'can be annotated with only one of @ID, @Property, @ToVertex, FromVertex. Class: $kClass"
)
