package chnu.edu.kn.bondareva.archunit.config;/*
  @author   User
  @project   ArchUnit
  @class  AuditorAware
  @version  1.0.0 
  @since 22.11.2025 - 00.10
*/

import org.springframework.data.domain.AuditorAware;

import java.util.Optional;

public class AuditorAwareImpl implements AuditorAware<String> {
    @Override
    public Optional<String> getCurrentAuditor() {
        return Optional.of(System.getProperty("user.name"));
    }
}
