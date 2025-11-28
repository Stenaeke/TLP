package com.stenaeke.TLP.config;


import com.stenaeke.TLP.security.JwtAuthenticationFilter;
import com.stenaeke.TLP.services.JWTService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@RequiredArgsConstructor
public class SecurityConfig {
    private final JWTService jwtService;

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }



        @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) {
            var jwtFilter = new JwtAuthenticationFilter(jwtService);

        http
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers(
                                "/error",
                                "/teacher/register",
                                "/auth/teacher/login",
                                "/v3/api-docs/**",
                                "/swagger-ui/**",
                                "/swagger-ui.html"
                        ).permitAll()
                        .requestMatchers("/teacher/**").hasAuthority("ROLE_TEACHER")
                        .anyRequest().authenticated()
                )
                .csrf(csrf -> csrf.disable())

                .httpBasic(httpBasic -> httpBasic.disable())
                .formLogin(formLogin -> formLogin.disable())
                .addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class);
        return http.build();
    }
}
