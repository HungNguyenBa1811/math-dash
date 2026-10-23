package ptit.e23cnpm01.nguyenbahung.mathdash;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import ptit.e23cnpm01.nguyenbahung.mathdash.controller.TokenInterceptor;

/**
 * Điểm khởi động backend Math Dash 1v1.
 */
@SpringBootApplication
public class MathDashApplication implements WebMvcConfigurer {

    public static void main(String[] args) {
        SpringApplication.run(MathDashApplication.class, args);
    }

    // Mọi API trận đấu phải kèm token đăng nhập
    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(new TokenInterceptor()).addPathPatterns("/api/matches", "/api/matches/**");
    }
}
