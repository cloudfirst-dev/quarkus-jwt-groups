package dev.cloudfirst.quarkus.jjwt.runtime.auth;

import java.security.Principal;
import java.util.Set;

import org.eclipse.microprofile.jwt.JsonWebToken;

import io.undertow.security.idm.Account;

public class JWTAccount implements Account {
  private static final long serialVersionUID = 4295205897089515606L;

  private JsonWebToken principal;

  private Account delegate;

  public JWTAccount(JsonWebToken principal, Account delegate) {
    this.principal = principal;
    this.delegate = delegate;
  }

  @Override
  public Principal getPrincipal() {
    return principal;
  }

  @Override
  public Set<String> getRoles() {
    return delegate.getRoles();
  }

}
