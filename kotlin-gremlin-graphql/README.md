# A library for easily deploying a GraphQL server backed by a graph database using kotlin-gremlin-ogm


#### Basic Usage:

Fetching relationships with coroutines in batched data loaders is as easy as

    fun getFriends(person: Person, env: DataFetchingEnvironment): CompletableFuture<List<Person>> = env.fetch {
        (friends from person).load() // returns List<Person>       
    }
    
Just build your GraphQLConfiguration with a `GraphMapperGQLContextBuilder` from this library


An example of a working implementation can be seen in the [starwars example project](https://github.com/pm-dev/kotlin-gremlin-ogm/tree/master/example/src/main/kotlin/starwars)

#### Installation:

- Gradle

        compile 'com.github.pm-dev:kotlin-gremlin-graphql:0.21.0'

- Maven

        <dependency>
            <groupId>com.github.pm-dev</groupId>
            <artifactId>kotlin-gremlin-graphql</artifactId>
            <version>0.21.0</version>
        </dependency>
