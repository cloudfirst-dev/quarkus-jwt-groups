package com.idsysapps.auth.dbroles.runtime;

import org.wildfly.security.auth.realm.token.TokenSecurityRealm;
import org.wildfly.security.auth.server.SecurityRealm;

import io.quarkus.arc.runtime.BeanContainer;
import io.quarkus.runtime.RuntimeValue;
import io.quarkus.runtime.annotations.Template;
import io.quarkus.smallrye.jwt.runtime.auth.MpJwtValidator;

@Template
public class DbRoleTemplate {

  public RuntimeValue<SecurityRealm> createTokenRealm(BeanContainer container) {
    MpJwtValidator jwtValidator = container.instance(MpJwtValidator.class);
    UserRolesResolver userRolessResolver = container.instance(UserRolesResolver.class);
    TokenSecurityRealm tokenRealm = TokenSecurityRealm.builder()
        .claimToPrincipal(userRolessResolver::mpJwtLogic).validator(jwtValidator).build();
    return new RuntimeValue<>(tokenRealm);
  }


}
