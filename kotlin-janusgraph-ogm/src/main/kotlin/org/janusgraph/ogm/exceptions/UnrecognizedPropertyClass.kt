package org.janusgraph.ogm.exceptions

import kotlin.reflect.KClass

internal class UnrecognizedPropertyClass(
        propertyKey: String,
        kClass: KClass<*>
) : RuntimeException("Attempting to index a property, but it's unknown how to map this class type to the graph. " +
        "$propertyKey, $kClass")
