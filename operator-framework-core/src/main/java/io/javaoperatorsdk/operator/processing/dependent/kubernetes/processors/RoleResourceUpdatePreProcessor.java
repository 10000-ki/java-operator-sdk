package io.javaoperatorsdk.operator.processing.dependent.kubernetes.processors;

import java.util.Objects;

import io.fabric8.kubernetes.api.model.rbac.Role;
import io.javaoperatorsdk.operator.api.reconciler.Context;

public class RoleResourceUpdatePreProcessor extends GenericResourceUpdatePreProcessor<Role> {

  @Override
  protected void updateClonedActual(Role actual, Role desired) {
    actual.setRules(desired.getRules());
  }

  @Override
  public boolean matches(Role actual, Role desired, boolean equality, Context<?> context,
      String[] ignoredPaths) {
    return Objects.equals(actual.getRules(), desired.getRules());
  }
}