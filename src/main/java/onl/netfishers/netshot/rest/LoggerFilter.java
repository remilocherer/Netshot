package onl.netfishers.netshot.rest;

import java.io.IOException;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerResponseContext;
import javax.ws.rs.container.ContainerResponseFilter;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.HttpHeaders;

import onl.netfishers.netshot.Netshot;
import onl.netfishers.netshot.aaa.Tacacs;
import onl.netfishers.netshot.aaa.User;
import onl.netfishers.netshot.device.NetworkAddress;

/**
 * Filter to log requests.
 */
class LoggerFilter implements ContainerResponseFilter {
	@Context
	private HttpServletRequest httpRequest;

	@Override
	public void filter(ContainerRequestContext requestContext, ContainerResponseContext responseContext)
			throws IOException {
		User user = null;
		try {
			user = (User) requestContext.getSecurityContext().getUserPrincipal();
		}
		catch (Exception e) {
			//
		}
		String method = requestContext.getMethod().toUpperCase();
		if ("GET".equals(method)) {
			Netshot.aaaLogger.debug("Request from {} ({}) - {} - \"{} {}\" - {}.", httpRequest.getRemoteAddr(),
					requestContext.getHeaderString(HttpHeaders.USER_AGENT), user == null ? "<none>" : user.getUsername(),
					requestContext.getMethod(), requestContext.getUriInfo().getRequestUri(), responseContext.getStatus());
		}
		else {
			Netshot.aaaLogger.info("Request from {} ({}) - {} - \"{} {}\" - {}.", httpRequest.getRemoteAddr(),
					requestContext.getHeaderString(HttpHeaders.USER_AGENT), user == null ? "<none>" : user.getUsername(),
					requestContext.getMethod(), requestContext.getUriInfo().getRequestUri(), responseContext.getStatus());
			Tacacs.account(requestContext.getMethod(), requestContext.getUriInfo().getRequestUri().getPath(),
				user == null ? "<none>" : user.getUsername(), Integer.toString(responseContext.getStatus()),
				NetworkAddress.getNetworkAddress(httpRequest.getRemoteAddr()));
		}
	}
}