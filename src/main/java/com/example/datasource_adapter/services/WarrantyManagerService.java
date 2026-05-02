package com.example.datasource_adapter.services;

import com.example.datasource_adapter.converters.WarrantyMapper;
import com.example.datasource_adapter.models.dtos.warranties.WarrantyCreateRqDto;
import com.example.datasource_adapter.models.dtos.warranties.WarrantyCreateRsDto;
import com.example.datasource_adapter.repositories.WarrantyRepository;
import com.google.inject.Inject;
import io.reactivex.rxjava3.core.Single;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Service orchestrating the business logic for Warranties.
 * Guice automatically injects the WarrantyRepository into this Record.
 */
public record WarrantyManagerService(WarrantyRepository repository,
                                     WarrantyMapper mapper) {

  private static final Logger log = LoggerFactory.getLogger(WarrantyManagerService.class);

  @Inject
  public WarrantyManagerService {
  }

  public Single<WarrantyCreateRsDto> createWarranty(WarrantyCreateRqDto body) {

    var entity = mapper.toEntity(body);

    return repository.createWarranty(entity, body.ticketSize())
      .map(savedEntity -> {
        log.info("Warranty created successfully with ID: {}", entity.id());
        return mapper.toCreateRsDto(savedEntity);
      });
  }
}
