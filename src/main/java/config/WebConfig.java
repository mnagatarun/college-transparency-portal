package portal.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(new RoleInterceptor("FACULTY")).addPathPatterns("/faculty/**");
        registry.addInterceptor(new RoleInterceptor("ADMIN")).addPathPatterns("/admin/**");
    }
}