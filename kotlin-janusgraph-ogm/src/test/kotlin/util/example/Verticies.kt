package util.example

import org.apache.tinkerpop.gremlin.ogm.annotations.Element
import org.apache.tinkerpop.gremlin.ogm.annotations.ID
import org.apache.tinkerpop.gremlin.ogm.annotations.Property
import java.util.*

@Element(label = "VertexWithInt")
internal class VertexWithInt(

        @ID
        id: Long? = null,

        @Property(key = "a")
        int: Int

) : BaseVertex<Int>(id = id, a = int) {

    companion object {
        fun sample() = VertexWithInt(int = Random().nextInt())
    }
}

internal abstract class BaseVertex<out T : Any?>(

        val id: Long? = null,

        @property:Property(key = "a")
        val a: T

) {

    override fun hashCode(): Int = id?.hashCode() ?: super.hashCode()

    override fun equals(other: Any?): Boolean = id != null && other != null && other is BaseVertex<*> && id == other.id
}
