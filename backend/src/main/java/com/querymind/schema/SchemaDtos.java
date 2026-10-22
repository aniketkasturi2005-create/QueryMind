package com.querymind.schema;

import java.util.List;

public class SchemaDtos {

    public record SchemaResponse(
            Long connectionId,
            String databaseName,
            List<TableInfo> tables
    ) {}

    public record TableInfo(
            String tableName,
            String tableType,
            List<ColumnInfo> columns,
            List<ForeignKeyInfo> foreignKeys
    ) {}

    public record ColumnInfo(
            String columnName,
            String dataType,
            boolean nullable,
            boolean primaryKey
    ) {}

    public record ForeignKeyInfo(
            String columnName,
            String referencedTable,
            String referencedColumn,
            String constraintName
    ) {}
}
