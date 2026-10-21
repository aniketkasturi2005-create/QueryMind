package com.querymind.schema;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/connections")
public class SchemaController {

    private final SchemaService schemaService;

    public SchemaController(SchemaService schemaService) {
        this.schemaService = schemaService;
    }

    @GetMapping("/{id}/schema")
    public ResponseEntity<SchemaDtos.SchemaResponse> getSchema(
            @PathVariable Long id) {
        return ResponseEntity.ok(schemaService.getSchema(id));
    }
}
