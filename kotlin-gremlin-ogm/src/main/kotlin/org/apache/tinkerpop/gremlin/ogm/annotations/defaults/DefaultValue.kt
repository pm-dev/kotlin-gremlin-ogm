package org.apache.tinkerpop.gremlin.ogm.annotations.defaults

import java.lang.annotation.Inherited
import java.util.function.Supplier
import kotlin.reflect.KClass

/**
 * Annotation that, when used with the @Property annotation, specifies a value to use when
 * the graph does not have a value for the given parameter. This can be useful as an alternative to a migration
 * when adding a non-nullable property to an Element.
 */
@Retention(value = AnnotationRetention.RUNTIME)
@Target(AnnotationTarget.VALUE_PARAMETER)
@Inherited
annotation class DefaultValue(

        /**
         * A class that can supply the default value for the parameter this annotation is placed on.
         * The supplied value must never be null.
         */
        val supplier: KClass<out Supplier<out Any>>)
