package com.example.datasource_adapter;

import com.example.datasource_adapter.commons.config.AppConfig;
import com.example.datasource_adapter.commons.di.AppModule;
import com.example.datasource_adapter.commons.web.OpenApiController;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.Key;
import com.google.inject.TypeLiteral;
import io.reactivex.rxjava3.core.Completable;
import io.vertx.core.json.jackson.DatabindCodec;
import io.vertx.rxjava3.core.AbstractVerticle;
import io.vertx.rxjava3.ext.web.openapi.router.RouterBuilder;
import io.vertx.rxjava3.openapi.contract.OpenAPIContract;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


import java.util.Set;

public class MainVerticle extends AbstractVerticle {

  private static final Logger log = LoggerFactory.getLogger(MainVerticle.class);

  @Override
  public Completable rxStart() {
    return AppConfig.load(vertx)
      .flatMapCompletable(this::startWithConfig)
      .doOnError(err -> log.error("❌ Fatal Boot Error: {}", err.getMessage()));
  }

  private Completable startWithConfig(AppConfig config) {
    Injector injector = Guice.createInjector(new AppModule(vertx, config));
    Set<OpenApiController> controllers = injector.getInstance(Key.get(new TypeLiteral<Set<OpenApiController>>() {
    }));
    DatabindCodec.mapper().registerModule(new JavaTimeModule());

    return OpenAPIContract.rxFrom(vertx, "openapi.yaml")
      .flatMap(contract -> {
        RouterBuilder builder = RouterBuilder.create(vertx, contract);
        controllers.forEach(c -> c.bindOperations(builder));

        return vertx.createHttpServer()
          .requestHandler(builder.createRouter())
          .rxListen(config.server().port(), config.server().host());
      })
      .doOnSuccess(server -> log.info("🚀 Server online at {}:{}", config.server().host(), config.server().port()))
      .ignoreElement();
  }
}
