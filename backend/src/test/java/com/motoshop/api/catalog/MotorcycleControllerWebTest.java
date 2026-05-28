package com.motoshop.api.catalog;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;

import org.junit.jupiter.api.Test;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithAnonymousUser;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.motoshop.api.catalog.dto.CreateMotorcycleRequest;
import com.motoshop.api.catalog.dto.MotorcycleResponse;
import com.motoshop.api.catalog.exception.MotorcycleNotFoundException;
import com.motoshop.api.catalog.model.Category;
import com.motoshop.api.catalog.model.Cooling;
import com.motoshop.api.catalog.model.EngineType;
import com.motoshop.api.catalog.model.LicenseType;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class MotorcycleControllerWebTest {

  @Autowired MockMvc mvc;
  @Autowired ObjectMapper json;

  @MockBean MotorcycleService service;

  // -------- public read access --------

  @Test
  @WithAnonymousUser
  void anonymousCanListCatalog() throws Exception {
    var page = new org.springframework.data.domain.PageImpl<>(List.of(sampleResponse()));
    when(service.list(any(), any())).thenReturn(page);

    mvc.perform(get("/api/motorcycles"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.content[0].brand").value("Honda"));
  }

  @Test
  @WithAnonymousUser
  void anonymousCanGetSingle() throws Exception {
    when(service.findById(1L)).thenReturn(sampleResponse());

    mvc.perform(get("/api/motorcycles/1"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.model").value("CB650R"));
  }

  @Test
  @WithAnonymousUser
  void notFoundIsJson() throws Exception {
    when(service.findById(9999L)).thenThrow(new MotorcycleNotFoundException(9999L));

    mvc.perform(get("/api/motorcycles/9999"))
        .andExpect(status().isNotFound())
        .andExpect(jsonPath("$.error").value("Not Found"))
        .andExpect(jsonPath("$.message").value("Motorcycle not found: 9999"));
  }

  // -------- authorisation matrix on write endpoints --------

  @Test
  @WithAnonymousUser
  void anonymousCannotCreate() throws Exception {
    mvc.perform(
            post("/api/motorcycles")
                .contentType(MediaType.APPLICATION_JSON)
                .content(json.writeValueAsString(sampleCreate())))
        .andExpect(status().isUnauthorized());
  }

  @Test
  @WithMockUser(roles = "BUYER")
  void buyerCannotCreate() throws Exception {
    mvc.perform(
            post("/api/motorcycles")
                .contentType(MediaType.APPLICATION_JSON)
                .content(json.writeValueAsString(sampleCreate())))
        .andExpect(status().isForbidden());
  }

  @Test
  @WithMockUser(roles = "ADMIN")
  void adminCanCreate() throws Exception {
    when(service.create(any())).thenReturn(sampleResponse());

    mvc.perform(
            post("/api/motorcycles")
                .contentType(MediaType.APPLICATION_JSON)
                .content(json.writeValueAsString(sampleCreate())))
        .andExpect(status().isCreated())
        .andExpect(header().exists("Location"));
  }

  @Test
  @WithMockUser(roles = "BUYER")
  void buyerCannotDelete() throws Exception {
    mvc.perform(delete("/api/motorcycles/1")).andExpect(status().isForbidden());
  }

  @Test
  @WithMockUser(roles = "ADMIN")
  void adminCanDelete() throws Exception {
    mvc.perform(delete("/api/motorcycles/1")).andExpect(status().isNoContent());
  }

  // -------- helpers --------

  private static MotorcycleResponse sampleResponse() {
    return new MotorcycleResponse(
        1L,
        "Honda",
        "CB650R",
        649,
        2024,
        890000L,
        5,
        "desc",
        null,
        94,
        63,
        210,
        EngineType.INLINE_FOUR,
        Cooling.LIQUID,
        206,
        810,
        new BigDecimal("15.4"),
        "Black",
        Category.NAKED,
        LicenseType.A,
        6,
        true,
        true,
        Instant.now(),
        Instant.now());
  }

  private static CreateMotorcycleRequest sampleCreate() {
    return new CreateMotorcycleRequest(
        "Honda",
        "CB650R",
        649,
        2024,
        890000L,
        5,
        "desc",
        94,
        63,
        210,
        EngineType.INLINE_FOUR,
        Cooling.LIQUID,
        206,
        810,
        new BigDecimal("15.4"),
        "Black",
        Category.NAKED,
        LicenseType.A,
        6,
        true,
        true);
  }
}