package org.apache.tinkerpop.gremlin.ogm.exceptions

import org.apache.tinkerpop.gremlin.ogm.GraphMapper
import kotlin.reflect.KClass

internal class ReservedIDName(
        kClass: KClass<*>
) : AnnotationException(
        description = "@Property.name may not equal ${GraphMapper.idTag}. " +
        "This name is reserved by the library. Class: $kClass"
)
