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
      ValidatedRequest validated = ctx.get("openApiValidatedRequest");

      if (validated == null) {
        ctx.fail(400, new RuntimeException("OpenAPI context missing"));
        return;
      }

      if (validated.getBody() != null && validated.getBody().get() instanceof JsonObject jsonBody) {
        WarrantyCreateRqDto body = jsonBody.mapTo(WarrantyCreateRqDto.class);

        var disposable = service.createWarranty(body)
          .subscribe(
            result -> sendResponse(ctx, 201, JsonObject.mapFrom(result)), // Used the DRY method
            ctx::fail
          );
        ctx.addEndHandler(v -> disposable.dispose());
      } else {
        ctx.fail(400, new RuntimeException("Empty or invalid request body"));
      }
    } catch (Exception e) {
      log.error("Mapping Error: ", e);
      ctx.fail(400, e);
    }
  }

}
