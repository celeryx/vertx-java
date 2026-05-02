package com.example.datasource_adapter.controllers;

import com.example.datasource_adapter.commons.web.OpenApiController;
import com.google.inject.Inject;
import io.vertx.ext.healthchecks.Status;
import io.vertx.rxjava3.core.Vertx;
import io.vertx.rxjava3.ext.web.RoutingContext;
import io.vertx.rxjava3.ext.web.healthchecks.HealthCheckHandler;

/**
 * HealthCheckController as a Java 21 Record.
 * Guice 7 supports record constructor injection out of the box.
 */
public record HealthCheckController(HealthCheckHandler healthCheckHandler) implements OpenApiController {

  @Inject
  public HealthCheckController(Vertx vertx) {
    this(HealthCheckHandler.create(vertx));
    healthCheckHandler.register("service", promise -> promise.complete(Status.OK()));
  }

  /**
   * Matches the operationId: healthcheck in openapi.yaml
   */
  public void healthcheck(RoutingContext ctx) {
    healthCheckHandler.handle(ctx);
  }
}
