package pl.vabanq.erp.infrastructure.config;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
public class LoggingFilter extends OncePerRequestFilter {

    private static final Logger logger = LoggerFactory.getLogger(LoggingFilter.class);

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {

        // Logujemy szczegóły żądania
        logger.info("Request Method: {}, Request URL: {}", request.getMethod(), request.getRequestURI());
        logger.info("Request Headers: {}", request.getHeaderNames());
        logger.info("Remote Address: {}", request.getRemoteAddr());

        // Kontynuuj przetwarzanie filtra
        filterChain.doFilter(request, response);

        // Logujemy status odpowiedzi (na wypadek błędu)
        logger.info("Response Status: {}", response.getStatus());
    }
}
