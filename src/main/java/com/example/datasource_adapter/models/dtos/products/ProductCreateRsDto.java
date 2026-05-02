package com.example.datasource_adapter.models.dtos.products;

import com.example.datasource_adapter.models.dtos.warranties.WarrantyCreateRsDto;
import com.fasterxml.jackson.annotation.JsonInclude;

import java.util.List;

/**
 * Product Response DTO.
 * This record flattens the properties from ManagerCreateRsBaseDto (like id)
 * and reflects optional TypeScript fields using JsonInclude.
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public record ProductCreateRsDto(
  String id,
  String sku,
  String ean,
  String upc,
  String line,
  String description,
  String ticketSize,
  List<WarrantyCreateRsDto> warranties
) {
}
