package dev.cloudfirst.quarkus.jjwt.it;

import java.security.Principal;

import javax.annotation.security.RolesAllowed;
import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.SecurityContext;

import org.eclipse.microprofile.jwt.JsonWebToken;

@Path("/secured")
public class SecuredResource {
  @Inject
  JsonWebToken jwt;

  @GET()
  @Path("roles-allowed")
  @RolesAllowed({"user_admin"})
  @Produces(MediaType.TEXT_PLAIN)
  public String helloRolesAllowed(@Context SecurityContext ctx) {
    Principal caller = ctx.getUserPrincipal();
    String name = caller == null ? "anonymous" : caller.getName();
    return name;
  }
}
