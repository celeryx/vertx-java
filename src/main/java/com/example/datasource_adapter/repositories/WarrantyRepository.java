package com.example.datasource_adapter.repositories;

import com.example.datasource_adapter.models.entities.warranties.WarrantyEntity;
import com.google.inject.Inject;
import io.reactivex.rxjava3.core.Maybe;
import io.reactivex.rxjava3.core.Single;
import io.vertx.rxjava3.sqlclient.Pool;
import io.vertx.rxjava3.sqlclient.SqlConnection;
import io.vertx.rxjava3.sqlclient.templates.RowMapper;
import io.vertx.rxjava3.sqlclient.templates.SqlTemplate;
import io.vertx.rxjava3.sqlclient.templates.TupleMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;

import static com.example.datasource_adapter.repositories.queries.warranties.WarrantyQueries.INSERT_WARRANTY;
import static com.example.datasource_adapter.repositories.queries.warranties.WarrantyQueries.UPDATE_PRODUCT_TICKET_SIZE;

public record WarrantyRepository(Pool dbPool) {

  private static final Logger log = LoggerFactory.getLogger(WarrantyRepository.class);

  @Inject
  public WarrantyRepository {
  }

  public Single<WarrantyEntity> createWarranty(WarrantyEntity entity, String ticketSize) {
    log.info("🚀 Preparing to insert Warranty Entity: {}", entity.id());

    TupleMapper<WarrantyEntity> rxTupleMapper = TupleMapper.newInstance(
      io.vertx.sqlclient.templates.TupleMapper.mapper(WarrantyEntity::toPersistenceMap)
    );

    RowMapper<WarrantyEntity> rxRowMapper = RowMapper.newInstance(
      (io.vertx.sqlclient.Row row) -> row.toJson().mapTo(WarrantyEntity.class)
    );

    return dbPool.rxWithTransaction((SqlConnection client) ->
      SqlTemplate.forQuery(client, INSERT_WARRANTY)
        .mapFrom(rxTupleMapper)
        .mapTo(rxRowMapper)
        .rxExecute(entity)
        .map(rowSet -> {
          log.info("Warranty inserted successfully with ID: {}", entity.id());
          if (!rowSet.iterator().hasNext()) {
            throw new IllegalStateException("Insert failed, no rows returned.");
          }
          return rowSet.iterator().next();
        })
        .flatMapMaybe(insertedWarranty -> {
          log.info("flatMapMaybe: {}", entity.id());
          boolean hasTicketSize = ticketSize != null && !ticketSize.isBlank();
          boolean hasProductId = insertedWarranty.productId() != null && !insertedWarranty.productId().isBlank();

          if (hasTicketSize && hasProductId) {
            Map<String, Object> updateParams = Map.of(
              "id", insertedWarranty.productId(),
              "ticketSize", ticketSize
            );

            return SqlTemplate.forQuery(client, UPDATE_PRODUCT_TICKET_SIZE)
              .rxExecute(updateParams)
              .ignoreElement()
              .andThen(Maybe.just(insertedWarranty));
          }

          return Maybe.just(insertedWarranty);
        })
    ).toSingle();
  }
}
