package org.janusgraph.ogm

import org.apache.tinkerpop.gremlin.ogm.reflection.ElementDescription
import org.apache.tinkerpop.gremlin.ogm.reflection.GraphDescription
import org.apache.tinkerpop.gremlin.ogm.reflection.PropertyDescription
import org.apache.tinkerpop.gremlin.structure.Edge
import org.apache.tinkerpop.gremlin.structure.Element
import org.apache.tinkerpop.gremlin.structure.Vertex
import org.janusgraph.core.JanusGraph
import org.janusgraph.core.schema.JanusGraphIndex
import org.janusgraph.core.schema.JanusGraphManagement
import org.janusgraph.core.schema.JanusGraphSchemaType
import org.janusgraph.ogm.annotations.Indexed
import org.janusgraph.ogm.exceptions.*
import org.janusgraph.ogm.reflection.IndexDescription
import org.slf4j.LoggerFactory
import kotlin.reflect.KClass
import kotlin.reflect.full.declaredMemberProperties
import kotlin.reflect.full.isSubclassOf
import kotlin.reflect.full.superclasses

interface JanusGraphIndicesBuilder {

    val graphDescription: GraphDescription

    operator fun invoke(graph: JanusGraph): List<JanusGraphIndex> {
        val mgmt = graph.openManagement()
        val indices = indexDescriptions(graphDescription).asSequence()
                .filter { !mgmt.containsGraphIndex(it.indexName) }
                .map { buildIndex(it, mgmt) }
                .toList()
        mgmt.commit()
        return indices
    }

    companion object {

        private val logger = LoggerFactory.getLogger(JanusGraphManagement::class.java)

        private fun buildIndex(indexDescription: IndexDescription, graphManagement: JanusGraphManagement): JanusGraphIndex {
            val qualifiedPropertyName = indexDescription.propertyName
            val propertyKey = if (graphManagement.containsPropertyKey(qualifiedPropertyName)) {
                graphManagement.getPropertyKey(qualifiedPropertyName).apply {
                    if (dataType() != indexDescription.dataType.javaObjectType) {
                        throw IndexTypeMismatch(indexDescription.dataType.javaObjectType, dataType())
                    }
                }
            } else {
                graphManagement.makePropertyKey(qualifiedPropertyName).dataType(indexDescription.dataType.javaObjectType).make()
            }
            val schemaType: JanusGraphSchemaType = when (indexDescription.elementType) {
                org.apache.tinkerpop.gremlin.structure.Edge::class -> graphManagement.getOrCreateEdgeLabel(indexDescription.elementLabel)
                org.apache.tinkerpop.gremlin.structure.Vertex::class -> graphManagement.getOrCreateVertexLabel(indexDescription.elementLabel)
                else -> throw UnknownElementType(indexDescription.elementType)
            }
            val indexBuilder = graphManagement.buildIndex(indexDescription.indexName, indexDescription.elementType.java)
            indexBuilder.addKey(propertyKey)
            indexBuilder.indexOnly(schemaType)
            if (indexDescription.unique) {
                indexBuilder.unique()
            }
            logger.info("Adding index ${indexDescription.indexName}")
            return indexBuilder.buildCompositeIndex()
        }

        private fun indexDescriptions(graphDescription: GraphDescription): List<IndexDescription> {
            val vertexIndexDescriptions = graphDescription.vertexClasses.flatMap { vertexClass ->
                val vertexDescription = graphDescription.getVertexDescription(vertexClass)
                indexDescriptions(vertexClass, vertexDescription, Vertex::class, graphDescription)
            }
            val edgeIndexDescriptions = graphDescription.edgeClasses.flatMap { edgeClass ->
                val edgeDescription = graphDescription.getEdgeDescription(edgeClass)
                indexDescriptions(edgeClass, edgeDescription, Edge::class, graphDescription)
            }
            graphDescription.objectPropertyClasses.forEach { objectPropertyClass ->
                val objectDescription = graphDescription.getObjectPropertyDescription(objectPropertyClass)
                objectDescription.properties.forEach {
                    val propertyDescription = it.value
                    if (propertyDescription.property.annotations.filterIsInstance(Indexed::class.java).isNotEmpty()) {
                        val propertyName = it.key
                        throw NestedIndexUnsupported(objectPropertyClass, propertyName)
                    }
                }
            }
            return vertexIndexDescriptions + edgeIndexDescriptions
        }

        private fun indexDescriptions(
                elementClass: KClass<*>,
                element: ElementDescription<*>,
                elementType: KClass<out Element>,
                graphDescription: GraphDescription
        ): List<IndexDescription> =
                elementClass.declaredMemberProperties.flatMap { property ->
                    val indexAnnotations = property.annotations.filterIsInstance(Indexed::class.java)
                    if (indexAnnotations.isNotEmpty()) {
                        val propertyDescriptions = element.properties.filter { it.value.property == property }
                        if (propertyDescriptions.isEmpty()) throw IndexNotOnProperty(elementClass, property)
                        indexAnnotations.flatMap { indexAnnotation ->
                            val propertyKeyToDescription = propertyDescriptions.entries.single()
                            val propertyKey = propertyKeyToDescription.key
                            val propertyDescription = propertyKeyToDescription.value
                            indexDescriptions(propertyDescription, indexAnnotation.unique, propertyKey, elementType, element.label, graphDescription)
                        }
                    } else {
                        emptyList()
                    }
                }.also {
                    elementClass.superclasses.forEach { superclass ->
                        superclass.declaredMemberProperties.forEach { superclassProperty ->
                            val indexAnnotations = superclassProperty.annotations.filterIsInstance(Indexed::class.java)
                            if (indexAnnotations.isNotEmpty()) {
                                throw SuperclassAnnotationException(elementClass, superclass, superclassProperty, indexAnnotations)
                            }
                        }
                    }
                }

        private fun indexDescriptions(
                propertyDescription: PropertyDescription<*, *>,
                unique: Boolean,
                prefix: String,
                elementType: KClass<out Element>,
                elementLabel: String,
                graphDescription: GraphDescription
        ): List<IndexDescription> {
            val deserializedClass = propertyDescription.kClass
            if (deserializedClass.isSubclassOf(Iterable::class)) {
                throw IterableIndexUnsupported(prefix, propertyDescription.property)
            }
            if (deserializedClass.isSubclassOf(Map::class)) {
                throw MapIndexUnsupported(prefix, propertyDescription.property)
            }
            val customMapper = propertyDescription.mapper
            if (customMapper != null) {
                val indexDescription = IndexDescription(prefix, customMapper.serializedClass, unique, elementType, elementLabel)
                return listOf(indexDescription)
            }
            if (graphDescription.objectPropertyClasses.contains(deserializedClass)) {
                return graphDescription.getObjectPropertyDescription(deserializedClass).properties.flatMap {
                    val nestedPropertyKey = it.key
                    val nestedPropertyDescription = it.value
                    indexDescriptions(nestedPropertyDescription, unique, "$prefix.$nestedPropertyKey", elementType, elementLabel, graphDescription)
                }
            }
            val datatype = graphDescription.getScalarPropertyMapper(deserializedClass).serializedClass
            val indexDescription = IndexDescription(prefix, datatype, unique, elementType, elementLabel)
            return listOf(indexDescription)
        }
    }
}
