package com.example.datasource_adapter.models.entities.warranties;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;

import java.time.LocalDateTime;

import static io.vertx.core.json.JsonObject.mapFrom;

@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public record WarrantyEntity(
  String id,
  String warrantyAssetId,
  String warrantyDescription,
  int warrantyExpiresIn,
  boolean warrantyIsExpired,
  LocalDateTime warrantyCreatedAt,
  String warrantyCreatedByDni,
  String warrantyCreatedByEmail,
  String productId,
  String warrantyTypeId,
  String providerId
) {

  public java.util.Map<String, Object> toPersistenceMap() {
    var map = mapFrom(this).getMap();
    map.put("warranty_created_at", this.warrantyCreatedAt());
    return map;
  }
}
