package chnu.edu.kn.bondareva.archunit.model;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
/*
  @author   User
  @project   ArchUnit
  @class  Item
  @version  1.0.0 
  @since 19.11.2025 - 20.01
*/

@Data
@NoArgsConstructor
@Document
@Getter
@Setter
@ToString
@Builder
public class Item extends AuditMetadata{
    @Id
    private String id;
    private String name;
    private String code;
    private String description;

    public Item(String name, String code, String description) {
        this.name = name;
        this.code = code;
        this.description = description;
    }
    public Item(String id, String name, String code, String description) {
        this.id = id;
        this.name = name;
        this.code = code;
        this.description = description;
    }
}
