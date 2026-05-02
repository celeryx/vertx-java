load("@rules_java//java:defs.bzl", "java_binary", "java_library", "java_plugin")

java_plugin(
    name = "mapstruct_plugin",
    processor_class = "org.mapstruct.processor.MapStructProcessor",
    deps = ["@maven//:org_mapstruct_mapstruct_processor"],
)

java_library(
    name = "app_lib",
    srcs = glob(["src/main/java/**/*.java"]),
    resources = glob(["src/main/resources/**/*"]),
    exported_plugins = [":mapstruct_plugin"],
    deps = [
        "@maven//:io_vertx_vertx_core",
        "@maven//:io_vertx_vertx_health_check",
        "@maven//:io_vertx_vertx_config",
        "@maven//:io_vertx_vertx_config_yaml",
        "@maven//:io_vertx_vertx_launcher_application",
        "@maven//:io_vertx_vertx_rx_java3",
        "@maven//:io_reactivex_rxjava3_rxjava",
        "@maven//:io_vertx_vertx_web",
        "@maven//:com_google_inject_guice",
        "@maven//:jakarta_inject_jakarta_inject_api",
        "@maven//:io_vertx_vertx_openapi",
        "@maven//:io_vertx_vertx_web_openapi_router",
        "@maven//:org_slf4j_slf4j_api",
        "@maven//:ch_qos_logback_logback_classic",
        "@maven//:net_logstash_logback_logstash_logback_encoder",
        "@maven//:com_fasterxml_jackson_core_jackson_annotations",
        "@maven//:com_fasterxml_jackson_core_jackson_databind",
        "@maven//:org_mapstruct_mapstruct",
        "@maven//:io_vertx_vertx_pg_client",
        "@maven//:io_vertx_vertx_sql_client",
        "@maven//:io_vertx_vertx_sql_client_templates",
    ],
)

java_binary(
    name = "app",
    main_class = "io.vertx.launcher.application.VertxApplication",
    args = ["com.example.datasource_adapter.MainVerticle"],
    runtime_deps = [":app_lib"],
)
