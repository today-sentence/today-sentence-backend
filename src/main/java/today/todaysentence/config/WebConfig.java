package today.todaysentence.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {
    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**")
                .allowedOrigins("http://localhost:8080","http://13.209.47.32:8080")  // Swagger UI의 도메인 추가
                .allowedMethods("GET", "POST", "PUT", "DELETE")  // 필요한 메서드 추가
                .allowedHeaders("*")  // 모든 헤더 허용
                .allowCredentials(true);  // 쿠키를 허용하려면 true로 설정
    }
}