package org.apache.tinkerpop.gremlin.ogm.exceptions

import kotlin.reflect.KClass

internal class IDPropertyRequired(
        kClass: KClass<*>
) : AnnotationException(
        "Could not find ID property of graph element. This library will first look for member properties " +
                "annotated with @ID, then it will look for a member property with the same name as the parameter " +
                "annotated with @ID. Class: $kClass"
)
