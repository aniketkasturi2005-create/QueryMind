package com.querymind.connection;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/connections")
public class ConnectionController {

    private final ConnectionService connectionService;

    public ConnectionController(ConnectionService connectionService) {
        this.connectionService = connectionService;
    }

    @PostMapping
    public ResponseEntity<ConnectionDtos.ConnectionResponse> createConnection(
            @RequestBody ConnectionDtos.CreateConnectionRequest request
    ) {
        return ResponseEntity.ok(
                connectionService.createConnection(request)
        );
    }

    @GetMapping
    public ResponseEntity<List<ConnectionDtos.ConnectionResponse>> getAllConnections() {
        return ResponseEntity.ok(
                connectionService.getAllConnections()
        );
    }
}