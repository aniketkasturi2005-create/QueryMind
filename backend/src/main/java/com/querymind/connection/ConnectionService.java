
package com.querymind.connection;

import org.springframework.stereotype.Service;

import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.List;
import java.util.Properties;

@Service
public class ConnectionService {

    private final DbConnectionRepository dbConnectionRepository;
    private final CryptoService cryptoService;

    public ConnectionService(
            DbConnectionRepository dbConnectionRepository,
            CryptoService cryptoService
    ) {
        this.dbConnectionRepository = dbConnectionRepository;
        this.cryptoService = cryptoService;
    }

    public ConnectionDtos.ConnectionResponse createConnection(
            ConnectionDtos.CreateConnectionRequest request
    ) {
        String encryptedPassword = cryptoService.encrypt(request.password());

        DbConnection connection = new DbConnection(
                request.name(),
                DbType.MYSQL,
                request.host(),
                request.port(),
                request.databaseName(),
                request.username(),
                encryptedPassword
        );

        DbConnection savedConnection =
                dbConnectionRepository.save(connection);

        return toResponse(savedConnection);
    }

    public List<ConnectionDtos.ConnectionResponse> getAllConnections() {
        return dbConnectionRepository.findAll()
                .stream()
                .map(this::toResponse)
                .toList();
    }

    public ConnectionDtos.ConnectionTestResponse testConnection(Long id) {

        DbConnection connection = dbConnectionRepository.findById(id)
                .orElseThrow(() ->
                        new IllegalArgumentException("Connection not found"));

        // Allow only ordinary hostnames/IP addresses.
        if (connection.getHost() == null
                || !connection.getHost().matches("[A-Za-z0-9.-]+")) {
            return new ConnectionDtos.ConnectionTestResponse(
                    id, false, "Invalid database host"
            );
        }

        // Validate the port and database name before building the JDBC URL.
        if (connection.getPort() == null
                || connection.getPort() < 1
                || connection.getPort() > 65535
                || connection.getDatabaseName() == null
                || !connection.getDatabaseName().matches("[A-Za-z0-9_]+")) {
            return new ConnectionDtos.ConnectionTestResponse(
                    id, false, "Invalid database connection settings"
            );
        }

        String url = "jdbc:mysql://"
                + connection.getHost()
                + ":"
                + connection.getPort()
                + "/"
                + connection.getDatabaseName();

        try {
            Properties properties = new Properties();
            properties.setProperty("user", connection.getUsername());
            properties.setProperty(
                    "password",
                    cryptoService.decrypt(connection.getPassword())
            );
            properties.setProperty("connectTimeout", "5000");
            properties.setProperty("socketTimeout", "5000");

            try (java.sql.Connection jdbcConnection =
                         DriverManager.getConnection(url, properties)) {

                return new ConnectionDtos.ConnectionTestResponse(
                        id,
                        true,
                        "Database connection successful"
                );
            }

        } catch (SQLException | RuntimeException exception) {
            // Do not expose credentials or raw database errors to the client.
            return new ConnectionDtos.ConnectionTestResponse(
                    id,
                    false,
                    "Connection failed. Check the database settings and access permissions."
            );
        }
    }

    private ConnectionDtos.ConnectionResponse toResponse(
            DbConnection connection
    ) {
        return new ConnectionDtos.ConnectionResponse(
                connection.getId(),
                connection.getName(),
                connection.getDbType(),
                connection.getHost(),
                connection.getPort(),
                connection.getDatabaseName(),
                connection.getUsername()
        );
    }
}