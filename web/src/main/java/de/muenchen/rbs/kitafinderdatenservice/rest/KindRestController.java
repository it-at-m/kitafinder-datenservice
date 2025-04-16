package de.muenchen.rbs.kitafinderdatenservice.rest;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import de.muenchen.rbs.kitafinderdatenservice.SecurityConfig;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;

@RestController
@SecurityRequirement(name = "ApiClient", scopes = { SecurityConfig.SCOPE_LHM_EXTENDED,
        SecurityConfig.SCOPE_OPENID, SecurityConfig.SCOPE_ROLES })
public class KindRestController {

    @GetMapping("/hello")
    @Operation(summary = "Test-Endpunkt.")
    public String sayHello() {
        return "Hello, World!";
    }
    
}
