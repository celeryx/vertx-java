package com.example.datasource_adapter.commons.config;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import io.reactivex.rxjava3.core.Single;
import io.vertx.config.ConfigRetrieverOptions;
import io.vertx.config.ConfigStoreOptions;
import io.vertx.core.json.JsonObject;
import io.vertx.rxjava3.config.ConfigRetriever;
import io.vertx.rxjava3.core.Vertx;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Optional;
import java.util.regex.Pattern;

/**
 * @JsonIgnoreProperties tells Jackson not to crash if there are extra
 * environment variables (like PATH or JAVA_HOME) in the merged JSON.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public record AppConfig(ServerConfig server, DatabaseConfig database) {
  private static final Logger log = LoggerFactory.getLogger(AppConfig.class);

  @JsonIgnoreProperties(ignoreUnknown = true)
  public record ServerConfig(int port, String host) {
    public ServerConfig {
      if (port < 1024 || port > 65535) {
        throw new IllegalArgumentException("CRITICAL: Invalid server port: " + port);
      }
      if (host == null || host.isBlank()) {
        throw new IllegalArgumentException("CRITICAL: Server host cannot be empty.");
      }
    }
  }

  @JsonIgnoreProperties(ignoreUnknown = true)
  public record DatabaseConfig(String host, int port, String name, String username, String password) {
    public DatabaseConfig {
      if (password == null || password.isBlank()) {
        throw new IllegalStateException("CRITICAL: Database password environment variable is missing!");
      }
    }
  }

  public static Single<AppConfig> load(Vertx vertx) {
    ConfigStoreOptions yamlStore = new ConfigStoreOptions()
      .setType("file")
      .setFormat("yaml")
      .setConfig(new JsonObject().put("path", "application.yml"));

    ConfigRetrieverOptions options = new ConfigRetrieverOptions().addStore(yamlStore);

    return ConfigRetriever.create(vertx, options)
      .rxGetConfig()
      .map(AppConfig::decode);
  }

  private static AppConfig decode(JsonObject json) {
    String resolvedRaw = resolveVariables(json.encode());
    return new JsonObject(resolvedRaw).mapTo(AppConfig.class);
  }

  private static String resolveVariables(String raw) {
    return Pattern.compile("\\$\\{([^:}]+)(?::([^}]*))?}")
      .matcher(raw)
      .replaceAll(result -> {
        String key = result.group(1);
        String defaultValue = result.group(2);
        return Optional.ofNullable(System.getenv(key))
          .or(() -> Optional.ofNullable(System.getProperty(key)))
          .or(() -> Optional.ofNullable(defaultValue))
          .orElseThrow(() -> {
            var message = "CRITICAL: Configuration variable " + key + " is not set and has no default.";
            log.error("❌ CRITICAL: {}", message);
            return new IllegalStateException(message);
          });
      });
  }
}
