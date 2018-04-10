package starwars.graphql

import com.coxautodev.graphql.tools.SchemaParser
import graphql.servlet.SimpleGraphQLServlet
import org.springframework.boot.web.servlet.ServletRegistrationBean
import org.springframework.stereotype.Component
import starwars.graphql.character.CharacterQueryResolver
import starwars.graphql.droid.DroidMutationResolver
import starwars.graphql.droid.DroidQueryResolver
import starwars.graphql.droid.DroidTypeResolver
import starwars.graphql.human.HumanQueryResolver
import starwars.graphql.human.HumanTypeResolver

@Component
class StarwarsGraphQLServletRegistrationBean(
        characterQueryResolver: CharacterQueryResolver,
        humanTypeResolver: HumanTypeResolver,
        humanQueryResolver: HumanQueryResolver,
        droidTypeResolver: DroidTypeResolver,
        droidQueryResolver: DroidQueryResolver,
        droidMutationResolver: DroidMutationResolver
): ServletRegistrationBean<SimpleGraphQLServlet>(SimpleGraphQLServlet.builder(
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
        .build(),
        "/graphql")
