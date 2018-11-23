package util.example

import com.google.common.collect.ImmutableBiMap
import org.apache.tinkerpop.gremlin.ogm.mappers.EnumBiMapper
import util.StaticBiMapper

internal enum class Sport {
    BASKETBALL,
    FOOTBALL,
    BASEBALL,
    TENNIS,
    ;

    companion object : EnumBiMapper<Sport>, StaticBiMapper<Sport, String> {
        override val map: ImmutableBiMap<Sport, String> = ImmutableBiMap.of(
                BASKETBALL, "BASKETBALL",
                FOOTBALL, "FOOTBALL",
                BASEBALL, "BASEBALL",
                TENNIS, "TENNIS")

        override val serializedClass get() = String::class
    }
}

