package io.javaoperatorsdk.operator.sample.specialresourcesdependent;

import java.util.concurrent.atomic.AtomicInteger;

import io.javaoperatorsdk.operator.api.reconciler.*;
import io.javaoperatorsdk.operator.api.reconciler.dependent.Dependent;
import io.javaoperatorsdk.operator.api.reconciler.workflow.Workflow;
import io.javaoperatorsdk.operator.support.TestExecutionInfoProvider;

@ControllerConfiguration(
    namespaces = Constants.WATCH_CURRENT_NAMESPACE,
    workflow = @Workflow(dependents = {
        @Dependent(type = ServiceAccountDependentResource.class),
    }))
public class SpecialResourceTestReconciler
    implements Reconciler<SpecialResourceCustomResource>,
    TestExecutionInfoProvider {

  private final AtomicInteger numberOfExecutions = new AtomicInteger(0);

  @Override
  public UpdateControl<SpecialResourceCustomResource> reconcile(
      SpecialResourceCustomResource resource,
      Context<SpecialResourceCustomResource> context) {
    numberOfExecutions.addAndGet(1);
    return UpdateControl.noUpdate();
  }

  public int getNumberOfExecutions() {
    return numberOfExecutions.get();
  }

}
