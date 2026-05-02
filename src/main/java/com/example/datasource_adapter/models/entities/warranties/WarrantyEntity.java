package com.example.datasource_adapter.models.entities.warranties;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;

import java.time.OffsetDateTime;

@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public record WarrantyEntity(
  String id,
  String warrantyAssetId,
  String warrantyDescription,
  int warrantyExpiresIn,
  boolean warrantyIsExpired,
  OffsetDateTime warrantyCreatedAt,
  String warrantyCreatedByDni,
  String warrantyCreatedByEmail,
  String productId,
  String warrantyTypeId,
  String providerId
) {
}
