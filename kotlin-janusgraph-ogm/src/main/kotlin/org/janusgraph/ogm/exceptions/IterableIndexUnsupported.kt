package org.janusgraph.ogm.exceptions

import kotlin.reflect.KProperty1

internal class IterableIndexUnsupported(
        prefix: String,
        property: KProperty1<*, *>
) : RuntimeException("@Indexed annotation may not be placed on a property that is an Iterable, or a property that " +
        "has nested Iterable properties. $prefix, $property")
