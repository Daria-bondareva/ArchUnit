package chnu.edu.kn.bondareva.archunit.config;/*
  @author   User
  @project   ArchUnit
  @class  AuditionConfiguration
  @version  1.0.0 
  @since 22.11.2025 - 00.15
*/

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.domain.AuditorAware;
import org.springframework.data.mongodb.config.EnableMongoAuditing;

@Configuration
@EnableMongoAuditing
public class AuditionConfiguration {
    @Bean
    public AuditorAware<String> auditorAware() {
        return new AuditorAwareImpl();
    }
}
