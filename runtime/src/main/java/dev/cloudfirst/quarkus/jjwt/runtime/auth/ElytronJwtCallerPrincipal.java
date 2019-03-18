package dev.cloudfirst.quarkus.jjwt.runtime.auth;

import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.jboss.logging.Logger;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.RequiredTypeException;

public class ElytronJwtCallerPrincipal extends JWTCallerPrincipal {
  private static final String TMP = "tmp";
  private static Logger logger = Logger.getLogger(ElytronJwtCallerPrincipal.class);

  private Claims claimsSet;

  public ElytronJwtCallerPrincipal(final ClaimAttributes claims) {
    super(claims.getRawToken(), claims.getTokenType());
    this.claimsSet = ((ClaimAttributes) claims).getClaimsSet();
  }

  public ElytronJwtCallerPrincipal(final String rawToken, final String tokenType,
      final Claims claimsSet) {
    super(rawToken, tokenType);
    this.claimsSet = claimsSet;
  }

  @Override
  protected Object getClaimValue(String claimName) {
    org.eclipse.microprofile.jwt.Claims claimType = org.eclipse.microprofile.jwt.Claims.UNKNOWN;
    Object claim = null;
    try {
      claimType = org.eclipse.microprofile.jwt.Claims.valueOf(claimName);
    } catch (IllegalArgumentException e) {
    }
    // Handle the jose4j NumericDate types and
    switch (claimType) {
      case exp:
      case iat:
      case auth_time:
      case nbf:
      case updated_at:
        try {
          claim = claimsSet.get(claimType.name(), Long.class);
          if (claim == null) {
            claim = new Long(0);
          }
        } catch (RequiredTypeException e) {
          logger.warn(e);
        }
        break;
      case groups:
        claim = getGroups();
        break;
      case aud:
        claim = getAudience();
        break;
      case UNKNOWN:
        // This has to be a Json type
        claim = claimsSet.get(claimName);
        break;
      default:
        claim = claimsSet.get(claimType.name());
    }
    return claim;
  }

  @Override
  public Set<String> getAudience() {
    Set<String> audSet = null;
    try {
      if (claimsSet.containsKey(org.eclipse.microprofile.jwt.Claims.aud.name())) {
        List<String> audList = claimsSet.get("aud", List.class);
        audSet = new HashSet<>(audList);
      }
    } catch (RequiredTypeException e) {
      try {
        // Not sent as an array, try a single value
        String aud = claimsSet.get("aud", String.class);
        audSet = new HashSet<>();
        audSet.add(aud);
      } catch (RequiredTypeException e1) {
      }
    }
    return audSet;
  }

  @Override
  public Set<String> getGroups() {
    HashSet<String> groups = new HashSet<>();
    List<String> globalGroups = claimsSet.get("groups", List.class);
    if (globalGroups != null) {
      groups.addAll(globalGroups);
    }
    return groups;
  }

  @Override
  protected Collection<String> doGetClaimNames() {
    return new HashSet<>(claimsSet.keySet());
  }
}
