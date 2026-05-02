package com.example.datasource_adapter.repositories;

import com.example.datasource_adapter.models.entities.warranties.WarrantyEntity;
import com.google.inject.Inject;
import io.reactivex.rxjava3.core.Maybe;
import io.reactivex.rxjava3.core.Single;
import io.vertx.rxjava3.sqlclient.Pool;
import io.vertx.rxjava3.sqlclient.SqlConnection;
import io.vertx.rxjava3.sqlclient.templates.SqlTemplate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;

import static com.example.datasource_adapter.repositories.queries.warranties.WarrantyQueries.INSERT_WARRANTY;
import static com.example.datasource_adapter.repositories.queries.warranties.WarrantyQueries.UPDATE_PRODUCT_TICKET_SIZE;

public class WarrantyRepository extends BaseRepository<WarrantyEntity> {

  private static final Logger log = LoggerFactory.getLogger(WarrantyRepository.class);

  @Inject
  public WarrantyRepository(Pool dbPool) {
    // ✅ Pass the Pool and the Entity-specific mapping rules to the Base class
    super(
      dbPool,
      io.vertx.sqlclient.templates.TupleMapper.mapper(WarrantyEntity::toPersistenceMap),
      row -> row.toJson().mapTo(WarrantyEntity.class)
    );
  }

  public Single<WarrantyEntity> createWarranty(WarrantyEntity entity, String ticketSize) {
    log.info("🚀 Preparing to insert Warranty Entity: {}", entity.id());

    boolean needsUpdate = ticketSize != null && !ticketSize.isBlank() && entity.productId() != null && !entity.productId().isBlank();

    if (!needsUpdate) {
      log.debug("No ticket size provided. Executing fast-path insert.");
      return insert(entity, INSERT_WARRANTY);
    }

    log.debug("Ticket size provided. Executing transactional insert and update.");
    return dbPool.rxWithTransaction((SqlConnection client) ->
      SqlTemplate.forQuery(client, INSERT_WARRANTY)
        .mapFrom(rxTupleMapper)
        .mapTo(rxRowMapper)
        .rxExecute(entity)
        .map(rowSet -> {
          if (!rowSet.iterator().hasNext()) {
            throw new IllegalStateException("Insert failed.");
          }
          return rowSet.iterator().next();
        })
        .flatMapMaybe(insertedWarranty -> {
          Map<String, Object> updateParams = Map.of("id", insertedWarranty.productId(), "ticketSize", ticketSize);

          return SqlTemplate.forQuery(client, UPDATE_PRODUCT_TICKET_SIZE)
            .rxExecute(updateParams)
            .ignoreElement()
            .andThen(Maybe.just(insertedWarranty));
        })
    ).toSingle();
  }
}
