package starwars.graphql

import graphql.servlet.SimpleGraphQLHttpServlet
import org.springframework.boot.web.servlet.ServletRegistrationBean
import org.springframework.stereotype.Component

@Component
internal class StarwarsGraphQLServletRegistrationBean(
        servlet: SimpleGraphQLHttpServlet
) : ServletRegistrationBean<SimpleGraphQLHttpServlet>(servlet, "/graphql")

