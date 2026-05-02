package com.example.datasource_adapter.converters;

import com.example.datasource_adapter.models.dtos.products.ProductCreateRsDto;
import com.example.datasource_adapter.models.dtos.providers.ProviderCreateRsDto;
import com.example.datasource_adapter.models.dtos.warranties.WarrantyCreateRqDto;
import com.example.datasource_adapter.models.dtos.warranties.WarrantyCreateRsDto;
import com.example.datasource_adapter.models.dtos.warranty_types.WarrantyTypeCreateRsDto;
import com.example.datasource_adapter.models.entities.warranties.WarrantyEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.time.OffsetDateTime;
import java.util.UUID;

@Mapper(imports = {UUID.class, OffsetDateTime.class})
public interface WarrantyMapper {

  @Mapping(target = "id", source = "id")
  @Mapping(target = "createdByDni", source = "warrantyCreatedByDni")
  @Mapping(target = "createdByEmail", source = "warrantyCreatedByEmail")
  @Mapping(target = "createdAt", source = "warrantyCreatedAt")
  @Mapping(target = "assetNumber", source = "warrantyAssetId")
  @Mapping(target = "description", source = "warrantyDescription")
  @Mapping(target = "daysToExpire", source = "warrantyExpiresIn")
  @Mapping(target = "isExpired", source = "warrantyIsExpired")
  @Mapping(target = "expirationDate", expression = "java(entity.warrantyCreatedAt() != null ? entity.warrantyCreatedAt().plusDays(entity.warrantyExpiresIn()).toString() : null)")
  @Mapping(target = "ticketSize", ignore = true)
  @Mapping(target = "product.id", source = "productId")
  @Mapping(target = "provider.id", source = "providerId")
  @Mapping(target = "warrantyType.id", source = "warrantyTypeId")
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
