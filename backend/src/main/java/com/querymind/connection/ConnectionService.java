package com.querymind.connection;

import org.springframework.stereotype.Service;

import java.util.List;

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

        String encryptedPassword =
                cryptoService.encrypt(request.password());

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