package co.edu.eci.blueprints.api.controllers;

import co.edu.eci.blueprints.api.model.*;
import co.edu.eci.blueprints.*;
import co.edu.eci.blueprints.api.services.BlueprintsServices;
import co.edu.eci.blueprints.api.persistence.BlueprintNotFoundException;
import co.edu.eci.blueprints.api.persistence.BlueprintPersistenceException;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize; // <-- IMPORTANTE MI PRI: Importar PreAuthorize
import org.springframework.web.bind.annotation.*;


import java.util.Map;
import java.util.Set;

@RestController
@RequestMapping("/api/blueprints")
@Tag(name = "Blueprints", description = "API de negocio de planos (blueprints), protegida por JWT")
public class BlueprintsAPIController {

    private final BlueprintsServices services;

    public BlueprintsAPIController(BlueprintsServices services) { this.services = services; }

    // GET /api/blueprints
    @GetMapping
    @PreAuthorize("hasAuthority('SCOPE_blueprints.read')") // <-- SCOPE DE LECTURA
    @SecurityRequirement(name = "bearer-jwt")
    @Operation(summary = "Listar todos los blueprints", description = "Requiere el scope blueprints.read.")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Listado obtenido correctamente"),
        @ApiResponse(responseCode = "401", description = "Token ausente, inválido o expirado"),
        @ApiResponse(responseCode = "403", description = "Token válido pero sin el scope blueprints.read")
    })
    public ResponseEntity<Set<Blueprint>> getAll() {
        return ResponseEntity.ok(services.getAllBlueprints());
    }

    // GET /api/blueprints/{author}
    @GetMapping("/{author}")
    @PreAuthorize("hasAuthority('SCOPE_blueprints.read')") // <-- SCOPE DE LECTURA
    @SecurityRequirement(name = "bearer-jwt")
    @Operation(summary = "Listar blueprints de un autor", description = "Requiere el scope blueprints.read.")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Blueprints del autor obtenidos correctamente"),
        @ApiResponse(responseCode = "401", description = "Token ausente, inválido o expirado"),
        @ApiResponse(responseCode = "403", description = "Token válido pero sin el scope blueprints.read"),
        @ApiResponse(responseCode = "404", description = "No existen blueprints para ese autor")
    })
    public ResponseEntity<?> byAuthor(@PathVariable String author) {
        try {
            return ResponseEntity.ok(services.getBlueprintsByAuthor(author));
        } catch (BlueprintNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("error", e.getMessage()));
        }
    }

    // GET /api/blueprints/{author}/{bpname}
    @GetMapping("/{author}/{bpname}")
    @PreAuthorize("hasAuthority('SCOPE_blueprints.read')") // <-- SCOPE DE LECTURA
    @SecurityRequirement(name = "bearer-jwt")
    @Operation(summary = "Obtener un blueprint específico", description = "Requiere el scope blueprints.read.")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Blueprint obtenido correctamente"),
        @ApiResponse(responseCode = "401", description = "Token ausente, inválido o expirado"),
        @ApiResponse(responseCode = "403", description = "Token válido pero sin el scope blueprints.read"),
        @ApiResponse(responseCode = "404", description = "El blueprint no existe")
    })
    public ResponseEntity<?> byAuthorAndName(@PathVariable String author, @PathVariable String bpname) {
        try {
            return ResponseEntity.ok(services.getBlueprint(author, bpname));
        } catch (BlueprintNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("error", e.getMessage()));
        }
    }

    // POST /api/blueprints
    @PostMapping
    @PreAuthorize("hasAuthority('SCOPE_blueprints.write')") // <-- SCOPE DE ESCRITURA
    @SecurityRequirement(name = "bearer-jwt")
    @Operation(summary = "Crear un nuevo blueprint", description = "Requiere el scope blueprints.write.")
    @ApiResponses({
        @ApiResponse(responseCode = "201", description = "Blueprint creado correctamente"),
        @ApiResponse(responseCode = "401", description = "Token ausente, inválido o expirado"),
        @ApiResponse(responseCode = "403", description = "Token válido sin el scope blueprints.write, o el blueprint ya existe")
    })
    public ResponseEntity<?> add(@Valid @RequestBody NewBlueprintRequest req) {
        try {
            Blueprint bp = new Blueprint(req.author(), req.name(), req.points());
            services.addNewBlueprint(bp);
            return ResponseEntity.status(HttpStatus.CREATED).build();
        } catch (BlueprintPersistenceException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(Map.of("error", e.getMessage()));
        }
    }

    // PUT /api/blueprints/{author}/{bpname}/points
    @PutMapping("/{author}/{bpname}/points")
    @PreAuthorize("hasAuthority('SCOPE_blueprints.write')") // <-- SCOPE DE ESCRITURA (Modificar también requiere write)
    @SecurityRequirement(name = "bearer-jwt")
    @Operation(summary = "Agregar un punto a un blueprint existente", description = "Requiere el scope blueprints.write.")
    @ApiResponses({
        @ApiResponse(responseCode = "202", description = "Punto agregado correctamente"),
        @ApiResponse(responseCode = "401", description = "Token ausente, inválido o expirado"),
        @ApiResponse(responseCode = "403", description = "Token válido pero sin el scope blueprints.write"),
        @ApiResponse(responseCode = "404", description = "El blueprint no existe")
    })
    public ResponseEntity<?> addPoint(@PathVariable String author, @PathVariable String bpname,
                                      @RequestBody Point p) {
        try {
            services.addPoint(author, bpname, p.x(), p.y());
            return ResponseEntity.status(HttpStatus.ACCEPTED).build();
        } catch (BlueprintNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("error", e.getMessage()));
        }
    }

    public record NewBlueprintRequest(
            @NotBlank String author,
            @NotBlank String name,
            @Valid java.util.List<Point> points
    ) { }
}