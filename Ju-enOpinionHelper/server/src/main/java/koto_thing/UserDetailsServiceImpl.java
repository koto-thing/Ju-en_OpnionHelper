package koto_thing;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class UserDetailsServiceImpl implements UserDetailsService {
    
    @Autowired
    private UserRepository userRepository;
    
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        System.out.println("=== loadUserByUsername ===");
        System.out.println("Requested username: '" + username + "'");
        System.out.println("Username length: " + username.length());
        System.out.println("Looking up user in database...");
        
        // デバッグ: データベース内の全ユーザーを表示
        long totalUsers = userRepository.count();
        System.out.println("Total users in database: " + totalUsers);
        
        User user = userRepository.findByUsername(username)
            .orElseThrow(() -> {
                System.out.println("User NOT FOUND: '" + username + "'");
                System.out.println("Checking case sensitivity...");
                
                // デバッグ: 全ユーザー名を表示
                userRepository.findAll().forEach(u -> {
                    System.out.println("  Existing user: '" + u.getUsername() + "' (length: " + u.getUsername().length() + ")");
                });
                
                return new UsernameNotFoundException("User not found: " + username);
            });
        
        System.out.println("User FOUND: '" + user.getUsername() + "'");
        System.out.println("Password hash: " + user.getPassword());
        System.out.println("Password hash length: " + user.getPassword().length());
        System.out.println("=============================");
        
        return org.springframework.security.core.userdetails.User
            .withUsername(user.getUsername())
            .password(user.getPassword())
            .authorities("USER")
            .build();
    }
}

