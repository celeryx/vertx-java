package com.example.datasource_adapter.converters;

import com.example.datasource_adapter.models.dtos.warranties.WarrantyCreateRqDto;
import com.example.datasource_adapter.models.dtos.warranties.WarrantyCreateRsDto;
import com.example.datasource_adapter.models.entities.warranties.WarrantyEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import java.util.UUID;
import java.time.OffsetDateTime;

@Mapper(imports = { UUID.class, OffsetDateTime.class })
public interface WarrantyMapper {

  @Mapping(target = "assetNumber", source = "warrantyAssetId")
  @Mapping(target = "description", source = "warrantyDescription")
  @Mapping(target = "daysToExpire", source = "warrantyExpiresIn")
  @Mapping(target = "isExpired", source = "warrantyIsExpired")
  @Mapping(target = "expirationDate", expression = "java(entity.warrantyCreatedAt().plusDays(entity.warrantyExpiresIn()).toString())")
  @Mapping(target = "ticketSize", ignore = true)
  @Mapping(target = "product", ignore = true)
  @Mapping(target = "provider", ignore = true)
  @Mapping(target = "warrantyType", ignore = true)
  WarrantyCreateRsDto toCreateRsDto(WarrantyEntity entity);

  @Mapping(target = "id", expression = "java(UUID.randomUUID().toString())")
  @Mapping(target = "warrantyAssetId", source = "assetNumber")
  @Mapping(target = "warrantyDescription", source = "description")
  @Mapping(target = "warrantyExpiresIn", source = "daysToExpire")
  @Mapping(target = "warrantyCreatedByDni", source = "createdByDni")
  @Mapping(target = "warrantyCreatedByEmail", source = "createdByEmail")
  @Mapping(target = "warrantyCreatedAt", expression = "java(LocalDateTime.now())")
  @Mapping(target = "warrantyIsExpired", constant = "false")
  WarrantyEntity toEntity(WarrantyCreateRqDto dto);
}
