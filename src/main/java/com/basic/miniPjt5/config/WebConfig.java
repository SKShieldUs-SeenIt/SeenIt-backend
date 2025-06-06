package com.basic.miniPjt5.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {
    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {

        registry.addResourceHandler("/webjars/**")
                .addResourceLocations("classpath:/META-INF/resources/webjars/");

        registry.addResourceHandler("/swagger-ui.html")
                .addResourceLocations("classpath:/META-INF/resources/"); // webjars/swagger-ui/ 안에 있는 index.html을 찾도록 유도

        registry.addResourceHandler("/swagger-ui/**")
                .addResourceLocations("classpath:/META-INF/resources/webjars/swagger-ui/"); // /swagger-ui/xxx 요청 처리 (예: /swagger-ui/swagger-ui-bundle.js)


        // 기존에 spring.web.resources.static-locations에 지정된 경로들도 추가 (안전하게)
        registry.addResourceHandler("/**") // 모든 나머지 요청
                .addResourceLocations("classpath:/META-INF/resources/", "classpath:/META-INF/resources/webjars/");


        // 중요: 항상 부모 메서드를 호출하여 기본 동작을 유지합니다.
        WebMvcConfigurer.super.addResourceHandlers(registry);
    }


    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**")
                .allowedOrigins("http://localhost:5173", "http://localhost:8080") // 와일드카드 없이 정확하게
                .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS")
                .allowedHeaders("*")
                .allowCredentials(true);  // true로 변경
    }

}
