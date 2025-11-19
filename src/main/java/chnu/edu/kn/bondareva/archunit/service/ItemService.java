package chnu.edu.kn.bondareva.archunit.service;/*
  @author   User
  @project   ArchUnit
  @class  ItemService
  @version  1.0.0 
  @since 19.11.2025 - 20.02
*/

import chnu.edu.kn.bondareva.archunit.model.Item;
import chnu.edu.kn.bondareva.archunit.repository.ItemRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ItemService {

    private final ItemRepository itemRepository;
    private List<Item> items = new ArrayList<>();

    {
        items.add(new Item("1", "name1", "000001","description1"));
        items.add(new Item("2", "name2", "000002","description3"));
        items.add(new Item("3", "name3", "000003","description3"));
    }

    // @PostConstruct // Можна закоментувати, щоб не падало без запущеної бази Mongo, для ArchUnit це не важливо
    void init() {
        // itemRepository.deleteAll();
        // itemRepository.saveAll(items);
    }

    public List<Item> getAll() {
        return itemRepository.findAll();
    }

    public Item getById(String id) {
        return itemRepository.findById(id).orElse(null);
    }

    public Item create(Item item) {
        return itemRepository.save(item);
    }

    public Item update(Item item) {
        return itemRepository.save(item);
    }

    public void delById(String id) {
        itemRepository.deleteById(id);
    }
}
