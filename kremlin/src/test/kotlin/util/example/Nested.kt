package util.example

import org.apache.tinkerpop.gremlin.ogm.annotations.Property
import java.util.*

internal data class ObjectWithInt(

        @param:Property(key = "intVal")
        @property:Property(key = "intVal")
        val intVal: Int
) {
    companion object {
        fun sample() = ObjectWithInt(intVal = Random().nextInt())
    }
}

internal data class Nested(

        @param:Property(key = "objWithInt")
        @property:Property(key = "objWithInt")
        val nestedObj: ObjectWithInt
) {
    companion object {
        fun sample() = Nested(nestedObj = ObjectWithInt.sample())
    }
}
