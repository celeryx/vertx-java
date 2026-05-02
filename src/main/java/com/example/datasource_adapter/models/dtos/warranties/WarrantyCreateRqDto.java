package com.example.datasource_adapter.models.dtos.warranties;

public record WarrantyCreateRqDto(
  String assetNumber,
  String description,
  int daysToExpire,
  String productId,
  String warrantyTypeId,
  String providerId,
  String ticketSize,
  String createdByDni,
  String createdByEmail
) {
}
