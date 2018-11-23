package framework

internal abstract class BaseElement<out ID>(
        val id: ID?
) {

    override fun hashCode() = id?.hashCode() ?: super.hashCode()

    override fun equals(other: Any?) = id != null && other != null && other is BaseElement<*> && id == other.id
}
