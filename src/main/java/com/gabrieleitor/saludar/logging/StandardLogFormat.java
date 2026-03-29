package com.gabrieleitor.saludar.logging;

/**
 * Formato fijo de mensajes para logs de petición, respuesta y negocio (clave=valor).
 */
public final class StandardLogFormat {

	private StandardLogFormat() {
	}

	public static String request(String method, String uri) {
		return "REQUEST " + method + " " + uri;
	}

	public static String response(String method, String uri, int status) {
		return "RESPONSE " + method + " " + uri + " status=" + status;
	}

	/**
	 * @param keyValuePairs pares nombre, valor (ej. {@code "ip", "1.2.3.4", "nombre", "ana"})
	 */
	public static String business(String event, String... keyValuePairs) {
		if (keyValuePairs.length % 2 != 0) {
			throw new IllegalArgumentException("business(): se esperan pares clave-valor");
		}
		StringBuilder sb = new StringBuilder(event);
		for (int i = 0; i < keyValuePairs.length; i += 2) {
			sb.append(' ').append(keyValuePairs[i]).append('=').append(keyValuePairs[i + 1]);
		}
		return sb.toString();
	}
}
