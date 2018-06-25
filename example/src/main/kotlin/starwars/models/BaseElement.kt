package starwars.models

internal abstract class BaseElement<ID>(
        val id: ID?
) {

    override fun hashCode() = id?.hashCode() ?: super.hashCode()

    override fun equals(other: Any?) = id != null && other != null && other is BaseElement<*> && id == other.id
}
