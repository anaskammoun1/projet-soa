package com.soutenance.security;

import com.soutenance.features.enseignant.entity.Enseignant;
import com.soutenance.features.etudiant.entity.Etudiant;
import com.soutenance.features.soutenance.dto.SoutenanceDTO;
import com.soutenance.features.soutenance.entity.Soutenance;
import com.soutenance.features.soutenance.service.Interface.SoutenanceService;
import com.soutenance.orchestrator.PlanificationOrchestrator;
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
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;
import java.util.Optional;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = com.soutenance.features.soutenance.controller.SoutenanceController.class)
@Import({SecurityConfig.class, JwtAuthenticationFilter.class, JwtService.class, AppUserDetailsService.class, OwnershipSecurity.class})
@TestPropertySource(properties = {
        "app.jwt.secret=test_secret_with_more_than_32_characters",
        "app.jwt.expiration-ms=900000"
})
class OwnershipAccessControlTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private JwtService jwtService;

    @MockBean
    private SoutenanceService soutenanceService;

    @MockBean
    private PlanificationOrchestrator planificationOrchestrator;

    @MockBean
    private ApplicationUserRepository userRepository;

    @Test
    void etudiantCanReadOwnSoutenances() throws Exception {
        when(soutenanceService.getByEtudiantId(7)).thenReturn(List.of(new SoutenanceDTO()));

        mockMvc.perform(get("/api/soutenances/etudiant/7")
                        .header(HttpHeaders.AUTHORIZATION, bearer(user("student", Role.ETUDIANT, null, 7))))
                .andExpect(status().isOk());
    }

    @Test
    void etudiantCannotReadAnotherStudentSoutenances() throws Exception {
        mockMvc.perform(get("/api/soutenances/etudiant/8")
                        .header(HttpHeaders.AUTHORIZATION, bearer(user("student", Role.ETUDIANT, null, 7))))
                .andExpect(status().isForbidden());
    }

    @Test
    void enseignantCanReadSoutenanceWhereHeIsJuryMember() throws Exception {
        Soutenance soutenance = new Soutenance();
        soutenance.setId(10L);
        soutenance.setPresident(new Enseignant(4L, "Nom", "Prenom", "teacher@example.local", "Prof", "SI"));
        soutenance.setEtudiant(new Etudiant(7, "Nom", "Prenom", "student@example.local", "M1", "GL", "M2"));
        when(soutenanceService.getOrThrow(10L)).thenReturn(soutenance);
        when(soutenanceService.getById(10L)).thenReturn(new SoutenanceDTO());

        mockMvc.perform(get("/api/soutenances/10")
                        .header(HttpHeaders.AUTHORIZATION, bearer(user("teacher", Role.ENSEIGNANT, 4L, null))))
                .andExpect(status().isOk());
    }

    @Test
    void enseignantCannotReadSoutenanceWhereHeIsNotJuryMember() throws Exception {
        Soutenance soutenance = new Soutenance();
        soutenance.setId(10L);
        soutenance.setPresident(new Enseignant(99L, "Nom", "Prenom", "other@example.local", "Prof", "SI"));
        when(soutenanceService.getOrThrow(10L)).thenReturn(soutenance);

        mockMvc.perform(get("/api/soutenances/10")
                        .header(HttpHeaders.AUTHORIZATION, bearer(user("teacher", Role.ENSEIGNANT, 4L, null))))
                .andExpect(status().isForbidden());
    }

    private ApplicationUser user(String username, Role role, Long enseignantId, Integer etudiantId) {
        return ApplicationUser.builder()
                .username(username)
                .email(username + "@example.local")
                .passwordHash("hashed")
                .role(role)
                .enseignantId(enseignantId)
                .etudiantId(etudiantId)
                .enabled(true)
                .build();
    }

    private String bearer(ApplicationUser user) {
        when(userRepository.findByUsername(user.getUsername())).thenReturn(Optional.of(user));
        return "Bearer " + jwtService.generateToken(user.getUsername(), user.getRole());
    }
}
