package jkube.operator.api;

import io.fabric8.kubernetes.client.CustomResource;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE})
public @interface Controller {

    String DEFAULT_FINALIZER = "operator.default.finalizer";

    String apiVersion() default "apiextensions.k8s.io/v1beta1";

    String crdVersion() default "v1";

    // todo we can get this from the class generic
    Class<? extends CustomResource> customResourceClass();

    String defaultFinalizer() default DEFAULT_FINALIZER;

    String customResourceDefinitionName();
}
