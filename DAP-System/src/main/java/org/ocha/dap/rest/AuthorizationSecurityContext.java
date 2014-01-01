package org.ocha.dap.rest;

import java.security.Principal;

import javax.ws.rs.core.SecurityContext;
import javax.ws.rs.ext.Provider;

import org.ocha.dap.persistence.entity.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Provider
public class AuthorizationSecurityContext implements SecurityContext {

	private static final Logger log = LoggerFactory.getLogger(AuthorizationSecurityContext.class);

	private final User user;

	public AuthorizationSecurityContext(final User user) {
		super();
		this.user = user;
	}

	public AuthorizationSecurityContext() {
		super();
		this.user = null;
	}

	@Override
	public String getAuthenticationScheme() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Principal getUserPrincipal() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean isSecure() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean isUserInRole(final String role) {
		if (user == null) {
			log.debug("about to reject null user");
			return false;
		}

		log.debug(String.format("about to evaluate user : %s and role : %s", user.getId(), role));
		// FIXME have a role in User instead of using the name
		return role.equals(user.getId());
	}

}
