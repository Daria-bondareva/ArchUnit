package chnu.edu.kn.bondareva.archunit.model;/*
  @author   User
  @project   ArchUnit
  @class  AuditMetadata
  @version  1.0.0 
  @since 22.11.2025 - 00.07
*/

import lombok.Data;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.annotation.LastModifiedDate;

import java.time.LocalDateTime;

@Data
public class AuditMetadata {
    @CreatedDate
    private LocalDateTime createdDate;
    @CreatedBy
    private String createdBy;
    @LastModifiedDate
    private LocalDateTime lastModifiedDate;
    @LastModifiedBy
    private String lastModifiedBy;
}
