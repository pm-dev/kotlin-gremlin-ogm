package starwars.graphql

internal enum class StarwarsPath(val dataLoaderRegistryKey: String) {
    FRIENDS("friends"),
    SECOND_DEGREE_FRIENDS("second_degree_friends"),
    TWINS("twins"),
}
