package org.janusgraph.ogm.exceptions

import kotlin.reflect.KProperty1

internal class MapIndexUnsupported(
        prefix: String,
        property: KProperty1<*, *>
) : RuntimeException("@Indexed annotation may not be placed on a property that is a Map, or a property that " +
        "has nested Map properties. $prefix, $property")
