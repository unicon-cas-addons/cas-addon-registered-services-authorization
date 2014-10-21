package net.unicon.cas.addon.registeredservices.authorization.config


import net.unicon.cas.addon.registeredservices.authorization.DefaultRegisteredServiceAuthorizer
import net.unicon.cas.addon.registeredservices.authorization.RegisteredServiceAuthorizer
import net.unicon.cas.addon.registeredservices.authorization.webflow.ServiceAuthorizationAction
import org.jasig.cas.services.ServicesManager
import org.jasig.cas.ticket.registry.TicketRegistry
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

/**
 * Spring Application Context configuration for service authorization addon beans.
 *
 * @author Dmitriy Kopylenko
 * @since 1.0.0
 */
@Configuration
class ServicesAuthorizationConfig {

    @Bean
    ServiceAuthorizationAction serviceAuthorizationAction(ServicesManager servicesManager, TicketRegistry ticketRegistry) {
        new ServiceAuthorizationAction(servicesManager, ticketRegistry, registeredServiceAuthorizer())
    }

    @Bean
    public RegisteredServiceAuthorizer registeredServiceAuthorizer() {
        new DefaultRegisteredServiceAuthorizer()
    }
}
