package koto_thing.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            .csrf(csrf -> csrf.disable())  // REST APIのためCSRFを無効化
            .cors(cors -> cors.configure(http))  // CORSを有効化
            .authorizeHttpRequests(auth -> auth
                .anyRequest().authenticated()  // すべてのリクエストに認証を要求
            )
            .httpBasic(basic -> {});  // Basic認証を有効化

        return http.build();
    }
}

