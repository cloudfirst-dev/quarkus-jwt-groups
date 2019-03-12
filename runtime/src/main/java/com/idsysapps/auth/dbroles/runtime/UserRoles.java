package com.idsysapps.auth.dbroles.runtime;

import java.util.List;

@FunctionalInterface
public interface UserRoles {
  List<String> getRolesForUser(String username);
}
