package net.unicon.cas.addon.registeredservices.authorization.config

import net.unicon.cas.addon.registeredservices.authorization.DefaultRegisteredServiceAuthorizer
import net.unicon.cas.addon.registeredservices.authorization.RegisteredServiceAuthorizer
import net.unicon.cas.addon.registeredservices.authorization.webflow.ServiceAuthorizationAction
import org.jasig.cas.services.ServicesManager
import org.jasig.cas.ticket.registry.TicketRegistry
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.core.annotation.Order

/**
 * Spring Application Context configuration for service authorization addon beans.
 *
 * @author Dmitriy Kopylenko
 * @since 1.0.0
 */
@Configuration
@Order //to make sure the lowest precedence order of this config processing is set to let all the other beans in the ctx be defined first
class ServicesAuthorizationConfig {

    @Bean
    ServiceAuthorizationAction serviceAuthorizationAction(ServicesManager servicesManager, TicketRegistry ticketRegistry,
                                                          RegisteredServiceAuthorizer authorizer) {
        new ServiceAuthorizationAction(servicesManager, ticketRegistry, authorizer)
    }

    @Bean
    @ConditionalOnMissingBean(RegisteredServiceAuthorizer)
    public RegisteredServiceAuthorizer defaultAuthorizer() {
        new DefaultRegisteredServiceAuthorizer()
    }
}
