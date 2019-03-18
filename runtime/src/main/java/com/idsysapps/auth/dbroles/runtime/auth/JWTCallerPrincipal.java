package com.idsysapps.auth.dbroles.runtime.auth;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import org.eclipse.microprofile.jwt.Claims;
import org.eclipse.microprofile.jwt.JsonWebToken;

public abstract class JWTCallerPrincipal implements JsonWebToken {
  private String rawToken;
  private String tokenType;

  public JWTCallerPrincipal(String rawToken, String tokenType) {
    this.rawToken = rawToken;
    this.tokenType = tokenType;
  }

  @Override
  public Set<String> getClaimNames() {
    Set<String> names = new HashSet<>(doGetClaimNames());
    names.add(Claims.raw_token.name());
    return names;
  }

  @Override
  public <T> T getClaim(String claimName) {
    @SuppressWarnings("unchecked")
    T claimValue =
        Claims.raw_token.name().equals(claimName) ? (T) rawToken : (T) getClaimValue(claimName);
    return claimValue;
  }

  protected abstract Object getClaimValue(String claimName);

  protected abstract Collection<String> doGetClaimNames();

  @Override
  public String toString() {
    return toString(true);
  }

  public String toString(boolean showAll) {
    String toString = "DefaultJWTCallerPrincipal{" + "id='" + getTokenID() + '\'' + ", name='"
        + getName() + '\'' + ", expiration=" + getExpirationTime() + ", notBefore="
        + getClaim(Claims.nbf.name()) + ", issuedAt=" + getIssuedAtTime() + ", issuer='"
        + getIssuer() + '\'' + ", audience=" + getAudience() + ", subject='" + getSubject() + '\''
        + ", type='" + tokenType + '\'' + ", issuedFor='" + getClaim("azp") + '\'' + ", authTime="
        + getClaim("auth_time") + ", givenName='" + getClaim("given_name") + '\'' + ", familyName='"
        + getClaim("family_name") + '\'' + ", middleName='" + getClaim("middle_name") + '\''
        + ", nickName='" + getClaim("nickname") + '\'' + ", preferredUsername='"
        + getClaim("preferred_username") + '\'' + ", email='" + getClaim("email") + '\''
        + ", emailVerified=" + getClaim(Claims.email_verified.name()) + ", allowedOrigins="
        + getClaim("allowedOrigins") + ", updatedAt=" + getClaim("updated_at") + ", acr='"
        + getClaim("acr") + '\'';
    StringBuilder tmp = new StringBuilder(toString);
    tmp.append(", groups=[");
    for (String group : getGroups()) {
      tmp.append(group);
      tmp.append(',');
    }
    tmp.setLength(tmp.length() - 1);
    tmp.append("]}");
    return tmp.toString();
  }

  @Override
  public String getName() {
    String principalName = getClaim(Claims.upn.name());
    if (principalName == null) {
      principalName = getClaim(Claims.preferred_username.name());
      if (principalName == null) {
        principalName = getClaim(Claims.sub.name());
      }
    }
    return principalName;
  }
}
