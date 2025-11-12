package koto_thing;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/debug")
public class DebugController {
    
    @Autowired
    private UserRepository userRepository;
    
    @Autowired
    private PasswordEncoder passwordEncoder;
    
    /**
     * 認証なしでアクセスできるデバッグエンドポイント
     * 管理者ユーザーの存在と設定を確認
     */
    @GetMapping("/check-admin")
    public Map<String, Object> checkAdmin() {
        Map<String, Object> result = new HashMap<>();
        
        long totalUsers = userRepository.count();
        result.put("totalUsers", totalUsers);
        
        var adminUser = userRepository.findByUsername("admin");
        result.put("adminExists", adminUser.isPresent());
        
        if (adminUser.isPresent()) {
            User user = adminUser.get();
            result.put("username", user.getUsername());
            result.put("passwordHashPrefix", user.getPassword().substring(0, Math.min(20, user.getPassword().length())));
            
            // テストパスワードが正しいか確認
            boolean matches = passwordEncoder.matches("admin1234", user.getPassword());
            result.put("passwordMatchesAdmin1234", matches);
        }
        
        return result;
    }
    
    /**
     * パスワードのハッシュ値を生成（デバッグ用）
     */
    @GetMapping("/hash-password")
    public Map<String, String> hashPassword(@RequestParam String password) {
        Map<String, String> result = new HashMap<>();
        String hash = passwordEncoder.encode(password);
        result.put("input", password);
        result.put("hash", hash);
        result.put("hashPrefix", hash.substring(0, Math.min(20, hash.length())));
        return result;
    }
}

