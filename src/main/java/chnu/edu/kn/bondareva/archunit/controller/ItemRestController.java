package chnu.edu.kn.bondareva.archunit.controller;/*
  @author   User
  @project   ArchUnit
  @class  ItemRestController
  @version  1.0.0 
  @since 19.11.2025 - 19.58
*/

import chnu.edu.kn.bondareva.archunit.model.Item;
import chnu.edu.kn.bondareva.archunit.service.ItemService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("api/v1/items/")
@RequiredArgsConstructor
public class ItemRestController {

    private final ItemService itemService;

    @GetMapping
    public List<Item> showAll() {
        return itemService.getAll();
    }

    @GetMapping("{id}")
    public Item showOneById(@PathVariable String id) {
        return itemService.getById(id);
    }

    @PostMapping
    public Item insert(@RequestBody Item item) {
        return itemService.create(item);
    }

    @PutMapping
    public Item edit(@RequestBody Item item) {
        return itemService.update(item);
    }

    @DeleteMapping("{id}")
    public void delete(@PathVariable String id) {
        itemService.delById(id);
    }
}
