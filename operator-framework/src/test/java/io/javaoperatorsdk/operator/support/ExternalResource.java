package io.javaoperatorsdk.operator.support;

import java.util.Objects;

import io.fabric8.kubernetes.api.model.HasMetadata;
import io.javaoperatorsdk.operator.processing.event.ResourceID;

public class ExternalResource {

  public static final String EXTERNAL_RESOURCE_NAME_DELIMITER = "#";

  // id can be either provided or generated, depends on service type
  private String id;
  private final String data;

  public ExternalResource(String data) {
    this(null, data);
  }

  public ExternalResource(String id, String data) {
    this.id = id;
    this.data = data;
  }

  public String getId() {
    return id;
  }

  public void setId(String id) {
    this.id = id;
  }

  public String getData() {
    return data;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o)
      return true;
    if (o == null || getClass() != o.getClass())
      return false;
    ExternalResource that = (ExternalResource) o;
    return Objects.equals(id, that.id) && Objects.equals(data, that.data);
  }

  @Override
  public int hashCode() {
    return Objects.hash(id, data);
  }

  public ResourceID toResourceID() {
    var parts = getId().split(EXTERNAL_RESOURCE_NAME_DELIMITER);
    return new ResourceID(parts[0], parts[1]);
  }

  public static String toExternalResourceId(HasMetadata primary, int i) {
    return primary.getMetadata().getName() + EXTERNAL_RESOURCE_NAME_DELIMITER +
        primary.getMetadata().getNamespace() +
        EXTERNAL_RESOURCE_NAME_DELIMITER + i;
  }

  public static String toExternalResourceId(HasMetadata primary) {
    return primary.getMetadata().getName() + EXTERNAL_RESOURCE_NAME_DELIMITER +
        primary.getMetadata().getNamespace();
  }
}