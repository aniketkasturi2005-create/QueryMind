
package com.querymind.backend;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;

@SpringBootTest
@TestPropertySource(properties = {
        "querymind.test-environment=true"
})
class BackendApplicationTests {

    @Test
    void contextLoads() {
    }
}
