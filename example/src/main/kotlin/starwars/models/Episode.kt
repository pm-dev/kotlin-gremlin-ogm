package starwars.models

import com.google.common.collect.ImmutableBiMap
import framework.StaticBiMapper
import org.apache.tinkerpop.gremlin.ogm.mappers.EnumBiMapper

internal enum class Episode {
    NEW_HOPE,
    EMPIRE,
    JEDI,
    ;
    companion object : EnumBiMapper<Episode>, StaticBiMapper<Episode, String> {
        override val map: ImmutableBiMap<Episode, String> = ImmutableBiMap.of(
                NEW_HOPE, "NEW_HOPE",
                EMPIRE, "EMPIRE",
                JEDI, "JEDI")

        override val serializedClass get() = String::class
    }
}
