package org.apache.tinkerpop.gremlin.ogm.exceptions

import kotlin.reflect.KClass

internal class ReservedNumberKey(
        kClass: KClass<*>,
        key: String
) : AnnotationException(
        description = "@Property.name may not be a number, since number indexes are used " +
        "as index keys for collection properties. '$key' on class $kClass"
)
