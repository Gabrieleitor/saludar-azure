package com.gabrieleitor.saludar;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;

@SpringBootTest
@AutoConfigureMockMvc
class SaludarControllerTest {

	@Autowired
	private MockMvc mockMvc;

	@Test
	void deberiaSaludarConNombre() throws Exception {
		String nombre = "Liliana";
		this.mockMvc
				.perform(get("/api/saludar/{nombre}", nombre))
				.andExpect(status().isOk())
				.andExpect(content().string("¿Cómo estás, " + nombre + "?????"));
	}
}

