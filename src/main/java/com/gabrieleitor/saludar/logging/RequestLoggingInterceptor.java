package com.gabrieleitor.saludar.logging;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

@Component
public class RequestLoggingInterceptor implements HandlerInterceptor {

	private static final String HEADER_TRACE_ID = "X-Trace-Id";

	private final RequestLogContext requestLogContext;

	public RequestLoggingInterceptor(RequestLogContext requestLogContext) {
		this.requestLogContext = requestLogContext;
	}

	@Override
	public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
		requestLogContext.begin(request, request.getHeader(HEADER_TRACE_ID));
		return true;
	}

	@Override
	public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler,
			Exception ex) {
		requestLogContext.complete(request, response);
	}
}
