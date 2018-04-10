package org.apache.tinkerpop.gremlin.ogm.exceptions

import kotlin.reflect.KClass

internal class MapperMissingOnType(
        kClass: KClass<*>,
        key: String
) : AnnotationException("@Property '$key' must be annotated with @Mapper since its type is not a " +
        "concrete KClass<*>. Class: $kClass")
