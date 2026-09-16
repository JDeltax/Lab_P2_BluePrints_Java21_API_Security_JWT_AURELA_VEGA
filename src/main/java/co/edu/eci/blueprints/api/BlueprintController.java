package co.edu.eci.blueprints.api;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/blueprints")
@Tag(name = "Blueprints", description = "Endpoints de negocio protegidos por JWT")
public class BlueprintController {

    @GetMapping
    @PreAuthorize("hasAuthority('SCOPE_blueprints.read')")
    @SecurityRequirement(name = "bearer-jwt")
    @Operation(
        summary = "Listar blueprints",
        description = "Requiere el scope blueprints.read en el token JWT."
    )
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Listado obtenido correctamente"),
        @ApiResponse(responseCode = "401", description = "Token ausente, inválido o expirado"),
        @ApiResponse(responseCode = "403", description = "Token válido pero sin el scope blueprints.read")
    })
    public List<Map<String, String>> list() {
        return List.of(
            Map.of("id", "b1", "name", "Casa de campo"),
            Map.of("id", "b2", "name", "Edificio urbano")
        );
    }

    @PostMapping
    @PreAuthorize("hasAuthority('SCOPE_blueprints.write')")
    @SecurityRequirement(name = "bearer-jwt")
    @Operation(
        summary = "Crear un blueprint",
        description = "Requiere el scope blueprints.write en el token JWT."
    )
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Blueprint creado correctamente"),
        @ApiResponse(responseCode = "401", description = "Token ausente, inválido o expirado"),
        @ApiResponse(responseCode = "403", description = "Token válido pero sin el scope blueprints.write")
    })
    public Map<String, String> create(@RequestBody Map<String, String> in) {
        return Map.of("id", "new", "name", in.getOrDefault("name", "nuevo"));
    }
}