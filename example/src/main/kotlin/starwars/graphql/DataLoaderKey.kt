package starwars.graphql

import graphql.schema.DataFetchingEnvironment
import graphql.servlet.batched.dataLoader
import org.dataloader.DataLoader

enum class DataLoaderKey(val key: String) {
    FRIENDS("friends"),
    SECOND_DEGREE_FRIENDS("second_degree_friends"),
    TWINS("twins"),
}

fun <K, V> DataFetchingEnvironment.dataLoader(key: DataLoaderKey): DataLoader<K, V> = dataLoader(key.key)
