package com.idsysapps.auth.dbroles.deployment;

import javax.enterprise.inject.Instance;
import javax.inject.Inject;

import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.spec.JavaArchive;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.RegisterExtension;

import com.idsysapps.auth.dbroles.runtime.UserRoleProvider;
import com.idsysapps.auth.dbroles.runtime.UserRoles;
import com.idsysapps.auth.dbroles.runtime.UserRolesResolver;

import io.quarkus.test.QuarkusUnitTest;

public class EndpointTest {
  @RegisterExtension
  static QuarkusUnitTest runner = new QuarkusUnitTest()
      .setArchiveProducer(() -> ShrinkWrap.create(JavaArchive.class).addClass(TestUserRoles.class));


  @Inject
  @UserRoleProvider
  Instance<UserRoles> checks;

  @Inject
  UserRolesResolver resolver;

  @Test
  public void getRoles() {
    checks.get().getRolesForUser("abc123");
  }
}
