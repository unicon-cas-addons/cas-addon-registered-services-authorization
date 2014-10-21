package net.unicon.cas.addon.registeredservices.authorization

/**
 * Default implementation of {@link RegisteredServiceAuthorizer}.
 *
 * This implementation uses a simple intersection of both sets of provided attributes (Map keys)
 * and authorizes further processing if any of the values from this flatten intersection of values match
 * for any given Map key that intersect.
 *
 * @author Dmitriy Kopylenko
 * @since 1.0.0
 */
class DefaultRegisteredServiceAuthorizer implements RegisteredServiceAuthorizer {

    /**
     * We always know in this case that attributes are instances of Map<String, Object>
     */
    @Override
    boolean authorized(Object serviceAttributes, Object authenticatedPrincipalAttributes) {
        //Groovy JDK APIs!
        serviceAttributes.keySet().intersect(authenticatedPrincipalAttributes.keySet()).any {
            serviceAttributes[it].intersect([authenticatedPrincipalAttributes[it]].flatten())
        }
    }
}
