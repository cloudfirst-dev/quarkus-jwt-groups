package com.idsysapps.auth.dbroles.runtime.config;

import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.net.URL;
import java.security.KeyFactory;
import java.security.PublicKey;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.X509EncodedKeySpec;
import java.util.Optional;

import javax.enterprise.context.Dependent;
import javax.enterprise.inject.Produces;
import javax.enterprise.inject.spi.DeploymentException;
import javax.inject.Inject;

import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.jboss.logging.Logger;

@Dependent
public class JJWTAuthContextInfoProvider {
  private static final String NONE = "NONE";
  private static final Logger log = Logger.getLogger(JJWTAuthContextInfoProvider.class);

  @Inject
  @ConfigProperty(name = "mp.jjwt.verify.publickey", defaultValue = NONE)
  private Optional<String> mpJjwtPublicKey;

  @Inject
  @ConfigProperty(name = "mp.jjwt.verify.publickey.location", defaultValue = NONE)
  private Optional<String> mpJjwtLocation;

  @Inject
  @ConfigProperty(name = "mp.jjwt.verify.issuer", defaultValue = NONE)
  private String mpJjwtIssuer;

  @Inject
  @ConfigProperty(name = "mp.jjwt.verify.requireiss", defaultValue = "true")
  private Optional<Boolean> mpJjwtRequireIss;

  @Produces
  Optional<JJWTAuthContextInfo> getOptionalContextInfo() {
    // Log the config values
    log.debugf("init, mpJjwtPublicKey=%s, mpJjwtIssuer=%s, mpJjwtLocation=%s",
        mpJjwtPublicKey.orElse("missing"), mpJjwtIssuer, mpJjwtLocation.orElse("missing"));

    JJWTAuthContextInfo contextInfo = new JJWTAuthContextInfo();
    // Look to MP-JJWT values first
    if (mpJjwtPublicKey.isPresent() && !NONE.equals(mpJjwtPublicKey.get())) {
      // Need to decode what this is...
      try {
        RSAPublicKey pk = (RSAPublicKey) getPemPublicKey(mpJjwtPublicKey.get(), "RSA");
        contextInfo.setSignerKey(pk);
        log.debugf("mpJjwtPublicKey parsed as PEM");
      } catch (Exception e1) {
        throw new DeploymentException(e1);
      }
    }

    if (mpJjwtLocation.isPresent() && !NONE.equals(mpJjwtLocation.get())) {
      // Need to decode what this is...
      try {
        String publicKeyPath = mpJjwtLocation.get();
        if (publicKeyPath.startsWith("classpath:")) {
          publicKeyPath = publicKeyPath.replace("classpath:", "");
          URL publicKeyUrl =
              JJWTAuthContextInfoProvider.class.getClassLoader().getResource(publicKeyPath);
          publicKeyPath = publicKeyUrl.getFile();
        }

        RSAPublicKey pk = (RSAPublicKey) getPemPublicKey(publicKeyPath, "RSA");
        contextInfo.setSignerKey(pk);
        log.debugf("mpJjwtPublicKey parsed as PEM");
      } catch (Exception e1) {
        throw new DeploymentException(e1);
      }
    }

    if (mpJjwtIssuer != null && !mpJjwtIssuer.equals(NONE)) {
      contextInfo.setIssuedBy(mpJjwtIssuer);
    } else {
      // If there is no expected issuer configured, don't validate it; new in MP-JWT 1.1
      contextInfo.setRequireIssuer(false);
    }

    if (mpJjwtRequireIss != null && mpJjwtRequireIss.isPresent()) {
      contextInfo.setRequireIssuer(mpJjwtRequireIss.get());
    } else {
      // Default is to require iss claim
      contextInfo.setRequireIssuer(true);
    }

    // The MP-JWT location can be a PEM, JWK or JWKS
    if (mpJjwtLocation.isPresent() && !NONE.equals(mpJjwtLocation.get())) {
      contextInfo.setJwksUri(mpJjwtLocation.get());
    }

    return Optional.of(contextInfo);
  }

  private PublicKey getPemPublicKey(String filename, String algorithm) throws Exception {
    File f = new File(filename);
    FileInputStream fis = new FileInputStream(f);
    DataInputStream dis = new DataInputStream(fis);
    byte[] keyBytes = new byte[(int) f.length()];
    dis.readFully(keyBytes);
    dis.close();

    String temp = new String(keyBytes);
    String publicKeyPEM = temp.replace("-----BEGIN PUBLIC KEY-----\n", "");
    publicKeyPEM = publicKeyPEM.replace("-----END PUBLIC KEY-----", "");
    publicKeyPEM = publicKeyPEM.replaceAll("\\n", "");

    byte[] decoded = java.util.Base64.getDecoder().decode(publicKeyPEM);

    X509EncodedKeySpec spec = new X509EncodedKeySpec(decoded);
    KeyFactory kf = KeyFactory.getInstance(algorithm);
    return kf.generatePublic(spec);
  }

  @Produces
  public JJWTAuthContextInfo getContextInfo() {
    return getOptionalContextInfo().get();
  }
}
