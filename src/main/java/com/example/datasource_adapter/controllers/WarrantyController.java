package com.example.datasource_adapter.controllers;

import com.example.datasource_adapter.commons.web.OpenApiController;
import com.example.datasource_adapter.models.dtos.warranties.WarrantyCreateRqDto;
import com.example.datasource_adapter.services.WarrantyManagerService;
import com.google.inject.Inject;
import io.vertx.core.json.JsonObject;
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

      WarrantyCreateRqDto body = extractValidatedJsonBody(ctx, WarrantyCreateRqDto.class);

      var disposable = service.createWarranty(body)
        .subscribe(
          result -> sendResponse(ctx, 201, JsonObject.mapFrom(result)),
          ctx::fail
        );

      ctx.addEndHandler(v -> disposable.dispose());

    } catch (IllegalArgumentException e) {
      log.error("Validation Error: {}", e.getMessage());
      ctx.fail(400, e);
    } catch (Exception e) {
      log.error("Internal Server Error during mapping: ", e);
      ctx.fail(500, e);
    }
  }

}
