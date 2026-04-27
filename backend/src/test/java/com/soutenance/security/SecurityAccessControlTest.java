package com.soutenance.security;

import com.soutenance.features.salle.dto.SalleDTO;
import com.soutenance.features.salle.service.Implementation.SalleServiceImpl;
import com.soutenance.security.jwt.JwtAuthenticationFilter;
import com.soutenance.security.jwt.JwtService;
import com.soutenance.security.user.ApplicationUser;
import com.soutenance.security.user.ApplicationUserRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = com.soutenance.features.salle.controller.SalleController.class)
@Import({SecurityConfig.class, JwtAuthenticationFilter.class, JwtService.class, AppUserDetailsService.class})
@TestPropertySource(properties = {
        "app.jwt.secret=test_secret_with_more_than_32_characters",
        "app.jwt.expiration-ms=3600000"
})
class SecurityAccessControlTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private JwtService jwtService;

    @MockBean
    private SalleServiceImpl salleService;

    @MockBean
    private ApplicationUserRepository userRepository;

    @Test
    void unauthenticatedApiRequestReturnsUnauthorized() throws Exception {
        mockMvc.perform(get("/api/salles"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void enseignantCanReadSalles() throws Exception {
        when(salleService.getAll()).thenReturn(List.of(new SalleDTO(1L, "A1", 20, "Bloc A", true)));

        mockMvc.perform(get("/api/salles")
                        .header(HttpHeaders.AUTHORIZATION, bearer("teacher", Role.ENSEIGNANT)))
                .andExpect(status().isOk());
    }

    @Test
    void etudiantCannotReadAdminTeacherApiList() throws Exception {
        mockMvc.perform(get("/api/salles")
                        .header(HttpHeaders.AUTHORIZATION, bearer("student", Role.ETUDIANT)))
                .andExpect(status().isForbidden());
    }

    @Test
    void adminCanCreateSalle() throws Exception {
        when(salleService.create(any(SalleDTO.class))).thenReturn(new SalleDTO(1L, "A1", 20, "Bloc A", true));

        mockMvc.perform(post("/api/salles")
                        .header(HttpHeaders.AUTHORIZATION, bearer("admin", Role.ADMIN))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"nom\":\"A1\",\"capacite\":20,\"localisation\":\"Bloc A\",\"disponible\":true}"))
                .andExpect(status().isOk());
    }

    @Test
    void enseignantCannotCreateSalle() throws Exception {
        mockMvc.perform(post("/api/salles")
                        .header(HttpHeaders.AUTHORIZATION, bearer("teacher", Role.ENSEIGNANT))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"nom\":\"A1\",\"capacite\":20,\"localisation\":\"Bloc A\",\"disponible\":true}"))
                .andExpect(status().isForbidden());
    }

    private String bearer(String username, Role role) {
        when(userRepository.findByUsername(username)).thenReturn(Optional.of(ApplicationUser.builder()
                .username(username)
                .email(username + "@example.local")
                .passwordHash("hashed")
                .role(role)
                .enabled(true)
                .build()));
        return "Bearer " + jwtService.generateToken(username, role);
    }
}
