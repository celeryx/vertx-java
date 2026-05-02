package com.example.datasource_adapter.services;

import com.example.datasource_adapter.converters.WarrantyMapper;
import com.example.datasource_adapter.models.dtos.warranties.WarrantyCreateRqDto;
import com.example.datasource_adapter.models.dtos.warranties.WarrantyCreateRsDto;
import com.example.datasource_adapter.repositories.WarrantyRepository;
import com.google.inject.Inject;
import io.reactivex.rxjava3.core.Single;

/**
 * Service orchestrating the business logic for Warranties.
 * Guice automatically injects the WarrantyRepository into this Record.
 */
public record WarrantyManagerService(WarrantyRepository repository) {

  @Inject
  public WarrantyManagerService {
  }

  public Single<WarrantyCreateRsDto> createWarranty(WarrantyCreateRqDto body) {

    var entity = WarrantyMapper.INSTANCE.toEntity(body);

    return repository.createWarranty(entity, body.ticketSize()).map(WarrantyMapper.INSTANCE::toCreateRsDto);
  }
}
