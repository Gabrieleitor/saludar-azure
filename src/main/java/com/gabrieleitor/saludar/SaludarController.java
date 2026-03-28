package com.gabrieleitor.saludar;

import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Timer;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
public class SaludarController {

    private final Counter saludosCounter;
    private final Timer tiempoRespuesta;

    public SaludarController(MeterRegistry registry) {
        this.saludosCounter = Counter.builder("saludar.requests.total")
                .description("Total de requests al endpoint saludar")
                .register(registry);

        this.tiempoRespuesta = Timer.builder("saludar.response.time")
                .description("Tiempo de respuesta del endpoint")
                .register(registry);
    }


    @GetMapping("/saludar/{nombre}")
    public String saludar(@PathVariable String nombre) {
        saludosCounter.increment();
        return tiempoRespuesta.record(() -> ("¿Cómo estás, " + nombre + "?"));
    }
}
