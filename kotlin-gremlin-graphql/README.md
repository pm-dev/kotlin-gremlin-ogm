# A library for easily deploying a GraphQL server backed by a graph database using kotlin-gremlin-ogm


#### Basic Usage:

Create a Servlet

        val contextBuilder = GraphMapperGQLContextBuilder(graphMapperSupplier, dataLoaderRegisterySupplier)
        SimpleGraphQLHttpServlet.newBuilder(
            GraphQLInvocationInputFactory.newBuilder(graphQLSchema)
                .withGraphQLContextBuilder(contextBuilder)
                .build())
            .build()

Fetch from a DataFetchingEnvironment

        fun getNode(nodeId: String, env: DataFetchingEnvironment): Node? {
            return env.graphMapper.V<Node>(nodeId).fetch()
        }

Use batched data loaders for fetching relationships

    fun getFriends(person: Person, env: DataFetchingEnvironment): CompletableFuture<List<Person>> {
        return env.dataLoader<Person, List<Person>>("friends").load(person)
    }


An example of a working implementation can be seen in the [starwars example project](https://github.com/pm-dev/kotlin-gremlin-ogm/tree/master/example/src/main/kotlin/starwars)

#### Installation:

- Gradle

        compile 'com.github.pm-dev:kotlin-gremlin-graphql:0.19.0'

- Maven

        <dependency>
            <groupId>com.github.pm-dev</groupId>
            <artifactId>kotlin-gremlin-graphql</artifactId>
            <version>0.19.0</version>
        </dependency>
