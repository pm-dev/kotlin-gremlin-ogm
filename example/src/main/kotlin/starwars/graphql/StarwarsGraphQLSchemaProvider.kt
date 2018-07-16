package starwars.graphql

import com.coxautodev.graphql.tools.SchemaParser
import graphql.servlet.DefaultGraphQLSchemaProvider
import org.springframework.stereotype.Component
import starwars.graphql.character.CharacterQueryResolver
import starwars.graphql.droid.DroidMutationResolver
import starwars.graphql.droid.DroidQueryResolver
import starwars.graphql.droid.DroidTypeResolver
import starwars.graphql.human.HumanQueryResolver
import starwars.graphql.human.HumanTypeResolver

@Component
internal class StarwarsGraphQLSchemaProvider(
        characterQueryResolver: CharacterQueryResolver,
        humanTypeResolver: HumanTypeResolver,
        humanQueryResolver: HumanQueryResolver,
        droidTypeResolver: DroidTypeResolver,
        droidQueryResolver: DroidQueryResolver,
        droidMutationResolver: DroidMutationResolver) : DefaultGraphQLSchemaProvider(
        SchemaParser.newParser()
                .file("starwars.graphqls")
                .resolvers(
                        characterQueryResolver,
                        humanTypeResolver,
                        humanQueryResolver,
                        droidTypeResolver,
                        droidQueryResolver,
                        droidMutationResolver)
                .build().makeExecutableSchema())

