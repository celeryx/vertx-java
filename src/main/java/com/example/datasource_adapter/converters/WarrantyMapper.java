package com.example.datasource_adapter.converters;

import com.example.datasource_adapter.models.dtos.warranties.WarrantyCreateRqDto;
import com.example.datasource_adapter.models.dtos.warranties.WarrantyCreateRsDto;
import com.example.datasource_adapter.models.entities.warranties.WarrantyEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

import java.time.OffsetDateTime;
import java.util.UUID;

@Mapper(imports = {UUID.class, OffsetDateTime.class})
public interface WarrantyMapper {

  WarrantyMapper INSTANCE = Mappers.getMapper(WarrantyMapper.class);

  // ==========================================
  // Rq DTO -> Entity (Incoming)
  // ==========================================
  @Mapping(target = "id", expression = "java(UUID.randomUUID().toString())")
  @Mapping(target = "warrantyAssetId", source = "assetNumber")
  @Mapping(target = "warrantyDescription", source = "description")
  @Mapping(target = "warrantyExpiresIn", source = "daysToExpire")
  @Mapping(target = "warrantyIsExpired", constant = "false")
  @Mapping(target = "warrantyCreatedAt", expression = "java(OffsetDateTime.now())")
  @Mapping(target = "warrantyCreatedByDni", source = "createdByDni")
  @Mapping(target = "warrantyCreatedByEmail", source = "createdByEmail")
  @Mapping(target = "productId", source = "productId")
  @Mapping(target = "warrantyTypeId", source = "warrantyTypeId")
  @Mapping(target = "providerId", source = "providerId")
  WarrantyEntity toEntity(WarrantyCreateRqDto dto);

  // ==========================================
  // Entity -> Rs DTO (Outgoing)
  // ==========================================
  // We map the database-specific fields back to your clean DTO names.
  // Fields with identical names (id, productId, providerId, etc.) are mapped automatically!
  @Mapping(target = "assetNumber", source = "warrantyAssetId")
  @Mapping(target = "description", source = "warrantyDescription")
  @Mapping(target = "daysToExpire", source = "warrantyExpiresIn")
  @Mapping(target = "createdByDni", source = "warrantyCreatedByDni")
  @Mapping(target = "createdByEmail", source = "warrantyCreatedByEmail")
  WarrantyCreateRsDto toCreateRsDto(WarrantyEntity entity);
}
