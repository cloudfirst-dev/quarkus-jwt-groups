package com.idsysapps.auth.dbroles.runtime;

import java.security.Principal;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Instance;
import javax.inject.Inject;

import org.jose4j.jwt.JwtClaims;
import org.wildfly.security.authz.Attributes;

import io.quarkus.smallrye.jwt.runtime.auth.ClaimAttributes;
import io.quarkus.smallrye.jwt.runtime.auth.ElytronJwtCallerPrincipal;
import io.quarkus.smallrye.jwt.runtime.auth.MpJwtValidator;

@ApplicationScoped
public class UserRolesResolver {
  @Inject
  @UserRoleProvider
  Instance<UserRoles> checks;

  public Principal mpJwtLogic(Attributes claims) {
    String pn = claims.getFirst("upn");
    if (pn == null) {
      pn = claims.getFirst("preferred_name");
    }
    if (pn == null) {
      pn = claims.getFirst("sub");
    }

    JwtClaims currentClaims = ((ClaimAttributes) claims).getClaimsSet();
    currentClaims.setClaim("groups", checks.get().getRolesForUser(pn));

    ClaimAttributes newClaims = new ClaimAttributes(currentClaims);

    ElytronJwtCallerPrincipal jwtCallerPrincipal = new ElytronJwtCallerPrincipal(pn, newClaims);
    return jwtCallerPrincipal;
  }
}
