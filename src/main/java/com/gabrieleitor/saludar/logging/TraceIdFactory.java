package com.gabrieleitor.saludar.logging;

import java.util.UUID;
import org.springframework.stereotype.Component;

@Component
public class TraceIdFactory {

	private static final int TRACE_ID_LENGTH = 8;

	public String newTraceId() {
		return UUID.randomUUID().toString().substring(0, TRACE_ID_LENGTH);
	}

	/**
	 * Si {@code traceId} viene informado y no está vacío, se normaliza; si no, se genera uno nuevo.
	 */
	public String resolve(String traceId) {
		if (traceId == null || traceId.isBlank()) {
			return newTraceId();
		}
		String t = traceId.trim();
		return t.length() <= TRACE_ID_LENGTH ? t : t.substring(0, TRACE_ID_LENGTH);
	}
}
