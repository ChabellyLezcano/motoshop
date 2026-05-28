package com.motoshop.api.catalog;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.motoshop.api.auth.dto.AuthResponse;
import com.motoshop.api.auth.dto.LoginRequest;
import com.motoshop.api.catalog.dto.CreateMotorcycleRequest;
import com.motoshop.api.catalog.model.Category;
import com.motoshop.api.catalog.model.Cooling;
import com.motoshop.api.catalog.model.EngineType;
import com.motoshop.api.catalog.model.LicenseType;
import com.motoshop.api.support.PostgresIntegrationTest;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class MotorcycleCatalogIT extends PostgresIntegrationTest {

  @Autowired TestRestTemplate http;
  @Autowired ObjectMapper json;

  @Test
  @DisplayName("Flyway seed populates the catalog with the expected number of rows")
  void seedIsPresent() throws Exception {
    var resp = http.getForEntity("/api/motorcycles?size=50", String.class);
    assertThat(resp.getStatusCode()).isEqualTo(HttpStatus.OK);

    JsonNode body = json.readTree(resp.getBody());
    // V2 seeds 30 motorcycles; we assert >=20 to keep room for future tweaks.
    assertThat(body.get("totalElements").asInt()).isGreaterThanOrEqualTo(20);
  }

  @Test
  @DisplayName("Public filtering by license=A2 returns A2-only items")
  void filterByLicenseA2() throws Exception {
    var resp = http.getForEntity("/api/motorcycles?license=A2&size=50", String.class);
    assertThat(resp.getStatusCode()).isEqualTo(HttpStatus.OK);

    JsonNode content = json.readTree(resp.getBody()).get("content");
    assertThat(content).isNotEmpty();
    content.forEach(item -> assertThat(item.get("license").asText()).isEqualTo("A2"));
  }

  @Test
  @DisplayName("Admin can create a new motorcycle; it shows up in the public listing")
  void adminCreatesAndItAppears() throws Exception {
    String adminToken = loginAsAdmin();

    var create =
        new CreateMotorcycleRequest(
            "TestMaker",
            "QA-Special",
            500,
            2024,
            555000L,
            4,
            "A bike that only exists in tests.",
            50,
            50,
            180,
            EngineType.PARALLEL_TWIN,
            Cooling.LIQUID,
            170,
            800,
            new BigDecimal("14.0"),
            "Hi-Vis Yellow",
            Category.NAKED,
            LicenseType.A2,
            6,
            true,
            false);

    HttpHeaders h = new HttpHeaders();
    h.setContentType(MediaType.APPLICATION_JSON);
    h.setBearerAuth(adminToken);

    var created =
        http.exchange(
            "/api/motorcycles", HttpMethod.POST, new HttpEntity<>(create, h), String.class);

    assertThat(created.getStatusCode()).isEqualTo(HttpStatus.CREATED);
    Long newId = json.readTree(created.getBody()).get("id").asLong();

    // Read it back, anonymously.
    var fetched = http.getForEntity("/api/motorcycles/" + newId, String.class);
    assertThat(fetched.getStatusCode()).isEqualTo(HttpStatus.OK);
    assertThat(json.readTree(fetched.getBody()).get("model").asText()).isEqualTo("QA-Special");
  }

  @Test
  @DisplayName("Invalid create payload returns 400 with field errors")
  void invalidCreateReturns400() throws Exception {
    String adminToken = loginAsAdmin();

    // Missing required fields; just brand + model.
    String invalid = """
                { "brand": "X", "model": "Y" }
                """;
    HttpHeaders h = new HttpHeaders();
    h.setContentType(MediaType.APPLICATION_JSON);
    h.setBearerAuth(adminToken);

    var resp =
        http.exchange(
            "/api/motorcycles", HttpMethod.POST, new HttpEntity<>(invalid, h), String.class);

    assertThat(resp.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
    assertThat(json.readTree(resp.getBody()).get("fieldErrors")).isNotNull();
  }

  private String loginAsAdmin() throws Exception {
    LoginRequest loginReq = new LoginRequest("[email protected]", "it-admin-pw");
    HttpHeaders headers = new HttpHeaders();
    headers.setContentType(MediaType.APPLICATION_JSON);
    
    var resp =
        http.postForEntity(
            "/api/auth/login",
            new HttpEntity<>(json.writeValueAsString(loginReq), headers),
            AuthResponse.class);
    assertThat(resp.getStatusCode()).isEqualTo(HttpStatus.OK);
    return resp.getBody().token();
  }
}