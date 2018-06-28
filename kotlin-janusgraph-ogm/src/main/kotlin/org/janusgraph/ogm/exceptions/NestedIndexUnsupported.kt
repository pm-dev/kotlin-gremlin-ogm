package org.janusgraph.ogm.exceptions

import kotlin.reflect.KClass

internal class NestedIndexUnsupported(
        nestedObject: KClass<*>,
        property: String
) : RuntimeException("Attempting to create an index on property of a class that is registered as a nested object, " +
        "but indices may only be added to properties of Edges or Vertices. " +
        "Nested object: $nestedObject, property: $property")
