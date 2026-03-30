package com.gabrieleitor.saludar;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.jwt;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.util.ArrayList;
import java.util.List;

@SpringBootTest
@ActiveProfiles("test")
@AutoConfigureMockMvc
class SaludarControllerTest {

	@Autowired
	private MockMvc mockMvc;

	@Autowired
	private JwtAuthenticationConverter jwtAuthenticationConverter;

	@Test
	void deberiaSaludarConNombre() throws Exception {
		String nombre = "Liliana";
		this.mockMvc
				.perform(get("/api/saludar/{nombre}", nombre))
				.andExpect(status().isOk())
				.andExpect(content().string("¿Cómo estás, " + nombre + "????5"));
	}

	@Test
	void saludar2SinTokenDebeResponder401() throws Exception {
		this.mockMvc
				.perform(get("/api/saludar2/{nombre}", "Liliana"))
				.andExpect(status().isUnauthorized());
	}

	@Test
	void saludar2ConRolWebAplicationsDebeResponder200() throws Exception {
		String nombre = "Liliana";
		this.mockMvc
				.perform(get("/api/saludar2/{nombre}", nombre)
						.with(jwt().authorities(new SimpleGrantedAuthority("ROLE_web_aplications"))))
				.andExpect(status().isOk())
				.andExpect(content().string("¿Cómo estás, " + nombre + "? (saludar 2)"));
	}

	@Test
	void saludar2ConClaimRolesWebAplicationsDebeResponder200() throws Exception {
		String nombre = "Liliana";
		this.mockMvc
				.perform(get("/api/saludar2/{nombre}", nombre)
						.with(jwt()
								.jwt(j -> j.claim("roles", List.of("web_aplications")))
								.authorities((Jwt jwt) -> new ArrayList<GrantedAuthority>(
										jwtAuthenticationConverter.convert(jwt).getAuthorities()))))
				.andExpect(status().isOk())
				.andExpect(content().string("¿Cómo estás, " + nombre + "? (saludar 2)"));
	}
}

