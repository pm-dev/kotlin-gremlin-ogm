package framework

import graphql.schema.pagination.Identifiable


internal abstract class BaseElement<out ID> : Identifiable {

    abstract override val id: ID?

    override fun hashCode() = id?.hashCode() ?: super.hashCode()

    override fun equals(other: Any?) = id != null && other != null && other is BaseElement<*> && id == other.id
}
