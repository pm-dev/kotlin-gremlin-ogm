package org.apache.tinkerpop.gremlin.ogm.exceptions

import kotlin.reflect.KClass

internal class IterableNotSupported(
        iterableClass: KClass<*>
) : AnnotationException(
        description = "List and Set are the only Iterable property types supported. " +
                "Attempting to serialize $iterableClass."
)
