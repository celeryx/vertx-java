package com.example.datasource_adapter.controllers;

import com.example.datasource_adapter.commons.web.OpenApiController;
import com.example.datasource_adapter.models.dtos.warranties.WarrantyCreateRqDto;
import com.example.datasource_adapter.services.WarrantyManagerService;
import com.google.inject.Inject;
import io.vertx.core.json.JsonObject;
import io.vertx.openapi.validation.ValidatedRequest;
import io.vertx.rxjava3.ext.web.RoutingContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class WarrantyController implements OpenApiController {
  Logger log = LoggerFactory.getLogger(this.getClass());
  private final WarrantyManagerService service;

  @Inject
  public WarrantyController(WarrantyManagerService service) {
    this.service = service;
  }

  public void createWarranty(RoutingContext ctx) {
    try {
      // 1. Use the EXACT key from your debug logs
      ValidatedRequest validated = ctx.get("openApiValidatedRequest");

      // 2. Check if the object was found
      if (validated == null) {
        log.error("❌ ValidatedRequest not found in context. Check if operationId matches YAML.");
        ctx.fail(400, new RuntimeException("OpenAPI context missing"));
        return;
      }

      // 3. Extract the body (ValidatedRequest uses .body() in V5)
      if (validated.getBody() == null || validated.getBody().get() == null) {
        log.error("❌ OpenAPI Validator found no body content.");
        ctx.fail(400, new RuntimeException("Empty or invalid request body"));
        return;
      }

      // 4. Get the JsonObject and map to your DTO
      JsonObject jsonBody = (JsonObject) validated.getBody().get();
      WarrantyCreateRqDto body = jsonBody.mapTo(WarrantyCreateRqDto.class);

      // 5. Proceed to service
      var disposable = service.createWarranty(body)
        .subscribe(
          result -> sendResponse(ctx, JsonObject.mapFrom(result)),
          ctx::fail
        );

      ctx.addEndHandler(v -> disposable.dispose());

    } catch (Exception e) {
      log.error("Mapping Error: ", e);
      ctx.fail(400, e);
    }
  }

  public void updateWarrantyById(RoutingContext ctx) {
    String id = ctx.pathParam("id");
    JsonObject body = ctx.body().asJsonObject();
    sendResponse(ctx, new JsonObject().put("id", id).put("action", "updateWarrantyById"));
  }

  public void replaceWarrantyById(RoutingContext ctx) {
    String id = ctx.pathParam("id");
    JsonObject body = ctx.body().asJsonObject();
    sendResponse(ctx, new JsonObject().put("id", id).put("action", "replaceWarrantyById"));
  }

  // --- Utility ---

  private void sendResponse(RoutingContext ctx, JsonObject payload) {
    var unused = ctx.response()
      .putHeader("content-type", "application/json")
      .end(payload.encode())
      .subscribe(
        () -> { /* Complete */ },
        err -> ctx.fail(500, err)
      );
  }
}
