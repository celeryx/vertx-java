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

import static io.vertx.core.json.JsonObject.mapFrom;

public record WarrantyRepository(Pool dbPool) {

  private static final Logger log = LoggerFactory.getLogger(WarrantyRepository.class);

  @Inject
  public WarrantyRepository {
  }

  private static final String INSERT_WARRANTY_SQL = """
    INSERT INTO manager.warranty (
        id, warranty_asset_id, warranty_description, warranty_expires_in,
        warranty_is_expired, warranty_created_at, warranty_created_by_dni,
        warranty_created_by_email, product_id, warranty_type_id, provider_id
    ) VALUES (
        #{id}, #{warranty_asset_id}, #{warranty_description}, #{warranty_expires_in},
        #{warranty_is_expired}, #{warranty_created_at},
        #{warranty_created_by_dni}, #{warranty_created_by_email}, #{product_id},
        #{warranty_type_id}, #{provider_id}
    ) RETURNING *;
    """;

  // Added the raw SQL for the native update
  private static final String UPDATE_PRODUCT_SQL = """
    UPDATE schema_manager.product
    SET ticket_size = #{ticketSize}
    WHERE id = #{id}
    """;

  public Single<WarrantyEntity> createWarranty(WarrantyEntity entity, String ticketSize) {

    io.vertx.sqlclient.templates.TupleMapper<WarrantyEntity> coreTupleMapper =
      io.vertx.sqlclient.templates.TupleMapper.mapper(w -> {
        var map = mapFrom(w).getMap();
        map.put("warranty_created_at", w.warrantyCreatedAt());
        return map;
      });

    io.vertx.sqlclient.templates.RowMapper<WarrantyEntity> coreRowMapper =
      (io.vertx.sqlclient.Row row) -> row.toJson().mapTo(WarrantyEntity.class);

    TupleMapper<WarrantyEntity> rxTupleMapper = TupleMapper.newInstance(coreTupleMapper);
    RowMapper<WarrantyEntity> rxRowMapper = RowMapper.newInstance(coreRowMapper);
    log.info("🚀 Preparing to insert Warranty Entity: {}", entity);
    return dbPool.rxWithTransaction((SqlConnection client) ->
      SqlTemplate.forQuery(client, INSERT_WARRANTY_SQL)
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

            return SqlTemplate.forQuery(client, UPDATE_PRODUCT_SQL)
              .rxExecute(updateParams)
              .ignoreElement()
              .andThen(Maybe.just(insertedWarranty));
          }

          return Maybe.just(insertedWarranty);
        })
    ).toSingle();
  }
}
