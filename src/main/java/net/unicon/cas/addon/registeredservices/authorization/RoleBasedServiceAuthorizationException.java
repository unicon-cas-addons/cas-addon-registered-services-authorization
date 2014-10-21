package net.unicon.cas.addon.registeredservices.authorization;

/**
 * Exception indicating a failure of the coarse-grained role-based authorization of any registered service.
 *
 * Typically, this exception should  be handled by SWF login webflow's global transition handler to act upon it.
 * For example, a global transition handler could do an external redirect to a pre-configured URL, etc.
 *
 * @author Dmitriy Kopylenko
 * @since 1.0.0
 */
public class RoleBasedServiceAuthorizationException extends RuntimeException {
}
