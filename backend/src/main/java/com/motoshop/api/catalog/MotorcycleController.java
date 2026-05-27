package com.motoshop.api.catalog;

import com.motoshop.api.catalog.MotorcycleService.MotorcycleFilter;
import com.motoshop.api.catalog.dto.CreateMotorcycleRequest;
import com.motoshop.api.catalog.dto.MotorcycleResponse;
import com.motoshop.api.catalog.dto.UpdateMotorcycleRequest;
import com.motoshop.api.catalog.model.Category;
import com.motoshop.api.catalog.model.LicenseType;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import java.net.URI;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/motorcycles")
@Tag(name = "Catalog", description = "Public listing and admin management of motorcycles")
public class MotorcycleController {

  private final MotorcycleService service;

  public MotorcycleController(MotorcycleService service) {
    this.service = service;
  }

  // -------------------- PUBLIC READ --------------------

  @Operation(
      summary = "List motorcycles",
      description =
          """
                    Paginated, filterable listing of the catalog. All filters are optional.
                    Sort with `sort=field,asc|desc`, e.g. `sort=priceCents,asc`.
                    """)
  @GetMapping
  public Page<MotorcycleResponse> list(
      @Parameter(description = "Search in brand or model (case-insensitive, partial match)")
          @RequestParam(required = false)
          String q,
      @Parameter(description = "Exact brand match") @RequestParam(required = false) String brand,
      @Parameter(description = "Filter by motorcycle category") @RequestParam(required = false)
          Category category,
      @Parameter(description = "Filter by minimum required license") @RequestParam(required = false)
          LicenseType license,
      @Parameter(description = "Minimum price in cents") @RequestParam(required = false)
          Long minPriceCents,
      @Parameter(description = "Maximum price in cents") @RequestParam(required = false)
          Long maxPriceCents,
      @Parameter(description = "Return only items with stock > 0") @RequestParam(required = false)
          Boolean inStock,
      @PageableDefault(size = 20, sort = "brand") Pageable pageable) {
    MotorcycleFilter filter =
        new MotorcycleFilter(q, brand, category, license, minPriceCents, maxPriceCents, inStock);
    return service.list(filter, pageable);
  }

  @Operation(summary = "Get a motorcycle by id")
  @ApiResponses({
    @ApiResponse(responseCode = "200", description = "Found"),
    @ApiResponse(responseCode = "404", description = "Not found", content = @Content)
  })
  @GetMapping("/{id}")
  public MotorcycleResponse findById(@PathVariable Long id) {
    return service.findById(id);
  }

  // -------------------- ADMIN WRITE --------------------

  @Operation(
      summary = "Create a motorcycle",
      description = "Requires the ADMIN role.",
      security = @SecurityRequirement(name = "bearer-jwt"))
  @ApiResponses({
    @ApiResponse(responseCode = "201", description = "Created"),
    @ApiResponse(responseCode = "400", description = "Validation failed", content = @Content),
    @ApiResponse(
        responseCode = "401",
        description = "Missing or invalid token",
        content = @Content),
    @ApiResponse(responseCode = "403", description = "Caller is not ADMIN", content = @Content)
  })
  @PostMapping
  public ResponseEntity<MotorcycleResponse> create(
      @Valid @RequestBody CreateMotorcycleRequest req) {
    MotorcycleResponse created = service.create(req);
    return ResponseEntity.created(URI.create("/api/motorcycles/" + created.id())).body(created);
  }

  @Operation(
      summary = "Update a motorcycle (partial)",
      description = "Requires the ADMIN role. Only the supplied fields are applied.",
      security = @SecurityRequirement(name = "bearer-jwt"))
  @ApiResponses({
    @ApiResponse(responseCode = "200", description = "Updated"),
    @ApiResponse(responseCode = "400", description = "Validation failed", content = @Content),
    @ApiResponse(
        responseCode = "401",
        description = "Missing or invalid token",
        content = @Content),
    @ApiResponse(responseCode = "403", description = "Caller is not ADMIN", content = @Content),
    @ApiResponse(responseCode = "404", description = "Not found", content = @Content)
  })
  @PutMapping("/{id}")
  public MotorcycleResponse update(
      @PathVariable Long id, @Valid @RequestBody UpdateMotorcycleRequest req) {
    return service.update(id, req);
  }

  @Operation(
      summary = "Delete a motorcycle",
      description = "Requires the ADMIN role.",
      security = @SecurityRequirement(name = "bearer-jwt"))
  @ApiResponses({
    @ApiResponse(responseCode = "204", description = "Deleted"),
    @ApiResponse(
        responseCode = "401",
        description = "Missing or invalid token",
        content = @Content),
    @ApiResponse(responseCode = "403", description = "Caller is not ADMIN", content = @Content),
    @ApiResponse(responseCode = "404", description = "Not found", content = @Content)
  })
  @DeleteMapping("/{id}")
  @ResponseStatus(HttpStatus.NO_CONTENT)
  public void delete(@PathVariable Long id) {
    service.delete(id);
  }
}
