package com.example.datasource_adapter.repositories;

import io.reactivex.rxjava3.core.Single;
import io.vertx.rxjava3.sqlclient.Pool;
import io.vertx.rxjava3.sqlclient.templates.RowMapper;
import io.vertx.rxjava3.sqlclient.templates.SqlTemplate;
import io.vertx.rxjava3.sqlclient.templates.TupleMapper;

/**
 * Base repository to handle generic SQL operations and cache mappers.
 *
 * @param <T> The Entity type
 */
public abstract class BaseRepository<T> {

  protected final Pool dbPool;
  protected final TupleMapper<T> rxTupleMapper;
  protected final RowMapper<T> rxRowMapper;

  protected BaseRepository(Pool dbPool,
                           io.vertx.sqlclient.templates.TupleMapper<T> coreTupleMapper,
                           io.vertx.sqlclient.templates.RowMapper<T> coreRowMapper) {
    this.dbPool = dbPool;
    this.rxTupleMapper = TupleMapper.newInstance(coreTupleMapper);
    this.rxRowMapper = RowMapper.newInstance(coreRowMapper);
  }

  /**
   * Generic Insert Operation (Fast Path - No explicit transaction)
   */
  public Single<T> insert(T entity, String sql) {
    return SqlTemplate.forQuery(dbPool, sql)
      .mapFrom(rxTupleMapper)
      .mapTo(rxRowMapper)
      .rxExecute(entity)
      .map(rowSet -> {
        if (!rowSet.iterator().hasNext()) {
          throw new IllegalStateException("Insert failed, no rows returned.");
        }
        return rowSet.iterator().next();
      });
  }
}
