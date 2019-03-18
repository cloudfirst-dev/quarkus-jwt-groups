package dev.cloudfirst.quarkus.jjwt.runtime.auth;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Header;
import io.jsonwebtoken.Jwt;
import io.jsonwebtoken.Jwts;
import io.undertow.security.idm.Credential;

public class JWTCredential implements Credential {

  private String bearerToken;
  private String name;
  private Exception jwtException;

  /**
   * @param bearerToken
   * @param authContextInfo
   */
  public JWTCredential(String bearerToken) {
    this.bearerToken = bearerToken;
  }


  public String getBearerToken() {
    return bearerToken;
  }

  public String getName() {
    if (name == null) {
      name = "INVALID_TOKEN_NAME";
      try {
        // get just the body and header
        int i = bearerToken.lastIndexOf('.');
        String withoutSignature = bearerToken.substring(0, i + 1);
        Jwt<Header, Claims> untrusted = Jwts.parser().parseClaimsJwt(withoutSignature);

        // We have to determine the unique name to use as the principal name. It comes from upn,
        // preferred_username, sub in that order
        name = untrusted.getBody().get("upn", String.class);
        if (name == null) {
          name = untrusted.getBody().get("preferred_username", String.class);
          if (name == null) {
            name = untrusted.getBody().getSubject();
          }
        }
      } catch (Exception e) {
        jwtException = e;
      }
    }
    return name;
  }

  public Exception getJwtException() {
    return jwtException;
  }
}
