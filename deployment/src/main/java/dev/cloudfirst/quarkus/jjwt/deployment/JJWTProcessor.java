package dev.cloudfirst.quarkus.jjwt.deployment;

import org.wildfly.security.auth.server.SecurityRealm;

import dev.cloudfirst.quarkus.jjwt.runtime.JJWTTemplate;
import dev.cloudfirst.quarkus.jjwt.runtime.PrincipalProducer;
import dev.cloudfirst.quarkus.jjwt.runtime.UserRolesResolver;
import dev.cloudfirst.quarkus.jjwt.runtime.auth.ElytronJwtCallerPrincipal;
import dev.cloudfirst.quarkus.jjwt.runtime.auth.JJWTValidator;
import dev.cloudfirst.quarkus.jjwt.runtime.config.JJWTAuthContextInfoProvider;
import io.quarkus.arc.deployment.AdditionalBeanBuildItem;
import io.quarkus.arc.deployment.BeanContainerBuildItem;
import io.quarkus.deployment.annotations.BuildProducer;
import io.quarkus.deployment.annotations.BuildStep;
import io.quarkus.deployment.annotations.ExecutionTime;
import io.quarkus.deployment.annotations.Record;
import io.quarkus.deployment.builditem.FeatureBuildItem;
import io.quarkus.deployment.builditem.ObjectSubstitutionBuildItem;
import io.quarkus.deployment.builditem.substrate.ReflectiveClassBuildItem;
import io.quarkus.elytron.security.deployment.AuthConfigBuildItem;
import io.quarkus.elytron.security.deployment.IdentityManagerBuildItem;
import io.quarkus.elytron.security.deployment.SecurityDomainBuildItem;
import io.quarkus.elytron.security.deployment.SecurityRealmBuildItem;
import io.quarkus.elytron.security.runtime.AuthConfig;
import io.quarkus.runtime.RuntimeValue;
import io.quarkus.undertow.deployment.ServletExtensionBuildItem;
import io.undertow.security.idm.IdentityManager;
import io.undertow.servlet.ServletExtension;

class JJWTProcessor {
  @BuildStep
  void registerAdditionalBeans(BuildProducer<AdditionalBeanBuildItem> additionalBeans) {
    AdditionalBeanBuildItem.Builder unremovable =
        AdditionalBeanBuildItem.builder().setUnremovable();
    unremovable.addBeanClass(UserRolesResolver.class);
    unremovable.addBeanClass(JJWTValidator.class);
    unremovable.addBeanClass(PrincipalProducer.class);
    additionalBeans.produce(unremovable.build());

    AdditionalBeanBuildItem.Builder removable = AdditionalBeanBuildItem.builder();
    removable.addBeanClass(JJWTAuthContextInfoProvider.class);
    additionalBeans.produce(removable.build());

  }

  @BuildStep
  FeatureBuildItem feature() {
    return new FeatureBuildItem("jwt-groups");
  }


  @BuildStep
  @Record(ExecutionTime.STATIC_INIT)
  @SuppressWarnings({"unchecked", "rawtypes"})
  AuthConfigBuildItem configureFileRealmAuthConfig(JJWTTemplate template,
      BuildProducer<ObjectSubstitutionBuildItem> objectSubstitution,
      BuildProducer<SecurityRealmBuildItem> securityRealm, BeanContainerBuildItem container,
      BuildProducer<ReflectiveClassBuildItem> reflectiveClasses) throws Exception {
    // if (config.enabled) {
    // Have the runtime template create the TokenSecurityRealm and create the build item
    RuntimeValue<SecurityRealm> realm = template.createTokenRealm(container.getValue());
    AuthConfig authConfig = new AuthConfig();
    authConfig.setAuthMechanism("MP-JWT");
    authConfig.setRealmName("Quarkus-JWT");
    securityRealm.produce(new SecurityRealmBuildItem(realm, authConfig));

    reflectiveClasses.produce(
        new ReflectiveClassBuildItem(false, false, ElytronJwtCallerPrincipal.class.getName()));
    reflectiveClasses.produce(new ReflectiveClassBuildItem(false, false,
        io.jsonwebtoken.impl.DefaultJwtParser.class.getName()));
    reflectiveClasses.produce(new ReflectiveClassBuildItem(false, false,
        io.jsonwebtoken.impl.io.RuntimeClasspathDeserializerLocator.class.getName()));
    reflectiveClasses.produce(new ReflectiveClassBuildItem(false, false,
        com.fasterxml.jackson.databind.ObjectMapper.class.getName()));

    reflectiveClasses.produce(new ReflectiveClassBuildItem(false, false,
        io.jsonwebtoken.io.JacksonDeserializer.class.getName()));



    // Return the realm authentication mechanism build item
    return new AuthConfigBuildItem(authConfig);
  }

  @BuildStep
  @Record(ExecutionTime.STATIC_INIT)
  void configureIdentityManager(JJWTTemplate template, SecurityDomainBuildItem securityDomain,
      BuildProducer<IdentityManagerBuildItem> identityManagerProducer) {
    IdentityManager identityManager =
        template.createIdentityManager(securityDomain.getSecurityDomain());
    identityManagerProducer.produce(new IdentityManagerBuildItem(identityManager));
  }

  @BuildStep
  @Record(ExecutionTime.STATIC_INIT)
  ServletExtensionBuildItem registerJwtAuthExtension(JJWTTemplate template,
      BeanContainerBuildItem container) {
    // logger.debugf("registerJwtAuthExtension");
    ServletExtension authExt = template.createAuthExtension("MP-JWT", container.getValue());
    ServletExtensionBuildItem sebi = new ServletExtensionBuildItem(authExt);
    return sebi;
  }
}
