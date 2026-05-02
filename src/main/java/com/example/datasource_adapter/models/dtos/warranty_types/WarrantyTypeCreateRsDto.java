package com.example.datasource_adapter.models.dtos.warranty_types;

import com.example.datasource_adapter.models.dtos.warranties.WarrantyCreateRsDto;
import com.fasterxml.jackson.annotation.JsonInclude;

import java.util.List;

/**
 * Warranty Type Response DTO.
 * Flattens base properties and maps the TypeScript Array to a Java List.
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public record WarrantyTypeCreateRsDto(
  String id,
  String name,
  String code,
  String description,
  List<WarrantyCreateRsDto> warranties
) {
}
