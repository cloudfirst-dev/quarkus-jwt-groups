package com.idsysapps.auth.dbroles.deployment;

import java.io.IOException;
import java.security.interfaces.RSAPublicKey;

import org.wildfly.security.auth.server.SecurityRealm;

import com.idsysapps.auth.dbroles.runtime.DbRoleTemplate;
import com.idsysapps.auth.dbroles.runtime.UserRolesResolver;

import io.quarkus.arc.deployment.AdditionalBeanBuildItem;
import io.quarkus.arc.deployment.BeanContainerBuildItem;
import io.quarkus.arc.deployment.BeanDefiningAnnotationBuildItem;
import io.quarkus.deployment.annotations.BuildProducer;
import io.quarkus.deployment.annotations.BuildStep;
import io.quarkus.deployment.annotations.ExecutionTime;
import io.quarkus.deployment.annotations.Record;
import io.quarkus.deployment.builditem.FeatureBuildItem;
import io.quarkus.deployment.builditem.ObjectSubstitutionBuildItem;
import io.quarkus.deployment.builditem.substrate.ReflectiveClassBuildItem;
import io.quarkus.deployment.recording.RecorderContext;
import io.quarkus.elytron.security.deployment.AuthConfigBuildItem;
import io.quarkus.elytron.security.deployment.SecurityRealmBuildItem;
import io.quarkus.elytron.security.runtime.AuthConfig;
import io.quarkus.runtime.RuntimeValue;
import io.quarkus.smallrye.jwt.runtime.auth.ClaimAttributes;
import io.quarkus.smallrye.jwt.runtime.auth.ElytronJwtCallerPrincipal;
import io.quarkus.smallrye.jwt.runtime.auth.PublicKeyProxy;
import io.quarkus.smallrye.jwt.runtime.auth.PublicKeySubstitution;

class IdmUserRolesProcessor {
  @BuildStep
  void registerAdditionalBeans(BuildProducer<AdditionalBeanBuildItem> additionalBeans) {
    additionalBeans.produce(new AdditionalBeanBuildItem(false, UserRolesResolver.class));
  }

  @BuildStep
  @Record(ExecutionTime.STATIC_INIT)
  void build(RecorderContext recorder, BuildProducer<FeatureBuildItem> feature,
      BuildProducer<AdditionalBeanBuildItem> additionalBean,
      BuildProducer<BeanDefiningAnnotationBuildItem> beanDefiningAnnotation) throws IOException {

    feature.produce(new FeatureBuildItem(FeatureBuildItem.SMALLRYE_JWT));
  }


  @BuildStep
  @Record(ExecutionTime.STATIC_INIT)
  @SuppressWarnings({"unchecked", "rawtypes"})
  AuthConfigBuildItem configureFileRealmAuthConfig(DbRoleTemplate template,
      BuildProducer<ObjectSubstitutionBuildItem> objectSubstitution,
      BuildProducer<SecurityRealmBuildItem> securityRealm, BeanContainerBuildItem container,
      BuildProducer<ReflectiveClassBuildItem> reflectiveClasses) throws Exception {
    // if (config.enabled) {
    // RSAPublicKey needs to be serialized
    ObjectSubstitutionBuildItem.Holder pkHolder = new ObjectSubstitutionBuildItem.Holder(
        RSAPublicKey.class, PublicKeyProxy.class, PublicKeySubstitution.class);
    ObjectSubstitutionBuildItem pkSub = new ObjectSubstitutionBuildItem(pkHolder);
    objectSubstitution.produce(pkSub);
    // Have the runtime template create the TokenSecurityRealm and create the build item
    RuntimeValue<SecurityRealm> realm = template.createTokenRealm(container.getValue());
    AuthConfig authConfig = new AuthConfig();
    authConfig.setAuthMechanism("MP-JWT");
    authConfig.setRealmName("Quarkus-JWT");
    securityRealm.produce(new SecurityRealmBuildItem(realm, authConfig));

    reflectiveClasses
        .produce(new ReflectiveClassBuildItem(false, false, ClaimAttributes.class.getName()));
    reflectiveClasses.produce(
        new ReflectiveClassBuildItem(false, false, ElytronJwtCallerPrincipal.class.getName()));

    // Return the realm authentication mechanism build item
    return new AuthConfigBuildItem(authConfig);
  }

}
