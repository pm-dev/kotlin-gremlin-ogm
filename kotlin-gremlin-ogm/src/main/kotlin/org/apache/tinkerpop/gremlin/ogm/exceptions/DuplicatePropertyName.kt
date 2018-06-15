package org.apache.tinkerpop.gremlin.ogm.exceptions

import kotlin.reflect.KClass

internal class DuplicatePropertyName(kClass: KClass<*>) : AnnotationException(
        "Cannot have multiple @Property annotations on member properties that have the same key. Class: $kClass"
)
