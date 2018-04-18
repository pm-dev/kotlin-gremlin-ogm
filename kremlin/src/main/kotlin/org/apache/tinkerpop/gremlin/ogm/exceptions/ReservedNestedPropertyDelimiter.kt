package org.apache.tinkerpop.gremlin.ogm.exceptions

import org.apache.tinkerpop.gremlin.ogm.extensions.nestedPropertyDelimiter
import kotlin.reflect.KClass

internal class ReservedNestedPropertyDelimiter(
        kClass: KClass<*>,
        key: String
) : AnnotationException(
        description = "@Property.name may not contain '$nestedPropertyDelimiter' as this string is used " +
        "as a delimiter for nested properties. '$key' on class $kClass"
)
