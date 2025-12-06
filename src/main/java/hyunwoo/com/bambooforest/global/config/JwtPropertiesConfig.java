package hyunwoo.com.bambooforest.global.config;

import hyunwoo.com.bambooforest.global.security.properties.JwtProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableConfigurationProperties({JwtProperties.class})
public class JwtPropertiesConfig {
    // JwtProperties 빈 등록
}
