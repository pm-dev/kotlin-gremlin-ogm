package org.janusgraph.ogm.exceptions

internal class IndexTypeMismatch(
        expected: Class<*>,
        actual: Class<*>
) : RuntimeException("Attempting to index a property that is mapped to the graph as a $expected, " +
        "but the graph schema indicates this property already exists as a $actual")
