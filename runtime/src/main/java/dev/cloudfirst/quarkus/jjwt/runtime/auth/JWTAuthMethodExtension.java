package dev.cloudfirst.quarkus.jjwt.runtime.auth;

import javax.servlet.ServletContext;

import io.undertow.servlet.ServletExtension;
import io.undertow.servlet.api.DeploymentInfo;

public class JWTAuthMethodExtension implements ServletExtension {
  private String authMechanism;

  public String getAuthMechanism() {
    return authMechanism;
  }

  public void setAuthMechanism(String authMechanism) {
    this.authMechanism = authMechanism;
  }

  /**
   * This registers the JWTAuthMechanismFactory under the "MP-JWT" mechanism name
   *
   * @param deploymentInfo - the deployment to augment
   * @param servletContext - the ServletContext for the deployment
   */
  @Override
  public void handleDeployment(DeploymentInfo deploymentInfo, ServletContext servletContext) {
    deploymentInfo.addAuthenticationMechanism(authMechanism, new JWTAuthMechanismFactory());
    deploymentInfo.addInnerHandlerChainWrapper(MpJwtPrincipalHandler::new);
  }
}
