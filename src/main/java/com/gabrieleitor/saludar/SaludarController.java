package com.gabrieleitor.saludar;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
public class SaludarController {

	@GetMapping("/saludar/{nombre}")
	public String saludar(@PathVariable String nombre) {
		return "¿Cómo estás, " + nombre + "?";
	}
}
