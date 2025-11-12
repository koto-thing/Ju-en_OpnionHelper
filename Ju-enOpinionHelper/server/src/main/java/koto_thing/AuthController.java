package koto_thing;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api")
public class AuthController {
    
    @Autowired
    private UserRepository userRepository;
    
    @Autowired
    private PasswordEncoder passwordEncoder;
    
    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody Map<String, String> request) {
        String username = request.get("username");
        String password = request.get("password");
        
        if (username == null || password == null) {
            return ResponseEntity.badRequest().body("Username and password are required");
        }
        
        if (userRepository.existsByUsername(username)) {
            return ResponseEntity.badRequest().body("Username already exists");
        }
        
        User user = new User();
        user.setUsername(username);
        user.setPassword(passwordEncoder.encode(password));
        userRepository.save(user);
        
        return ResponseEntity.ok("User registered successfully");
    }
    
    @GetMapping("/users")
    public ResponseEntity<?> getUsers() {
        List<Map<String, Object>> users = userRepository.findAll().stream()
            .map(user -> {
                Map<String, Object> userInfo = new HashMap<>();
                userInfo.put("id", user.getId());
                userInfo.put("username", user.getUsername());
                return userInfo;
            })
            .collect(Collectors.toList());
        return ResponseEntity.ok(users);
    }
    
    @GetMapping("/debug/user-exists")
    public ResponseEntity<?> checkUserExists(@RequestParam String username) {
        boolean exists = userRepository.existsByUsername(username);
        Map<String, Object> result = new HashMap<>();
        result.put("username", username);
        result.put("exists", exists);
        result.put("totalUsers", userRepository.count());
        return ResponseEntity.ok(result);
    }
}

