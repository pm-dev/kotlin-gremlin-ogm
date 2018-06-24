package util.example

import org.apache.tinkerpop.gremlin.ogm.annotations.*
import org.assertj.core.internal.bytebuddy.utility.RandomString
import java.time.Instant
import java.util.*
import kotlin.math.absoluteValue


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

@Element(label = "VertexWithByte")
internal class VertexWithByte(

        @ID
        id: Long? = null,

        @Property(key = "a")
        byte: Byte

) : BaseVertex<Byte>(id = id, a = byte) {

    companion object {
        fun sample() = VertexWithByte(byte = Byte.MAX_VALUE)
    }
}

@Element(label = "VertexWithBoolean")
internal class VertexWithBoolean(

        @ID
        id: Long? = null,

        @Property(key = "a")
        bool: Boolean

) : BaseVertex<Boolean>(id = id, a = bool) {

    companion object {
        fun sample() = VertexWithBoolean(bool = Random().nextBoolean())
    }
}

@Element(label = "VertexWithDouble")
internal class VertexWithDouble(

        @ID
        id: Long? = null,


        @Property(key = "a")
        double: Double

) : BaseVertex<Double>(id = id, a = double) {

    companion object {
        fun sample() = VertexWithDouble(double = Random().nextDouble())
    }
}

@Element(label = "VertexWithFloat")
internal class VertexWithFloat(

        @ID
        id: Long? = null,

        @Property(key = "a")
        float: Float

) : BaseVertex<Float>(id = id, a = float) {

    companion object {
        fun sample() = VertexWithFloat(float = Random().nextFloat())
    }
}

@Element(label = "VertexWithLong")
internal class VertexWithLong(

        @ID
        id: Long? = null,

        @Property(key = "a")
        long: Long

) : BaseVertex<Long>(id = id, a = long) {

    companion object {
        fun sample() = VertexWithLong(long = Random().nextLong())
    }
}

@Element(label = "VertexWithString")
internal class VertexWithString(

        @ID
        id: Long? = null,

        @Property(key = "a")
        string: String

) : BaseVertex<String>(id = id, a = string) {

    companion object {
        fun sample() = VertexWithString(string = RandomString.make())
    }
}

@Element(label = "VertexWithInstant")
internal class VertexWithInstant(

        @ID
        id: Long? = null,

        @Property(key = "a")
        instant: Instant

) : BaseVertex<Instant>(id = id, a = instant) {

    companion object {
        fun sample() = VertexWithInstant(instant = Instant.now())
    }
}

@Element(label = "VertexWithUUID")
internal class VertexWithUUID(

        @ID
        id: Long? = null,

        @Property(key = "a")
        uuid: UUID

) : BaseVertex<UUID>(id = id, a = uuid) {

    companion object {
        fun sample() = VertexWithUUID(uuid = UUID.randomUUID())
    }
}

@Element(label = "VertexWithDoubleNested")
internal class VertexWithDoubleNested(

        @ID
        id: Long? = null,

        @Property(key = "a")
        nested: Nested

) : BaseVertex<Nested>(id = id, a = nested) {

    companion object {
        fun sample() = VertexWithDoubleNested(nested = Nested.sample())
    }
}

@Element(label = "VertexWithPrimitiveList")
internal class VertexWithPrimitiveList(

        @ID
        id: Long? = null,

        @Property(key = "a")
        listOfInts: List<Int>

) : BaseVertex<List<Int>>(id = id, a = listOfInts) {

    companion object {
        fun sample() = VertexWithPrimitiveList(listOfInts = listOf(Random().nextInt(), Random().nextInt()))
    }
}

@Element(label = "VertexWithPrimitiveSet")
internal class VertexWithPrimitiveSet(

        @ID
        id: Long? = null,

        @Property(key = "a")
        setOfStrings: Set<String>

) : BaseVertex<Set<String>>(id = id, a = setOfStrings) {

    companion object {
        fun sample() = VertexWithPrimitiveSet(setOfStrings = setOf(RandomString.make(), RandomString.make()))
    }
}

@Element(label = "VertexWithObjectList")
internal class VertexWithObjectList(

        @ID
        id: Long? = null,

        @Property(key = "a")
        listOfObjextWithInts: List<ObjectWithInt>

) : BaseVertex<List<ObjectWithInt>>(id = id, a = listOfObjextWithInts) {

    companion object {
        fun sample() = VertexWithObjectList(listOfObjextWithInts = listOf(ObjectWithInt.sample(), ObjectWithInt.sample()))
    }
}

@Element(label = "VertexWithPrimitiveMap")
internal class VertexWithPrimitiveMap(

        @ID
        id: Long? = null,

        @Property(key = "a")
        intMap: Map<String, Int>

) : BaseVertex<Map<String, Int>>(id = id, a = intMap) {

    companion object {
        fun sample() = VertexWithPrimitiveMap(intMap = mapOf(
                "first" to Random().nextInt(),
                "second" to Random().nextInt()))
    }
}

@Element(label = "VertexWithNullablePrimitiveMap")
internal class VertexWithNullablePrimitiveMap(

        @ID
        id: Long? = null,

        @Property(key = "a")
        intMap: Map<String, Int>?

) : BaseVertex<Map<String, Int>?>(id = id, a = intMap) {

    companion object {
        fun sample() = VertexWithNullablePrimitiveMap(intMap = null)
    }
}

@Element(label = "VertexWithObjectMap")
internal class VertexWithObjectMap(

        @ID
        id: Long? = null,

        @Property(key = "a")
        objWithIntMap: Map<String, ObjectWithInt>

) : BaseVertex<Map<String, ObjectWithInt>>(id = id, a = objWithIntMap) {

    companion object {
        fun sample() = VertexWithObjectMap(objWithIntMap = mapOf(
                "first" to ObjectWithInt.sample(),
                "second" to ObjectWithInt.sample()))
    }
}

@Element(label = "VertexWithEnum")
internal class VertexWithEnum(

        @ID
        id: Long? = null,

        @Property(key = "a")
        sport: Sport

) : BaseVertex<Sport>(id = id, a = sport) {

    companion object {
        fun sample() = VertexWithEnum(sport = Sport.values()[Random().nextInt().absoluteValue % Sport.values().size])
    }
}

@Element(label = "VertexWithNumber")
internal class VertexWithNumber(

        @ID
        id: Long? = null,

        @Property(key = "a")
        number: Number

) : BaseVertex<Number>(id = id, a = number) {
    companion object {
        fun sample() = VertexWithNumber(number = Random().nextDouble())
    }
}

@Element(label = "VertexWithCustomMapper")
internal class VertexWithCustomMapper(

        @ID
        id: Long? = null,

        @Property(key = "a")
        @Mapper(Base64Mapper::class)
        string: String

): BaseVertex<String>(id = id, a = string) {
    companion object {
        fun sample() = VertexWithCustomMapper(string = RandomString.make())
    }
}

@Element(label = "VertexWithNullable")
internal class VertexWithNullable(

        @ID
        id: Long? = null,

        @Property(key = "a")
        nullableString: String? = null

) : BaseVertex<String?>(id = id, a = nullableString) {

    companion object {
        fun sample() = VertexWithNullable()
    }
}

@Element(label = "VertexWithTransient")
internal class VertexWithTransient(

        @ID
        val id: Long? = null,

        val transientString: String? = null

) {

    companion object {
        fun sample() = VertexWithTransient(transientString = RandomString.make())
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
