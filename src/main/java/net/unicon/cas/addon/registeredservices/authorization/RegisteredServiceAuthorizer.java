package net.unicon.cas.addon.registeredservices.authorization;

/**
 * An authorization strategy interface for vending service tickets based on {@link net.unicon.cas.addon.registeredservices
 * .RegisteredServiceWithAttributes} authorization attributes.
 *
 * @author Dmitriy Kopylenko
 * @since 1.0.0
 */
public interface RegisteredServiceAuthorizer {

    /**
     * Determine if CAS is authorized to vend service ticket for the given service by comparing configured registered service authorization attributes
     * with the actual resolved attributes of an authenticated principal.
     *
     * @param serviceAttributes
     * @param authenticatedPrincipalAttributes
     *
     * @return true if authorized, false otherwise
     */
    boolean authorized(Object serviceAttributes, Object authenticatedPrincipalAttributes);
}
