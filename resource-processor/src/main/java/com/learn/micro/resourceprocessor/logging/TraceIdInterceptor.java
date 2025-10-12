package com.learn.micro.resourceprocessor.logging;

import java.io.IOException;
import org.springframework.http.HttpRequest;
import org.springframework.http.client.ClientHttpRequestExecution;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.http.client.ClientHttpResponse;

public class TraceIdInterceptor implements ClientHttpRequestInterceptor {

    private static final String TRACE_ID_HEADER = "X-Trace-Id";

    @Override
    public ClientHttpResponse intercept(HttpRequest request, byte[] body,
                                        ClientHttpRequestExecution execution) throws IOException {
        String traceId = TraceContext.getTraceId();
        if (traceId != null) {
            request.getHeaders().add(TRACE_ID_HEADER, traceId);
        }
        return execution.execute(request, body);
    }
}
