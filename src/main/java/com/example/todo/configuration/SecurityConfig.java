package com.example.todo.configuration;

import com.example.todo.exception.NotFoundException;
import com.example.todo.model.Role;
import com.example.todo.web.filter.JwtFilter;
import com.example.todo.model.User;
import com.example.todo.service.UserService;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatus;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.HttpStatusEntryPoint;
import org.springframework.security.web.authentication.logout.LogoutFilter;
import org.springframework.security.web.authentication.logout.LogoutSuccessHandler;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import java.io.IOException;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    private UserService userService;

    EnvironmentValues environmentValues;

    @Autowired
    public void setUserService(UserService userService) {
        this.userService = userService;
    }

    @Autowired
    public void setEnvironmentValues(EnvironmentValues environmentValues) {
        this.environmentValues = environmentValues;
    }

    @Bean
    public static PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public LogoutSuccessHandler logoutSuccessHandler() {

        return new LogoutSuccessHandler() {

            private final Logger logger = LoggerFactory.getLogger(LogoutSuccessHandler.class);
            private JwtFilter jwtFilter;

            @Autowired
            public void setJwtFilter(JwtFilter jwtFilter) {
                this.jwtFilter = jwtFilter;
            }

            @Override
            public void onLogoutSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {

                jwtFilter.blacklistJwtInRequestAttribute(request);
                logger.info("JWT token blacklisted at request url: {}", request.getRequestURL());
            }
        };
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {

        CorsConfiguration configuration = new CorsConfiguration();
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();

        environmentValues.TODO_CORS_ALLOWED_ORIGINS.forEach(configuration::addAllowedOrigin);
        environmentValues.TODO_CORS_ALLOWED_METHODS.forEach(configuration::addAllowedMethod);
        environmentValues.TODO_CORS_ALLOWED_HEADERS.forEach(configuration::addAllowedHeader);
        configuration.setAllowCredentials(true);
        source.registerCorsConfiguration("/**", configuration);

        return source;
    }

    @Bean
    public HttpStatusEntryPoint unauthorizedEntryPoint() {
        return new HttpStatusEntryPoint(HttpStatus.UNAUTHORIZED);
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http, JwtFilter jwtFilter) throws Exception {

        return http
                .authorizeHttpRequests(req -> req
                        .requestMatchers(
                                ConstantValues.RestApi.REST_API_ROUTE_PREFIX + "/users",
                                ConstantValues.RestApi.REST_API_ROUTE_PREFIX + "/auth/login"
                        )
                        .permitAll()
                        .anyRequest()
                        .authenticated()
                )
                .addFilterBefore(jwtFilter, LogoutFilter.class)
                .formLogin(AbstractHttpConfigurer::disable)
                .httpBasic(basic -> basic.authenticationEntryPoint(unauthorizedEntryPoint()))
                .logout(logout -> logout
                        .logoutSuccessHandler(logoutSuccessHandler())
                        .logoutUrl(ConstantValues.RestApi.REST_API_ROUTE_PREFIX + "/auth/logout")
                )
                .csrf(AbstractHttpConfigurer::disable)
                .cors(cors -> cors.configurationSource(corsConfigurationSource()))
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .build();
    }

    @Bean
    public UserDetailsService userDetailsService() {

        return new UserDetailsService() {

            private final Logger logger = LoggerFactory.getLogger(UserDetailsService.class);

            @Override
            public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {

                logger.debug("Parameter:: username: {}", username);

                try {

                    User user = userService.findUser(username);
                    logger.debug("Found user with username: {}", user.getUsername());

                    UserDetails userDetails = org.springframework.security.core.userdetails.User.builder()
                            .username(user.getUsername())
                            .password(user.getPassword())
                            .roles(user.getRoles().stream().map(Role::getName).toArray(String[]::new))
                            .build();
                    logger.info("Created UserDetails: {}", userDetails);

                    return userDetails;
                }
                catch (NotFoundException e) {
                    throw new UsernameNotFoundException(e.getMessage(), e);
                }
            }
        };
    }
}
