package org.apache.tinkerpop.gremlin.ogm.exceptions

import kotlin.reflect.KClass

internal class ElementAnnotationMissing(
        kClass: KClass<*>
) : AnnotationException(
        "Could not find @Element annotation on class that was registered as an Edge or Vertex. Class: $kClass"
)
