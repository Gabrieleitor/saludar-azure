package com.gabrieleitor.saludar.logging;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.stereotype.Component;

@Component
public class RequestLogContext {

	private static final Logger log = LoggerFactory.getLogger(RequestLogContext.class);

	private final TraceIdFactory traceIdFactory;

	public RequestLogContext(TraceIdFactory traceIdFactory) {
		this.traceIdFactory = traceIdFactory;
	}

	public void begin(HttpServletRequest request) {
		begin(request, null);
	}

	/**
	 * @param traceId Si es null o vacío, se genera con {@link TraceIdFactory}; si no, se usa el valor recibido
	 *                (p. ej. cabecera {@code X-Trace-Id}).
	 */
	public void begin(HttpServletRequest request, String traceId) {
		String resolved = traceIdFactory.resolve(traceId);
		String clientIp = resolveClientIp(request);
		String method = request.getMethod();
		String uri = request.getRequestURI();

		MDC.put("traceId", resolved);
		MDC.put("service", "saludar");
		MDC.put("clientIp", clientIp);

		log.info(StandardLogFormat.request(method, uri));
	}

	/** Log usando el {@code traceId} ya presente en el MDC (p. ej. tras {@link #begin}). */
	public void info(String format, Object... args) {
		log.info(format, args);
	}

	/**
	 * Log con un {@code traceId} concreto solo para esta línea; si no hubiera MDC previo, no rompe el flujo.
	 * Para el flujo HTTP normal usa {@link #begin(HttpServletRequest, String)} o la cabecera {@code X-Trace-Id}.
	 */
	public void infoWithTrace(String traceId, String format, Object... args) {
		if (traceId == null || traceId.isBlank()) {
			log.info(format, args);
			return;
		}
		String previous = MDC.get("traceId");
		MDC.put("traceId", traceIdFactory.resolve(traceId));
		try {
			log.info(format, args);
		}
		finally {
			if (previous != null) {
				MDC.put("traceId", previous);
			}
			else {
				MDC.remove("traceId");
			}
		}
	}

	/**
	 * Evento de negocio estandarizado: {@code EVENTO clave=valor ...}
	 */
	public void businessInfo(String event, String... keyValuePairs) {
		log.info(StandardLogFormat.business(event, keyValuePairs));
	}

	public String getClientIp() {
		return MDC.get("clientIp");
	}

	public String getTraceId() {
		return MDC.get("traceId");
	}

	public void complete(HttpServletRequest request, HttpServletResponse response) {
		try {
			log.info(StandardLogFormat.response(
					request.getMethod(), request.getRequestURI(), response.getStatus()));
		}
		finally {
			MDC.clear();
		}
	}

	private static String resolveClientIp(HttpServletRequest request) {
		String ip = request.getHeader("X-Forwarded-For");
		if (ip == null || ip.isEmpty()) {
			ip = request.getHeader("X-Real-IP");
		}
		if (ip == null || ip.isEmpty()) {
			ip = request.getRemoteAddr();
		}
		return ip.split(",")[0].trim();
	}
}
