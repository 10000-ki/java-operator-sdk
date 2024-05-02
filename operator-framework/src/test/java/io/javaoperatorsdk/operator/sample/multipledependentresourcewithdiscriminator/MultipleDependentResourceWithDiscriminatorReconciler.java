package io.javaoperatorsdk.operator.sample.multipledependentresourcewithdiscriminator;

import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import io.fabric8.kubernetes.api.model.ConfigMap;
import io.javaoperatorsdk.operator.api.config.informer.InformerConfiguration;
import io.javaoperatorsdk.operator.api.reconciler.*;
import io.javaoperatorsdk.operator.processing.event.source.EventSource;
import io.javaoperatorsdk.operator.processing.event.source.informer.InformerEventSource;
import io.javaoperatorsdk.operator.support.TestExecutionInfoProvider;

@ControllerConfiguration
public class MultipleDependentResourceWithDiscriminatorReconciler
    implements Reconciler<MultipleDependentResourceCustomResourceWithDiscriminator>,
    TestExecutionInfoProvider {

  public static final int FIRST_CONFIG_MAP_ID = 1;
  public static final int SECOND_CONFIG_MAP_ID = 2;
  private final AtomicInteger numberOfExecutions = new AtomicInteger(0);

  private final MultipleDependentResourceConfigMap firstDependentResourceConfigMap;
  private final MultipleDependentResourceConfigMap secondDependentResourceConfigMap;

  public MultipleDependentResourceWithDiscriminatorReconciler() {
    firstDependentResourceConfigMap = new MultipleDependentResourceConfigMap(FIRST_CONFIG_MAP_ID);
    secondDependentResourceConfigMap = new MultipleDependentResourceConfigMap(SECOND_CONFIG_MAP_ID);
  }

  @Override
  public UpdateControl<MultipleDependentResourceCustomResourceWithDiscriminator> reconcile(
      MultipleDependentResourceCustomResourceWithDiscriminator resource,
      Context<MultipleDependentResourceCustomResourceWithDiscriminator> context) {
    numberOfExecutions.getAndIncrement();
    firstDependentResourceConfigMap.reconcile(resource, context);
    secondDependentResourceConfigMap.reconcile(resource, context);
    return UpdateControl.noUpdate();
  }


  public int getNumberOfExecutions() {
    return numberOfExecutions.get();
  }

  @Override
  public List<EventSource> prepareEventSources(
      EventSourceContext<MultipleDependentResourceCustomResourceWithDiscriminator> context) {
    InformerEventSource<ConfigMap, MultipleDependentResourceCustomResourceWithDiscriminator> eventSource =
        new InformerEventSource<>(InformerConfiguration.from(ConfigMap.class, context)
            .build(), context);
    firstDependentResourceConfigMap.configureWith(eventSource);
    secondDependentResourceConfigMap.configureWith(eventSource);

    return List.of(eventSource);
  }
}