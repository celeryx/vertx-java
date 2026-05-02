package com.example.datasource_adapter.repositories;

import io.reactivex.rxjava3.core.Single;
import io.vertx.rxjava3.sqlclient.Pool;
import io.vertx.rxjava3.sqlclient.templates.RowMapper;
import io.vertx.rxjava3.sqlclient.templates.SqlTemplate;
import io.vertx.rxjava3.sqlclient.templates.TupleMapper;

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

  public Single<T> insert(T entity, String sql) {
    return executeWithCustomReturn(entity, sql, this.rxRowMapper);
  }

  public <R> Single<R> executeWithCustomReturn(T params, String sql, RowMapper<R> customReturnMapper) {
    return SqlTemplate.forQuery(dbPool, sql)
      .mapFrom(rxTupleMapper)
      .mapTo(customReturnMapper)
      .rxExecute(params)
      .map(rowSet -> {
        if (!rowSet.iterator().hasNext()) {
          throw new IllegalStateException("Query executed but no rows returned.");
        }
        return rowSet.iterator().next();
      });
  }
}
