package chnu.edu.kn.bondareva.archunit.service;/*
  @author   User
  @project   ArchUnit
  @class  ItemService
  @version  1.0.0 
  @since 19.11.2025 - 20.02
*/

import chnu.edu.kn.bondareva.archunit.model.Item;
import chnu.edu.kn.bondareva.archunit.repository.ItemRepository;
import chnu.edu.kn.bondareva.archunit.request.ItemCreateRequest;
import chnu.edu.kn.bondareva.archunit.request.ItemUpdateRequest;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ItemService {

    private final ItemRepository itemRepository;
    private List<Item> items = new ArrayList<>();

    {
        items.add(new Item("name1", "000001","description1"));
        items.add(new Item("2", "name2", "000002","description3"));
        items.add(new Item("3", "name3", "000003","description3"));
    }

    @PostConstruct
    void init() {
        itemRepository.deleteAll();
        itemRepository.saveAll(items);
    }

    public List<Item> getAll() {
        return itemRepository.findAll();
    }

    public void createAll(List<Item> items) {
        LocalDateTime now = LocalDateTime.now();
        for (Item item : items) {
            if (item.getCreateDate() == null) item.setCreateDate(now);
            if (item.getLastModifiedDate() == null) item.setLastModifiedDate(now); // Додаємо це
        }
        itemRepository.saveAll(items);
    }
    public Item getById(String id) {
        return itemRepository.findById(id).orElse(null);
    }

    public Item create(Item item) {
        LocalDateTime now = LocalDateTime.now();
        item.setCreateDate(now);

        item.setLastModifiedDate(now);

        if (item.getUpdateDate() == null) {
            item.setUpdateDate(new ArrayList<>());
        }
        return itemRepository.save(item);
    }

    public Item create(ItemCreateRequest request){
        if (itemRepository.existsByCode(request.code())){
            return null;
        }
        Item item = mapToItem(request);

        LocalDateTime now = LocalDateTime.now();
        item.setCreateDate(now);

        item.setLastModifiedDate(now);

        item.setUpdateDate(new ArrayList<LocalDateTime>());
        return itemRepository.save(item);
    }

    public Item update(Item item) {
        if (item.getUpdateDate() == null) {
            item.setUpdateDate(new ArrayList<>());
        }
        LocalDateTime now = LocalDateTime.now();
        //item.getUpdateDate().add(now);
        item.setLastModifiedDate(now);

        return itemRepository.save(item);
    }

    public Item update(ItemUpdateRequest request) {
        Item item = itemRepository.findById(request.id()).orElse(null);
        if (item == null) {
            return null;
        }

        item.setName(request.name());
        item.setCode(request.code());
        item.setDescription(request.description());

        if (item.getUpdateDate() == null) {
            item.setUpdateDate(new ArrayList<>());
        }
        LocalDateTime now = LocalDateTime.now();
        item.getUpdateDate().add(now);
        item.setLastModifiedDate(now);

        return itemRepository.save(item);
    }

    public void delById(String id) {
        itemRepository.deleteById(id);
    }
    private Item mapToItem(ItemCreateRequest request){
        Item item = new Item(request.name(), request.code(), request.description());
        return item;
    }
}
