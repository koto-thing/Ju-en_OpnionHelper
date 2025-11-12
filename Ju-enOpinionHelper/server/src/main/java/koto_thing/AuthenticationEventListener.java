package koto_thing;

import org.springframework.context.event.EventListener;
import org.springframework.security.authentication.event.AbstractAuthenticationFailureEvent;
import org.springframework.security.authentication.event.AuthenticationSuccessEvent;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.beans.factory.annotation.Autowired;

@Component
public class AuthenticationEventListener {
    
    @Autowired
    private UserRepository userRepository;
    
    @Autowired
    private PasswordEncoder passwordEncoder;
    
    @EventListener
    public void onSuccess(AuthenticationSuccessEvent event) {
        String username = event.getAuthentication().getName();
        System.out.println("=== AUTHENTICATION SUCCESS ===");
        System.out.println("User: " + username);
        System.out.println("==============================");
    }
    
    @EventListener
    public void onFailure(AbstractAuthenticationFailureEvent event) {
        String username = event.getAuthentication().getName();
        Object credentials = event.getAuthentication().getCredentials();
        
        System.out.println("=== AUTHENTICATION FAILURE ===");
        System.out.println("Username: " + username);
        System.out.println("Credentials provided: " + (credentials != null));
        System.out.println("Exception: " + event.getException().getMessage());
        
        // ユーザーが存在する場合、パスワードが合っているか確認
        userRepository.findByUsername(username).ifPresent(user -> {
            if (credentials != null) {
                String providedPassword = credentials.toString();
                boolean matches = passwordEncoder.matches(providedPassword, user.getPassword());
                System.out.println("User exists in DB: YES");
                System.out.println("Password matches: " + (matches ? "YES" : "NO"));
                System.out.println("Provided password length: " + providedPassword.length());
                System.out.println("Provided password: '" + providedPassword + "'");
                System.out.println("Expected password hash: " + user.getPassword().substring(0, 20) + "...");
            }
        });
        
        System.out.println("==============================");
    }
}

