package com.idsysapps.auth.dbroles.runtime;

import org.jboss.logging.Logger;
import org.wildfly.security.auth.realm.token.TokenSecurityRealm;
import org.wildfly.security.auth.server.SecurityDomain;
import org.wildfly.security.auth.server.SecurityRealm;

import com.idsysapps.auth.dbroles.runtime.auth.JJWTValidator;
import com.idsysapps.auth.dbroles.runtime.auth.JWTAuthMethodExtension;
import com.idsysapps.auth.dbroles.runtime.auth.JwtIdentityManager;

import io.quarkus.arc.runtime.BeanContainer;
import io.quarkus.runtime.RuntimeValue;
import io.quarkus.runtime.annotations.Template;
import io.undertow.security.idm.IdentityManager;
import io.undertow.servlet.ServletExtension;

@Template
public class JJWTTemplate {
  private static Logger log = Logger.getLogger(JJWTTemplate.class);

  public RuntimeValue<SecurityRealm> createTokenRealm(BeanContainer container) {
    JJWTValidator jwtValidator = container.instance(JJWTValidator.class);
    UserRolesResolver userRolessResolver = container.instance(UserRolesResolver.class);
    TokenSecurityRealm tokenRealm = TokenSecurityRealm.builder()
        .claimToPrincipal(userRolessResolver::mpJwtLogic).validator(jwtValidator).build();
    return new RuntimeValue<>(tokenRealm);
  }

  public IdentityManager createIdentityManager(RuntimeValue<SecurityDomain> securityDomain) {
    return new JwtIdentityManager(securityDomain.getValue());
  }

  public ServletExtension createAuthExtension(String authMechanism, BeanContainer container) {
    JWTAuthMethodExtension authExt = container.instance(JWTAuthMethodExtension.class);
    authExt.setAuthMechanism(authMechanism);
    return authExt;
  }

}
