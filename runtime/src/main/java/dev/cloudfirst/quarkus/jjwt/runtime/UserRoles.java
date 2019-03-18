package dev.cloudfirst.quarkus.jjwt.runtime;

import java.util.List;

@FunctionalInterface
public interface UserRoles {
  List<String> getRolesForUser(String username);
}
