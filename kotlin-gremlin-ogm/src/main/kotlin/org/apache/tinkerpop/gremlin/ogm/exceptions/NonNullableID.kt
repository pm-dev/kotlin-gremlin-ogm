package org.apache.tinkerpop.gremlin.ogm.exceptions

import kotlin.reflect.KClass
import kotlin.reflect.KParameter

internal class NonNullableID(
        kClass: KClass<*>,
        param: KParameter
) : AnnotationException(
        "Param '${param.name}' annotated with @ID must be nullable for when the " +
                "object has not yet been persisted. " +
                "Clients may choose to have another param used for identification that is non-null. " +
                "Class $kClass"
)
