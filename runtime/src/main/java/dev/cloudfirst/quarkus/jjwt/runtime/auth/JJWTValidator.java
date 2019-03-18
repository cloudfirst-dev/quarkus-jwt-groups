package dev.cloudfirst.quarkus.jjwt.runtime.auth;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import org.jboss.logging.Logger;
import org.wildfly.security.auth.realm.token.TokenValidator;
import org.wildfly.security.auth.server.RealmUnavailableException;
import org.wildfly.security.authz.Attributes;
import org.wildfly.security.evidence.BearerTokenEvidence;

import dev.cloudfirst.quarkus.jjwt.runtime.config.JJWTAuthContextInfo;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.Jwts;

@ApplicationScoped
public class JJWTValidator implements TokenValidator {
  private static Logger log = Logger.getLogger(JJWTValidator.class);
  @Inject
  JJWTAuthContextInfo authContextInfo;

  public JJWTValidator() {}

  public JJWTValidator(JJWTAuthContextInfo authContextInfo) {
    this.authContextInfo = authContextInfo;
  }

  @Override
  public Attributes validate(BearerTokenEvidence evidence) throws RealmUnavailableException {
    String token = evidence.getToken();

    Jws<io.jsonwebtoken.Claims> jws;

    try {
      // TODO add issuer checking
      jws = Jwts.parser().setSigningKey(authContextInfo.getSignerKey()).parseClaimsJws(token);
    } catch (Exception ex) {
      throw new RealmUnavailableException("Failed to verify token", ex);
    }


    log.debug("calimsSet: " + jws);
    ClaimAttributes claimAttributes = new ClaimAttributes(token, "bearer", jws.getBody());
    return claimAttributes;
  }
}
