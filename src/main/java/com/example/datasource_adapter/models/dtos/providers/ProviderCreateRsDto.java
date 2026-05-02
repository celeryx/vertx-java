package com.example.datasource_adapter.models.dtos.providers;

import com.example.datasource_adapter.models.dtos.warranties.WarrantyCreateRsDto;
import com.fasterxml.jackson.annotation.JsonInclude;

import java.util.List;

/**
 * Provider Response DTO.
 * Flattens the properties from ManagerCreateRsBaseDto (e.g., id).
 * All TypeScript properties are marked as required (no '?'),
 * but we use NON_NULL for safe JSON serialization of collections.
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public record ProviderCreateRsDto(
  String id,
  String name,
  String dni,
  String attendedBy,
  String city,
  String commune,
  String address,
  String openingTime,
  String closingTime,
  String postalCode,
  List<WarrantyCreateRsDto> warranties,
  List<ProviderContactRsDto> contacts
) {
}
