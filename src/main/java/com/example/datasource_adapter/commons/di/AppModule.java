package com.example.datasource_adapter.commons.di;

import com.example.datasource_adapter.commons.config.AppConfig;
import com.example.datasource_adapter.commons.web.OpenApiController;
import com.example.datasource_adapter.controllers.HealthCheckController;
import com.example.datasource_adapter.controllers.WarrantyController;
import com.example.datasource_adapter.converters.WarrantyMapper;
import com.google.inject.AbstractModule;
import com.google.inject.Provides;
import com.google.inject.Singleton;
import com.google.inject.multibindings.Multibinder;
import io.vertx.pgclient.PgConnectOptions;
import io.vertx.rxjava3.core.Vertx;
import io.vertx.rxjava3.pgclient.PgBuilder;
import io.vertx.rxjava3.sqlclient.Pool;
import io.vertx.sqlclient.PoolOptions;
import org.mapstruct.factory.Mappers;

public class AppModule extends AbstractModule {

  private final Vertx vertx;
  private final AppConfig config;

  public AppModule(Vertx vertx, AppConfig config) {
    this.vertx = vertx;
    this.config = config;
  }

  @Provides
  @Singleton
  public Pool providePool() {
    var db = config.database();

    PgConnectOptions connectOptions = new PgConnectOptions()
      .setPort(db.port())
      .setHost(db.host())
      .setDatabase(db.name())
      .setUser(db.username())
      .setPassword(db.password());

    PoolOptions poolOptions = new PoolOptions().setMaxSize(5);

    return PgBuilder.pool()
      .with(poolOptions)
      .connectingTo(connectOptions)
      .using(vertx)
      .build();
  }

  @Provides
  @Singleton
  public WarrantyMapper provideWarrantyMapper() {
    return Mappers.getMapper(WarrantyMapper.class);
  }

  @Override
  protected void configure() {
    bind(Vertx.class).toInstance(vertx);
    bind(AppConfig.class).toInstance(config);

    Multibinder<OpenApiController> controllerBinder = Multibinder.newSetBinder(binder(), OpenApiController.class);
    controllerBinder.addBinding().to(WarrantyController.class);
    controllerBinder.addBinding().to(HealthCheckController.class);
  }
}
