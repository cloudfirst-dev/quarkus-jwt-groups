package dev.cloudfirst.quarkus.jjwt.runtime;

import java.security.Principal;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Instance;
import javax.inject.Inject;

import org.jboss.logging.Logger;
import org.wildfly.security.authz.Attributes;

import dev.cloudfirst.quarkus.jjwt.runtime.auth.ClaimAttributes;
import dev.cloudfirst.quarkus.jjwt.runtime.auth.ElytronJwtCallerPrincipal;
import io.jsonwebtoken.Claims;

@ApplicationScoped
public class UserRolesResolver {
  private static Logger log = Logger.getLogger(JJWTTemplate.class);

  @Inject
  @UserRoleProvider
  Instance<UserRoles> checks;

  public Principal mpJwtLogic(Attributes claims) {
    log.debug("mapping claims to principal: " + claims);

    if (!(claims instanceof ClaimAttributes)) {
      throw new IllegalStateException("ElytronJwtCallerPrincipal requires Attributes to be a: "
          + ClaimAttributes.class.getName());
    }

    ElytronJwtCallerPrincipal jwtCallerPrincipal =
        new ElytronJwtCallerPrincipal(ClaimAttributes.class.cast(claims));

    if (checks.isResolvable()) {
      log.debug("found a custom group mapping");
      Claims currentClaims = ((ClaimAttributes) claims).getClaimsSet();

      currentClaims.put("groups", checks.get().getRolesForUser(jwtCallerPrincipal.getName()));

      claims = new ClaimAttributes(ClaimAttributes.class.cast(claims).getRawToken(),
          ClaimAttributes.class.cast(claims).getTokenType(), currentClaims);
    }

    return jwtCallerPrincipal;
  }
}
