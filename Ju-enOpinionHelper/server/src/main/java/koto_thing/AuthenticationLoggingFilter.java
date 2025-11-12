package koto_thing;

import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Base64;

@Component
@Order(1)  // Spring Securityより前に実行
public class AuthenticationLoggingFilter implements Filter {
    // ...existing code...

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        
        HttpServletRequest httpRequest = (HttpServletRequest) request;
        
        // 認証ヘッダーをログに記録
        String authHeader = httpRequest.getHeader("Authorization");
        String requestURI = httpRequest.getRequestURI();
        
        System.out.println("=== Incoming Request ===");
        System.out.println("URI: " + requestURI);
        System.out.println("Method: " + httpRequest.getMethod());
        
        if (authHeader != null) {
            System.out.println("Authorization header present: YES");
            if (authHeader.startsWith("Basic ")) {
                try {
                    String base64Credentials = authHeader.substring(6);
                    String credentials = new String(Base64.getDecoder().decode(base64Credentials));
                    String[] parts = credentials.split(":", 2);
                    System.out.println("Username from header: " + (parts.length > 0 ? parts[0] : "NONE"));
                    System.out.println("Password present: " + (parts.length > 1 && !parts[1].isEmpty()));
                } catch (Exception e) {
                    System.out.println("Failed to decode Authorization header");
                }
            }
        } else {
            System.out.println("Authorization header present: NO");
        }
        System.out.println("=======================");
        
        chain.doFilter(request, response);
    }
}

