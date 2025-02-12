package today.todaysentence.global.security;


import jakarta.servlet.DispatcherType;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.annotation.web.configurers.FormLoginConfigurer;
import org.springframework.security.config.annotation.web.configurers.HttpBasicConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import today.todaysentence.global.jwt.JwtAuthFilter;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Stream;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity(securedEnabled = true)
@RequiredArgsConstructor
public class CustomSecurityConfig {

    private final static String[] PERMIT_ALL_URI_MEMBER = {
            "/api/member/sign-up",
            "/api/member/sign-in",
            "/api/member/check-email",
            "/api/member/check-nickname",
            "/api/member/find-email",
            "/api/member/check-code",
            "/api/member/verify-code",
            "/api/member/find-password"

    };
    private final static String[] PERMIT_ALL_URI_UTIL = {
            "/swagger-ui/**",
            "/v3/api-docs/**",
    };
    private final static String[] PERMIT_ALL_URI_SEARCH = {
            "/api/search/**"
    };

    private final JwtAuthFilter jwtAuthFilter;

    private final EndpointHandler endpointHandler;


    @Bean
    public PasswordEncoder passwordEncoder(){
        return new BCryptPasswordEncoder();
    }


    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception{

        http
                .httpBasic(HttpBasicConfigurer::disable)
                .formLogin(FormLoginConfigurer::disable)

                .sessionManagement(s->s.sessionCreationPolicy(SessionCreationPolicy.STATELESS))

                .csrf(AbstractHttpConfigurer::disable)
                .cors(cors -> cors.configurationSource(corsConfigurationSource()))

                .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class)
//                .addFilterBefore(jwtExceptionFilter, JwtAuthFilter.class)

                .authorizeHttpRequests(request->{

                    String[] permitAllUri = Stream.of(
                                    PERMIT_ALL_URI_MEMBER,
                                    PERMIT_ALL_URI_UTIL,
                                    PERMIT_ALL_URI_SEARCH
                            )
                            .flatMap(Arrays::stream)
                            .toArray(String[]::new);

                    request.requestMatchers(permitAllUri).permitAll()
                            .dispatcherTypeMatchers(DispatcherType.ERROR).permitAll()
                            .anyRequest().authenticated();
                })
                .exceptionHandling(configure -> {
                    configure
                            .authenticationEntryPoint(endpointHandler)
                            .accessDeniedHandler(endpointHandler);
                });




                ;
        return http.build();

    }
    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.addAllowedOrigin("http://localhost:8081");
        configuration.addAllowedOrigin("http://localhost:8082");

        configuration.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "OPTIONS"));
        configuration.setAllowCredentials(true);

        configuration.setAllowedHeaders(List.of("Authorization", "Content-Type"));

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }


}
