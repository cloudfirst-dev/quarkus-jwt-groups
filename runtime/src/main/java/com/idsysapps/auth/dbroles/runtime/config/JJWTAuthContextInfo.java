package com.idsysapps.auth.dbroles.runtime.config;

import java.security.interfaces.RSAPublicKey;

public class JJWTAuthContextInfo {
  private RSAPublicKey signerKey;
  private String issuedBy;
  private int expGracePeriodSecs = 60;
  private String jwksUri;
  private Integer jwksRefreshInterval;

  /**
   * Flag that indicates whether the issuer is required and validated, or ignored, new in MP-JWT
   * 1.1.
   */
  private boolean requireIssuer = true;

  public JJWTAuthContextInfo() {}

  /**
   * Create an auth context from the token signer public key and issuer
   * 
   * @param signerKey
   * @param issuedBy
   */
  public JJWTAuthContextInfo(RSAPublicKey signerKey, String issuedBy) {
    this.signerKey = signerKey;
    this.issuedBy = issuedBy;
  }

  public RSAPublicKey getSignerKey() {
    return signerKey;
  }

  public void setSignerKey(RSAPublicKey signerKey) {
    this.signerKey = signerKey;
  }

  public String getIssuedBy() {
    return issuedBy;
  }

  public void setIssuedBy(String issuedBy) {
    this.issuedBy = issuedBy;
  }

  public int getExpGracePeriodSecs() {
    return expGracePeriodSecs;
  }

  public void setExpGracePeriodSecs(int expGracePeriodSecs) {
    this.expGracePeriodSecs = expGracePeriodSecs;
  }

  public String getJwksUri() {
    return jwksUri;
  }

  public void setJwksUri(String jwksUri) {
    this.jwksUri = jwksUri;
  }

  public Integer getJwksRefreshInterval() {
    return jwksRefreshInterval;
  }

  public void setJwksRefreshInterval(Integer jwksRefreshInterval) {
    this.jwksRefreshInterval = jwksRefreshInterval;
  }

  public boolean isRequireIssuer() {
    return requireIssuer;
  }

  public void setRequireIssuer(boolean requireIssuer) {
    this.requireIssuer = requireIssuer;
  }

}
