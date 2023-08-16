package com.example.server.configuration;

import com.example.server.utility.JwtUtilities;
import com.example.server.web.filter.JwtFilter;
import com.example.server.model.User;
import com.example.server.service.UserService;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
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
import java.util.Map;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    private UserService userService;

    @Autowired
    public void setUserService(UserService userService) {
        this.userService = userService;
    }

    @Bean
    public static PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public LogoutSuccessHandler logoutSuccessHandler() {

        return new LogoutSuccessHandler() {

            JwtUtilities jwtUtilities;

            @Autowired
            public void setJwtUtils(JwtUtilities jwtUtilities) {
                this.jwtUtilities = jwtUtilities;
            }

            @Override
            public void onLogoutSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {

                Object attribute = request.getAttribute(Constants.Jwt.REQUEST_ATTRIBUTE_TOKEN_KEY);

                if (attribute instanceof Map.Entry entry && entry.getKey() instanceof String username && entry.getValue() instanceof String token) {
                    jwtUtilities.blackListJwt(username, token);
                }
            }
        };
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {

        CorsConfiguration configuration = new CorsConfiguration();
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();

        Constants.RestApi.CORS_ALLOWED_ORIGINS.forEach(configuration::addAllowedOrigin);
        Constants.RestApi.CORS_ALLOWED_METHODS.forEach(configuration::addAllowedMethod);
        Constants.RestApi.CORS_ALLOWED_HEADERS.forEach(configuration::addAllowedHeader);
        configuration.setAllowCredentials(true);
        source.registerCorsConfiguration("/**", configuration);

        return source;
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http, JwtFilter jwtFilter, ApplicationContext context) throws Exception {

        return http
                .authorizeHttpRequests(req -> req
                        .requestMatchers(
                                Constants.RestApi.REST_API_ROUTE_PREFIX + "/users",
                                Constants.RestApi.REST_API_ROUTE_PREFIX + "/auth/login"
                        )
                        .permitAll()
                        .anyRequest()
                        .authenticated()
                )
                .addFilterBefore(jwtFilter, LogoutFilter.class)
                .formLogin(AbstractHttpConfigurer::disable)
                .httpBasic(basic -> basic.authenticationEntryPoint(new HttpStatusEntryPoint(HttpStatus.UNAUTHORIZED)))
                .logout(logout -> logout
                        .logoutSuccessHandler(logoutSuccessHandler())
                        .logoutUrl(Constants.RestApi.REST_API_ROUTE_PREFIX + "/auth/logout")
                )
                .csrf(AbstractHttpConfigurer::disable)
                .cors(cors -> cors.configurationSource(corsConfigurationSource()))
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .build();
    }

    @Bean
    public UserDetailsService userDetailsService() {

        return new UserDetailsService() {

            @Override
            public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {

                User user = userService.findUser(username);

                return org.springframework.security.core.userdetails.User.builder()
                        .username(user.getUsername())
                        .password(user.getPassword())
                        .roles(user.getRoles().toArray(new String[0]))
                        .build();
            }
        };
    }
}
