package emt.sacco.middleware.Security;
//import emt.sacco.middleware.Utils.RequestResponseLongs.RequestResponseLogger;
//import emt.sacco.middleware.Utils.RequestResponseLongs.RequestResponseLogger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebMvcConfig implements WebMvcConfigurer {
//
//    @Autowired
//    private RequestResponseLogger requestResponseLogger;
//    @Override
//    public void addInterceptors(InterceptorRegistry registry) {
//        registry.addInterceptor(requestResponseLogger);
//    }
    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**")

//        registry.addMapping("/api/v1/mobile/AtmGls")

//                .allowedOrigins("http://localhost:4200")
                .allowedOrigins("*")
                .allowedMethods("GET", "POST", "PUT", "DELETE")
                .allowedHeaders("*");
    }
}
