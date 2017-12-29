import com.zhy.zooweb.web.ZkCacheServlet;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.ServletRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;

/**
 * Spring Boot 应用启动类
 * Created by zhuhongyu
 */
@ComponentScan(basePackages = "com.zhy.zooweb")
@SpringBootApplication
public class Application {

    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }

    @Bean
    public ServletRegistrationBean zkCacheServlet() {
        ServletRegistrationBean registration = new ServletRegistrationBean(new ZkCacheServlet());
        registration.addUrlMappings("/hello");
        return registration;
    }

}
