package koto_thing;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
public class DataInitializer implements CommandLineRunner {
    @Autowired
    private UserRepository userRepository;
    
    @Autowired
    private PasswordEncoder passwordEncoder;
    
    @Override
    public void run(String... args) {
        // 環境変数から管理者パスワードを取得（未設定の場合はランダム生成）
        String adminPassword = System.getenv("ADMIN_PASSWORD");
        
        if (!userRepository.existsByUsername("admin")) {
            User admin = new User();
            admin.setUsername("admin");
            
            if (adminPassword == null || adminPassword.isEmpty()) {
                // 環境変数が未設定の場合はランダムパスワードを生成
                adminPassword = generateRandomPassword();
                System.err.println("WARNING: ADMIN_PASSWORD not set!");
                System.err.println("Generated random admin password: " + adminPassword);
                System.err.println("IMPORTANT: Save this password immediately!");
            }
            
            admin.setPassword(passwordEncoder.encode(adminPassword));
            userRepository.save(admin);
            System.out.println("Admin user created successfully");
        }
    }
    
    private String generateRandomPassword() {
        String chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789!@#$%^&*";
        StringBuilder password = new StringBuilder();
        java.security.SecureRandom random = new java.security.SecureRandom();
        for (int i = 0; i < 16; i++) {
            password.append(chars.charAt(random.nextInt(chars.length())));
        }
        return password.toString();
    }
}
