package org.janusgraph.ogm.exceptions

import kotlin.reflect.KClass

internal class UnknownElementType(
        kClass: KClass<*>
) : RuntimeException("Attempting to create an index on class $kClass which is not an Edge or Vertex. " +
        "This indicates a bug in the library")
