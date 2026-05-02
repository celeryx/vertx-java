package com.example.datasource_adapter.controllers;

import com.example.datasource_adapter.commons.web.OpenApiController;
import com.example.datasource_adapter.models.dtos.warranties.WarrantyCreateRqDto;
import com.example.datasource_adapter.services.WarrantyManagerService;
import com.google.inject.Inject;
import io.vertx.core.json.JsonObject;
import io.vertx.rxjava3.ext.web.RoutingContext;

public class WarrantyController implements OpenApiController {

  private final WarrantyManagerService service;

  @Inject
  public WarrantyController(WarrantyManagerService service) {
    this.service = service;
  }

  public void createWarranty(RoutingContext ctx) {
    try {
      WarrantyCreateRqDto body = ctx.body().asJsonObject().mapTo(WarrantyCreateRqDto.class);
      var disposable = service.createWarranty(body)
        .subscribe(
          result -> sendResponse(ctx, JsonObject.mapFrom(result)),
          ctx::fail
        );
      ctx.addEndHandler(v -> disposable.dispose());
    } catch (Exception e) {
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
