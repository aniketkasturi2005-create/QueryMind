package com.querymind.schema;

import com.querymind.connection.CryptoService;
import com.querymind.connection.DbConnection;
import com.querymind.connection.DbConnectionRepository;
import org.springframework.stereotype.Service;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Properties;
import java.util.Set;

@Service
public class SchemaService {

    private final DbConnectionRepository repository;
    private final CryptoService cryptoService;

    public SchemaService(
            DbConnectionRepository repository,
            CryptoService cryptoService) {
        this.repository = repository;
        this.cryptoService = cryptoService;
    }

    public SchemaDtos.SchemaResponse getSchema(Long connectionId) {
        DbConnection saved = repository.findById(connectionId)
                .orElseThrow(() ->
                        new IllegalArgumentException("Connection not found"));

        if (saved.getHost() == null
                || !saved.getHost().matches("[A-Za-z0-9.-]+")
                || saved.getPort() == null
                || saved.getPort() < 1
                || saved.getPort() > 65535
                || saved.getDatabaseName() == null
                || !saved.getDatabaseName().matches("[A-Za-z0-9_]+")) {
            throw new IllegalArgumentException(
                    "Invalid database connection settings");
        }

        String url = "jdbc:mysql://" + saved.getHost()
                + ":" + saved.getPort()
                + "/" + saved.getDatabaseName();

        Properties properties = new Properties();
        properties.setProperty("user", saved.getUsername());
        properties.setProperty(
                "password", cryptoService.decrypt(saved.getPassword()));
        properties.setProperty("connectTimeout", "5000");
        properties.setProperty("socketTimeout", "5000");

        try (Connection connection =
                     DriverManager.getConnection(url, properties)) {

            DatabaseMetaData metadata = connection.getMetaData();
            List<SchemaDtos.TableInfo> tables = new ArrayList<>();

            try (ResultSet tableRows = metadata.getTables(
                    saved.getDatabaseName(), null, "%",
                    new String[]{"TABLE", "VIEW"})) {

                while (tableRows.next()) {
                    String tableName = tableRows.getString("TABLE_NAME");
                    String tableType = tableRows.getString("TABLE_TYPE");

                    Set<String> primaryKeys = new HashSet<>();

                    try (ResultSet keys = metadata.getPrimaryKeys(
                            saved.getDatabaseName(), null, tableName)) {
                        while (keys.next()) {
                            primaryKeys.add(keys.getString("COLUMN_NAME"));
                        }
                    }

                    List<SchemaDtos.ColumnInfo> columns = new ArrayList<>();

                    try (ResultSet columnRows = metadata.getColumns(
                            saved.getDatabaseName(), null, tableName, "%")) {
                        while (columnRows.next()) {
                            String columnName =
                                    columnRows.getString("COLUMN_NAME");

                            columns.add(new SchemaDtos.ColumnInfo(
                                    columnName,
                                    columnRows.getString("TYPE_NAME"),
                                    columnRows.getInt("NULLABLE")
                                            != DatabaseMetaData.columnNoNulls,
                                    primaryKeys.contains(columnName)
                            ));
                        }
                    }

                    List<SchemaDtos.ForeignKeyInfo> foreignKeys =
                            new ArrayList<>();

                    try (ResultSet keyRows = metadata.getImportedKeys(
                            saved.getDatabaseName(), null, tableName)) {
                        while (keyRows.next()) {
                            foreignKeys.add(
                                    new SchemaDtos.ForeignKeyInfo(
                                            keyRows.getString("FKCOLUMN_NAME"),
                                            keyRows.getString("PKTABLE_NAME"),
                                            keyRows.getString("PKCOLUMN_NAME"),
                                            keyRows.getString("FK_NAME")
                                    )
                            );
                        }
                    }

                    tables.add(new SchemaDtos.TableInfo(
                            tableName, tableType, columns, foreignKeys));
                }
            }

            return new SchemaDtos.SchemaResponse(
                    connectionId, saved.getDatabaseName(), tables);

        } catch (SQLException | RuntimeException exception) {
            throw new IllegalStateException(
                    "Could not retrieve database schema. Check the saved "
                            + "connection and its permissions.");
        }
    }
}
