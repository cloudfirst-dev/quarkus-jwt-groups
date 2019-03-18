package dev.cloudfirst.quarkus.jjwt.runtime;

import javax.annotation.Priority;
import javax.enterprise.context.RequestScoped;
import javax.enterprise.inject.Alternative;
import javax.enterprise.inject.Produces;

import org.eclipse.microprofile.jwt.JsonWebToken;

import dev.cloudfirst.quarkus.jjwt.runtime.auth.JWTAccount;

@Priority(1)
@Alternative
@RequestScoped
public class PrincipalProducer {
  private JWTAccount account;

  public PrincipalProducer() {}

  public JWTAccount getAccount() {
    return account;
  }

  public void setAccount(JWTAccount account) {
    this.account = account;
  }

  /**
   * The producer method for the current JsonWebToken
   *
   * @return
   */
  @Produces
  JsonWebToken currentJWTPrincipalOrNull() {
    JsonWebToken token = null;
    if (account != null) {
      token = (JsonWebToken) account.getPrincipal();
    }
    return token;
  }
}
