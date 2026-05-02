package com.example.datasource_adapter.commons.web;

import io.vertx.rxjava3.ext.web.RoutingContext;
import io.vertx.rxjava3.ext.web.openapi.router.RouterBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Arrays;

public interface OpenApiController {

  /**
   * Automatically binds Java methods to OpenAPI operation IDs using Reflection.
   * Convention: The Java method name MUST match the OpenAPI operationId exactly.
   */
  default void bindOperations(RouterBuilder builder) {
    Logger log = LoggerFactory.getLogger(this.getClass());

    Arrays.stream(this.getClass().getDeclaredMethods())
      .filter(method -> method.getParameterCount() == 1)
      .filter(method -> method.getParameterTypes()[0].equals(RoutingContext.class))
      .forEach(method -> {
      String operationId = method.getName();

      try {
        builder.getRoute(operationId).addHandler(ctx -> {
          try {
            method.invoke(this, ctx);
          } catch (Exception e) {
            ctx.fail(500, e);
          }
        });

        log.info("✅ Auto-bound OpenAPI operation: {}", operationId);

      } catch (IllegalArgumentException | NullPointerException ignored) {
        // 3. Fallback to standard Java practice for intentionally ignored exceptions without preview features
      }
    });
  }
}
