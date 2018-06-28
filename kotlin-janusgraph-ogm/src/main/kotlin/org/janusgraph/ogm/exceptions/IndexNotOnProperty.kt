package org.janusgraph.ogm.exceptions

import kotlin.reflect.KClass
import kotlin.reflect.KProperty1

internal class IndexNotOnProperty(
        element: KClass<*>,
        property: KProperty1<*, *>
) : RuntimeException("@Indexed is only supported on Vertex or Edge properties that are annotated with @Property. Class: $element, property: $property")
