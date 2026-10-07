package com.querymind.connection;

import org.springframework.data.jpa.repository.JpaRepository;

public interface DbConnectionRepository
        extends JpaRepository<DbConnection, Long> {
}