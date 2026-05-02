package com.example.datasource_adapter.models.dtos.warranties;

import com.example.datasource_adapter.models.dtos.products.ProductCreateRsDto;
import com.example.datasource_adapter.models.dtos.providers.ProviderCreateRsDto;
import com.example.datasource_adapter.models.dtos.warranty_types.WarrantyTypeCreateRsDto;
import com.fasterxml.jackson.annotation.JsonInclude;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record WarrantyCreateRsDto(
  String id,
  String createdByDni,
  String createdByEmail,
  String createdAt,
  String assetNumber,
  String description,
  int daysToExpire,
  String expirationDate,
  boolean isExpired,
  String ticketSize,
  ProductCreateRsDto product,
  ProviderCreateRsDto provider,
  WarrantyTypeCreateRsDto warrantyType
) {
}
