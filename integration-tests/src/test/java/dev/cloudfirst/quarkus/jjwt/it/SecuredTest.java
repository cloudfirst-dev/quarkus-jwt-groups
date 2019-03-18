package dev.cloudfirst.quarkus.jjwt.it;

import static io.restassured.RestAssured.given;
import static org.hamcrest.CoreMatchers.is;

import java.io.IOException;
import java.security.GeneralSecurityException;

import org.junit.jupiter.api.Test;

import io.jsonwebtoken.security.InvalidKeyException;
import io.quarkus.test.junit.QuarkusTest;

@QuarkusTest
public class SecuredTest {
  @Test
  public void testHelloEndpoint()
      throws InvalidKeyException, IOException, GeneralSecurityException {
    given().when().header("Authorization", "bearer " + KeyUtils.generateToken())
        .get("/secured/roles-allowed").then().statusCode(200).body(is("test@example.com"));
  }
}
