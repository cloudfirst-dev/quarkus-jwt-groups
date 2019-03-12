package com.idsysapps.auth.dbroles.deployment;

import java.util.List;

import javax.enterprise.context.Dependent;

import com.idsysapps.auth.dbroles.runtime.UserRoleProvider;
import com.idsysapps.auth.dbroles.runtime.UserRoles;

@Dependent
@UserRoleProvider
public class TestUserRoles implements UserRoles {

  @Override
  public List<String> getRolesForUser(String username) {
    // TODO Auto-generated method stub
    return null;
  }

}
