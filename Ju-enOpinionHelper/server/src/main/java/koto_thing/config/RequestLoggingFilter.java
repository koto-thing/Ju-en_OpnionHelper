package koto_thing.config;

import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Base64;

@Component
public class RequestLoggingFilter implements Filter {
    
    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        
        HttpServletRequest httpRequest = (HttpServletRequest) request;
        String authHeader = httpRequest.getHeader("Authorization");
        
        System.out.println("=== Incoming Request ===");
        System.out.println("Method: " + httpRequest.getMethod());
        System.out.println("Path: " + httpRequest.getRequestURI());
        System.out.println("Authorization header: " + authHeader);
        
        if (authHeader != null && authHeader.startsWith("Basic ")) {
            try {
                String base64Credentials = authHeader.substring(6);
                System.out.println("Base64 credentials: " + base64Credentials);
                
                byte[] decodedBytes = Base64.getDecoder().decode(base64Credentials);
                String credentials = new String(decodedBytes);
                System.out.println("Decoded credentials: '" + credentials + "'");
                
                String[] parts = credentials.split(":", 2);
                if (parts.length == 2) {
                    System.out.println("  Username: '" + parts[0] + "' (length: " + parts[0].length() + ")");
                    System.out.println("  Password: '" + parts[1] + "' (length: " + parts[1].length() + ")");
                } else {
                    System.out.println("  ERROR: Could not split credentials");
                }
            } catch (Exception e) {
                System.out.println("  ERROR decoding: " + e.getMessage());
            }
        } else {
            System.out.println("  No Basic auth header found");
        }
        System.out.println("========================");
        
        chain.doFilter(request, response);
    }
}

