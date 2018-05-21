package org.apache.tinkerpop.gremlin.ogm.annotations

import java.lang.annotation.Inherited


/**
 * Marks a class as capable of being mapped to a vertex of the graph.
 * Clients should register classes marked with @Vertex with a GraphMapper.
 */
@Retention(value = AnnotationRetention.RUNTIME)
@Target(allowedTargets = [(AnnotationTarget.CLASS)])
@Inherited
annotation class Vertex(

        /**
         * The label of the vertex as stored in the graph. We require clients to specify an explicit
         * label (instead of using the class name) to guard against refactoring situations
         * where the class name is changed and this annotation is not updated to keep the original label.
         */
        val label: String)
