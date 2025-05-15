package com.api.route_calculator.security;

import jakarta.servlet.http.HttpServletResponse;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@Profile("prod")
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception{
        return http
               // .csrf(AbstractHttpConfigurer::disable) // <--- ADD THIS LINE FOR TESTING
                .authorizeHttpRequests(auth -> {
                    auth.requestMatchers("/error","/", "oauth2/**").permitAll();
                    auth.requestMatchers("/login", "/login/**").permitAll();
                    auth.requestMatchers("/api/**", "api/auth/login").authenticated();
                    auth.anyRequest().authenticated();
                })
                .oauth2Login(oauth2 -> {
                    //oauth2.loginPage("/api/auth/google");
                    oauth2.defaultSuccessUrl("http://localhost:4200", true);
                    }
                )
                .sessionManagement(session ->
                    session.sessionCreationPolicy(SessionCreationPolicy.ALWAYS)
                )
//                .exceptionHandling(exception -> exception
//                        .authenticationEntryPoint((request, response, authException) -> {
//                            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
//                            response.getWriter().write("Unauthorized");
//                        })
//                        .accessDeniedHandler(((request, response, accessDeniedException) -> {
//                            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
//                            response.getWriter().write("Forbidden");
//                        }))
//                )
                .logout(logout -> logout
                        .logoutUrl("api/auth/logout")
                        .logoutSuccessHandler(((request, response, authentication) -> {
                            response.setStatus(HttpServletResponse.SC_OK);
                            response.getWriter().write("Logout successful");
                        }))
                        .invalidateHttpSession(true)
                        .deleteCookies("JSESSIONID")
                )



                .build();
    }

//    @Bean
//    public AuthenticationManager authenticationManager(UserDetailsService userDetailsService,
//                                                       PasswordEncoder passwordEncoder) {
//        DaoAuthenticationProvider provider = new DaoAuthenticationProvider();
//        provider.setUserDetailsService(userDetailsService);
//        provider.setPasswordEncoder(passwordEncoder);
//        return new ProviderManager(provider);
//    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

}
