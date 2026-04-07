package com.inmar.metadata.filter

import jakarta.servlet.FilterChain
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.slf4j.MDC
import org.springframework.core.annotation.Order
import org.springframework.stereotype.Component
import org.springframework.web.filter.OncePerRequestFilter
import java.util.UUID

@Component
@Order(1)
class TraceIdFilter : OncePerRequestFilter() {

    companion object {
        const val TRACE_ID_HEADER = "X-Trace-Id"
        const val TRACE_ID_MDC_KEY = "traceId"
        const val SPAN_ID_MDC_KEY = "spanId"
    }

    override fun doFilterInternal(req: HttpServletRequest, res: HttpServletResponse, chain: FilterChain) {
        val traceId = req.getHeader(TRACE_ID_HEADER) ?: UUID.randomUUID().toString().replace("-", "")
        val spanId = UUID.randomUUID().toString().replace("-", "").take(16)
        try {
            MDC.put(TRACE_ID_MDC_KEY, traceId)
            MDC.put(SPAN_ID_MDC_KEY, spanId)
            res.setHeader(TRACE_ID_HEADER, traceId)
            chain.doFilter(req, res)
        } finally {
            MDC.remove(TRACE_ID_MDC_KEY)
            MDC.remove(SPAN_ID_MDC_KEY)
        }
    }
}
