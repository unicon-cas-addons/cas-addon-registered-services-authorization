package net.unicon.cas.addon.registeredservices.authorization.webflow

import net.unicon.cas.addon.registeredservices.DefaultRegisteredServiceWithAttributes
import net.unicon.cas.addon.registeredservices.authorization.DefaultRegisteredServiceAuthorizer
import net.unicon.cas.addon.registeredservices.authorization.RoleBasedServiceAuthorizationException
import org.jasig.cas.authentication.Authentication
import org.jasig.cas.authentication.principal.Principal
import org.jasig.cas.authentication.principal.WebApplicationService
import org.jasig.cas.services.RegisteredServiceImpl
import org.jasig.cas.services.ServicesManager
import org.jasig.cas.services.UnauthorizedServiceException
import org.jasig.cas.ticket.TicketGrantingTicket
import org.jasig.cas.ticket.registry.TicketRegistry
import org.springframework.webflow.core.collection.LocalAttributeMap
import org.springframework.webflow.execution.RequestContext
import spock.lang.Specification

/**
 * Spock-based tests for ${link ServiceAuthorizationAction}
 *
 * @author Eric Pierce
 */
class ServiceAuthorizationActionTests extends Specification {


    /**
     * Mock objects that will be used in all tests
     */
    def principal = Mock(Principal)
    def authentication = Mock(Authentication)
    def tgt = Mock(TicketGrantingTicket)
    def ticketRegistry = Mock(TicketRegistry)
    def webApplicationService = Mock(WebApplicationService)
    def requestContext = Mock(RequestContext)
    def servicesManager = Mock(ServicesManager)

    /**
     * configure mocked objects
     **/
    def setup() {

        ticketRegistry.getTicket('test-tgt', TicketGrantingTicket) >> tgt

        tgt.authentication >> authentication

        authentication.principal >> principal

        //User with attributes
        principal.id  >> "testUser"
        principal.attributes >> [attr1: 'val1', attr2: 'val2']

        //Service URL the user is accessing
        webApplicationService.id >> "http://example.com/service"

        requestContext.getRequestScope() >> new LocalAttributeMap([ticketGrantingTicketId: 'test-tgt'])
        requestContext.getFlowScope() >> new LocalAttributeMap([ticketGrantingTicketId: 'test-tgt', service: webApplicationService])
    }

    def "User is authorized to access a service with a whitelisted service registry"() {
        given: 'Whitelisted service registry should allow access to service'

        def registeredServiceImpl = new RegisteredServiceImpl()
        servicesManager.findServiceBy(_) >> registeredServiceImpl

        when: 'The test user accesses the RBAC service'
        def serviceAuthorizationActionTest = new ServiceAuthorizationAction(servicesManager, ticketRegistry, new DefaultRegisteredServiceAuthorizer())
        def result = serviceAuthorizationActionTest.doExecute(requestContext)

        then:
        notThrown(RoleBasedServiceAuthorizationException)
        result == null
    }

    def "User is authorized to access a service with RBAC"() {
        given: 'A registered service with RBAC rules that the test user will match'

        def registeredServiceWithRbac = new DefaultRegisteredServiceWithAttributes()
        registeredServiceWithRbac.extraAttributes = [authzAttributes: [attr1: ['val1']], unauthorizedRedirectUrl: "http://example.com/unauthorized"]
        registeredServiceWithRbac.serviceId = "http://example.com/service"

        //Configure the mocked service registry to return this service
        servicesManager.findServiceBy(_) >> registeredServiceWithRbac

        when: 'The test user accesses the RBAC service'
        def serviceAuthorizationActionTest = new ServiceAuthorizationAction(servicesManager, ticketRegistry, new DefaultRegisteredServiceAuthorizer())
        def result = serviceAuthorizationActionTest.doExecute(requestContext)

        then:
        notThrown(RoleBasedServiceAuthorizationException)
        result == null
    }

    def "User is not authorized to access a service with RBAC"() {
        given: 'A registered service with RBAC rules that the test user will NOT match'

        def registeredServiceWithRbac = new DefaultRegisteredServiceWithAttributes()
        registeredServiceWithRbac.extraAttributes = [authzAttributes: [attr1: ['some_other_val']], unauthorizedRedirectUrl: "http://example.com/unauthorized"]
        registeredServiceWithRbac.serviceId = "http://example.com/service"

        //Configure the mocked service registry to return this service
        servicesManager.findServiceBy(_) >> registeredServiceWithRbac

        when: 'The test user accesses the RBAC service'
        def serviceAuthorizationActionTest = new ServiceAuthorizationAction(servicesManager, ticketRegistry, new DefaultRegisteredServiceAuthorizer())
        def result = serviceAuthorizationActionTest.doExecute(requestContext) == null

        then:
        thrown(RoleBasedServiceAuthorizationException)
    }

    def "Service does not have RBAC rules configured"() {
        given: 'A registered service WITHOUT RBAC rules'

        def registeredServiceWithoutRbac = new DefaultRegisteredServiceWithAttributes()
        registeredServiceWithoutRbac.extraAttributes = [:]
        registeredServiceWithoutRbac.serviceId = "http://example.com/service"

        //Configure the mocked service registry to return this service
        servicesManager.findServiceBy(_) >> registeredServiceWithoutRbac

        when: 'The test user accesses the RBAC service'
        def serviceAuthorizationActionTest = new ServiceAuthorizationAction(servicesManager, ticketRegistry, new DefaultRegisteredServiceAuthorizer())
        def result = serviceAuthorizationActionTest.doExecute(requestContext)

        then:
        notThrown(RoleBasedServiceAuthorizationException)
        result == null
    }

    def "Service is disabled"() {
        given: 'A disabled service'

        def registeredServiceWithRbac = new DefaultRegisteredServiceWithAttributes()
        registeredServiceWithRbac.enabled = false
        registeredServiceWithRbac.extraAttributes = [authzAttributes: [attr1: ['val1']], unauthorizedRedirectUrl: "http://example.com/unauthorized"]
        registeredServiceWithRbac.serviceId = "http://example.com/service"

        //Configure the mocked service registry to return this service
        servicesManager.findServiceBy(_) >> registeredServiceWithRbac

        when: 'The test user accesses the RBAC service'
        def serviceAuthorizationActionTest = new ServiceAuthorizationAction(servicesManager, ticketRegistry, new DefaultRegisteredServiceAuthorizer())
        def result = serviceAuthorizationActionTest.doExecute(requestContext) == null

        then:
        thrown(UnauthorizedServiceException)
    }

    def "Service is not in the registry"() {
        given: 'ServiceManager returns null for an unknown service'

        //Configure the mocked service to return null when searching simulating non-existing service
        servicesManager.findServiceBy(_) >> null

        when: 'The test user accesses the RBAC service'
        def serviceAuthorizationActionTest = new ServiceAuthorizationAction(servicesManager, ticketRegistry, new DefaultRegisteredServiceAuthorizer())
        def result = serviceAuthorizationActionTest.doExecute(requestContext) == null

        then:
        thrown(UnauthorizedServiceException)
    }
}
