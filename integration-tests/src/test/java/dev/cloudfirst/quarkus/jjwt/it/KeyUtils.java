package dev.cloudfirst.quarkus.jjwt.it;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.net.URL;
import java.security.GeneralSecurityException;
import java.security.Key;
import java.security.KeyFactory;
import java.security.interfaces.RSAPrivateKey;
import java.security.spec.PKCS8EncodedKeySpec;
import java.util.Arrays;
import java.util.Base64;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.InvalidKeyException;

public class KeyUtils {
  static String generateToken() throws InvalidKeyException, IOException, GeneralSecurityException {
    return Jwts.builder().setSubject("test@example.com")
        .claim("groups", Arrays.asList("user_admin")).signWith(getPrivateKey()).compact();
  }

  private static Key getPrivateKey() throws IOException, GeneralSecurityException {
    URL filePath = KeyUtils.class.getClassLoader().getResource("private_key.pem");

    return getPrivateKey(filePath.getPath());
  }

  private static String getKey(String filename) throws IOException {
    // Read key from file
    String strKeyPEM = "";
    BufferedReader br = new BufferedReader(new FileReader(filename));
    String line;
    while ((line = br.readLine()) != null) {
      strKeyPEM += line + "\n";
    }
    br.close();
    return strKeyPEM;
  }

  private static RSAPrivateKey getPrivateKey(String filename)
      throws IOException, GeneralSecurityException {
    String privateKeyPEM = getKey(filename);
    return getPrivateKeyFromString(privateKeyPEM);
  }

  private static RSAPrivateKey getPrivateKeyFromString(String key)
      throws IOException, GeneralSecurityException {
    String privateKeyPEM = key;
    privateKeyPEM = privateKeyPEM.replace("-----BEGIN PRIVATE KEY-----\n", "");
    privateKeyPEM = privateKeyPEM.replace("-----END PRIVATE KEY-----", "");
    privateKeyPEM = privateKeyPEM.replaceAll("\\n", "");
    byte[] encoded = Base64.getDecoder().decode(privateKeyPEM);
    KeyFactory kf = KeyFactory.getInstance("RSA");
    PKCS8EncodedKeySpec keySpec = new PKCS8EncodedKeySpec(encoded);
    RSAPrivateKey privKey = (RSAPrivateKey) kf.generatePrivate(keySpec);
    return privKey;
  }
}
