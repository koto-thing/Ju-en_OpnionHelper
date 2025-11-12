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
        System.out.println("=================================");
        System.out.println("DataInitializer: Starting...");
        System.out.println("ADMIN_PASSWORD env var set: " + (adminPassword != null && !adminPassword.isEmpty()));
        
        // デバッグ: 実際のパスワード値を表示
        if (adminPassword != null) {
            System.out.println("ADMIN_PASSWORD value: '" + adminPassword + "'");
            System.out.println("ADMIN_PASSWORD length: " + adminPassword.length());
        }
        
        if (!userRepository.existsByUsername("admin")) {
            // 新規作成
            User admin = new User();
            admin.setUsername("admin");
            
            if (adminPassword == null || adminPassword.isEmpty()) {
                // 環境変数が未設定の場合はランダムパスワードを生成
                adminPassword = generateRandomPassword();
                System.err.println("WARNING: ADMIN_PASSWORD not set!");
                System.err.println("Generated random admin password: " + adminPassword);
                System.err.println("IMPORTANT: Save this password immediately!");
            } else {
                System.out.println("Using ADMIN_PASSWORD from environment variable");
                System.out.println("Password to be encoded: '" + adminPassword + "'");
            }
            
            admin.setPassword(passwordEncoder.encode(adminPassword));
            userRepository.save(admin);
            System.out.println("Admin user created successfully");
            System.out.println("  Username: admin");
            System.out.println("  Password: " + (adminPassword.length() > 0 ? "[SET]" : "[EMPTY]"));
        } else {
            // 既存ユーザーが存在する場合、環境変数が設定されていればパスワードを更新
            System.out.println("Admin user already exists");
            
            if (adminPassword != null && !adminPassword.isEmpty()) {
                User admin = userRepository.findByUsername("admin").orElseThrow();
                String oldPasswordHash = admin.getPassword();
                String newPasswordHash = passwordEncoder.encode(adminPassword);
                
                System.out.println("Updating password...");
                System.out.println("  New password (plain): '" + adminPassword + "'");
                System.out.println("  Old hash prefix: " + oldPasswordHash.substring(0, 20));
                System.out.println("  New hash prefix: " + newPasswordHash.substring(0, 20));
                
                admin.setPassword(newPasswordHash);
                userRepository.save(admin);
                System.out.println("Admin password updated from ADMIN_PASSWORD environment variable");
                
                // 検証: パスワードが正しくマッチするか確認
                boolean matches = passwordEncoder.matches(adminPassword, admin.getPassword());
                System.out.println("  Password verification: " + (matches ? "SUCCESS" : "FAILED"));
            } else {
                System.out.println("ADMIN_PASSWORD not set - keeping existing password");
            }
        }
        
        // 全ユーザーを表示
        long userCount = userRepository.count();
        System.out.println("Total users in database: " + userCount);
        System.out.println("=================================");
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
