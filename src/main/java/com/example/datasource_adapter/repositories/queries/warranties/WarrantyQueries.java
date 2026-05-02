package com.example.datasource_adapter.repositories.queries.warranties;

public final class WarrantyQueries {

  private WarrantyQueries() {
    throw new UnsupportedOperationException("This is a utility class and cannot be instantiated");
  }

  // region Warranty Inserts
  public static final String INSERT_WARRANTY = """
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
  // endregion

  // region Product Updates
  public static final String UPDATE_PRODUCT_TICKET_SIZE = """
    UPDATE schema_manager.product
    SET ticket_size = #{ticketSize}
    WHERE id = #{id}
    """;
  // endregion
}
