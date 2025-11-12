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
        System.out.println("=== Authentication Attempt ===");
        System.out.println("Username: " + username);
        System.out.println("Looking up user in database...");
        
        User user = userRepository.findByUsername(username)
            .orElseThrow(() -> {
                System.out.println("User NOT FOUND: " + username);
                long totalUsers = userRepository.count();
                System.out.println("Total users in database: " + totalUsers);
                return new UsernameNotFoundException("User not found: " + username);
            });
        
        System.out.println("User FOUND: " + user.getUsername());
        System.out.println("Password hash (first 20 chars): " + user.getPassword().substring(0, Math.min(20, user.getPassword().length())) + "...");
        System.out.println("=============================");
        
        return org.springframework.security.core.userdetails.User
            .withUsername(user.getUsername())
            .password(user.getPassword())
            .authorities("USER")
            .build();
    }
}

