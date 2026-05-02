package com.example.datasource_adapter.models.dtos.providers;

import com.fasterxml.jackson.annotation.JsonInclude;

import java.time.OffsetDateTime;

/**
 * Provider Contact DTO.
 * Uses an Enum to represent the TypeScript union type for 'kind'.
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public record ProviderContactRsDto(
  String id,
  ContactKind kind,
  String value,
  String label,
  OffsetDateTime createdAt
) {
  public enum ContactKind {
    PHONE,
    EMAIL
  }
}
