package dev.cloudfirst.quarkus.jjwt.runtime.auth;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.jboss.logging.Logger;
import org.wildfly.security.authz.Attributes;
import org.wildfly.security.authz.SimpleAttributesEntry;

public class ClaimAttributes implements Attributes {
  private static Logger log = Logger.getLogger(ClaimAttributes.class);
  private final io.jsonwebtoken.Claims claimsSet;
  private HashMap<String, Entry> entries = new HashMap<>();
  private final String rawToken;
  private final String tokenType;

  public ClaimAttributes(String rawToken, String tokenType, io.jsonwebtoken.Claims claimsSet) {
    // super(claimsSet);
    this.claimsSet = claimsSet;
    this.rawToken = rawToken;
    this.tokenType = tokenType;

    populateEntries();
  }

  public String getRawToken() {
    return rawToken;
  }

  public String getTokenType() {
    return tokenType;
  }

  @Override
  public Collection<Entry> entries() {
    return entries.values();
  }

  @Override
  public int size(String key) {
    int size = 0;
    try {
      Object objectValue = claimsSet.get(key);
      if (objectValue instanceof List) {
        size = ((List) objectValue).size();
      } else {
        size = 1;
      }
    } catch (Exception e) {
      e.printStackTrace();
      log.error(e);
    }

    return size;
  }

  @Override
  public Entry get(String key) {
    return entries.get(key);
  }

  private boolean isClaimValueStringList(String key) {
    try {
      Object o = claimsSet.get(key);
      List.class.cast(o);
    } catch (ClassCastException e) {
      return false;
    }

    return true;
  }

  private boolean isClaimValueString(String key) {
    try {
      Object o = claimsSet.get(key);
      String.class.cast(o);
    } catch (ClassCastException e) {
      return false;
    }

    return true;
  }

  @Override
  public String get(String key, int idx) {
    String value = null;
    try {
      if (isClaimValueStringList(key)) {
        List<String> values = claimsSet.get(key, List.class);
        try {
          value = Optional.ofNullable(values).orElse(new ArrayList<>()).get(idx);
        } catch (IndexOutOfBoundsException ex) {
          log.warn("index not found", ex);
          value = null;
        }
      } else if (isClaimValueString(key) && idx == 0) {
        value = claimsSet.get(key, String.class);
      } else {
        Object objectValue = claimsSet.get(key);
        if (objectValue instanceof List) {
          value = ((List) objectValue).get(idx).toString();
        } else {
          value = objectValue.toString();
        }
      }
    } catch (Exception e) {
      log.error(e);
      e.printStackTrace();
    }
    return value;
  }

  @Override
  public int size() {
    return entries.size();
  }

  public io.jsonwebtoken.Claims getClaimsSet() {
    return claimsSet;
  }

  /**
   *
   */
  private void populateEntries() {
    for (Map.Entry<String, Object> entry : claimsSet.entrySet()) {
      String key = entry.getKey();

      SimpleAttributesEntry attributesEntry = new SimpleAttributesEntry(this, key);
      entries.put(key, attributesEntry);
    }
  }
}
