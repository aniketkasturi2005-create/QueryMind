package com.querymind.connection;

public class ConnectionDtos {

    public record CreateConnectionRequest(
            String name,
            String host,
            Integer port,
            String databaseName,
            String username,
            String password
    ) {}

    public record ConnectionResponse(
            Long id,
            String name,
            DbType dbType,
            String host,
            Integer port,
            String databaseName,
            String username
    ) {}
}