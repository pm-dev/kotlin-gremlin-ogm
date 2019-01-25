package org.janusgraph.ogm.exceptions

import kotlin.reflect.KClass
import kotlin.reflect.KProperty1

internal class SuperclassAnnotationException(
        baseClass: KClass<*>,
        superClass: KClass<*>,
        property: KProperty1<*, *>,
        annotations: List<*>
) : RuntimeException("All annotations must happen in the base class. Baseclass: $baseClass. " +
        "Superclass $superClass. Property: $property. Annotations: $annotations"
)
