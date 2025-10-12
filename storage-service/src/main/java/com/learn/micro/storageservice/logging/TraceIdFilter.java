package com.learn.micro.storageservice.logging;

import com.learn.micro.storageservice.logging.TraceContext;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.UUID;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

@Slf4j
@Component
public class TraceIdFilter extends OncePerRequestFilter {

    private static final String TRACE_ID_HEADER = "X-Trace-Id";

    @Override
    protected void doFilterInternal(HttpServletRequest request,
        HttpServletResponse response,
        FilterChain filterChain) throws ServletException, IOException {
        String traceId = request.getHeader(TRACE_ID_HEADER);
        if (traceId == null || traceId.isEmpty()) {
            log.warn("TraceId header is missing from upstream request");
        } else {
            log.info("Using traceId from upstream: {}", traceId);
        }
        TraceContext.setTraceId(traceId);
        MDC.put("traceId", traceId);
        try {
            filterChain.doFilter(request, response);
        } finally {
//            MDC.remove("traceId");
//            TraceContext.clear();
        }
    }
}
