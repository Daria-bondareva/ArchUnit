package chnu.edu.kn.bondareva.archunit.repository;/*
  @author   User
  @project   ArchUnit
  @class  ItemRepository
  @version  1.0.0 
  @since 19.11.2025 - 20.01
*/

import chnu.edu.kn.bondareva.archunit.model.Item;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ItemRepository extends MongoRepository<Item, String> {
}
