package chnu.edu.kn.bondareva.archunit;/*
  @author   User
  @project   ArchUnit
  @class  IntegrationTests
  @version  1.0.0 
  @since 01.12.2025 - 19.32
*/

import chnu.edu.kn.bondareva.archunit.Utils.Utils;
import chnu.edu.kn.bondareva.archunit.model.Item;
import chnu.edu.kn.bondareva.archunit.repository.ItemRepository;
import chnu.edu.kn.bondareva.archunit.request.ItemCreateRequest;
import chnu.edu.kn.bondareva.archunit.request.ItemUpdateRequest;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
public class IntegrationTests {
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ItemRepository repository;

    private List<Item> items = new ArrayList<>();

    @BeforeEach
    void setUp (){
        items.add(new Item("MacBook Pro M3", "APPLE_MAC_M3", "Premium laptop"));
        items.add(new Item("iPhone 15 Pro", "APPLE_IPHONE_15", "Titanium smartphone"));
        items.add(new Item("Google Pixel 8", "GOOGLE_PIXEL_8", "Android flagship"));
        repository.saveAll(items);
    }

    @AfterEach
    void tearsDown(){
        repository.deleteAll();
    }

    @Test
    void whenCodeIsUniqueThenItShouldCreateNewItem() throws Exception {
        // given
        ItemCreateRequest request = new ItemCreateRequest(
                "Sony PlayStation 5", "SONY_PS5", "Gaming Console");
        // when
        ResultActions perform = mockMvc.perform(post("/api/v1/items/dto")
                .contentType(MediaType.APPLICATION_JSON)
                .content(Utils.toJson(request)));
        // then
        Item item = repository.findAll().stream()
                .filter(it -> it.getCode().equals(request.code()))
                .findFirst().orElse(null);

        perform.andExpect(status().isOk());
        assertThat(repository.existsByCode(request.code())).isTrue();
        assertNotNull(item);
        assertNotNull(item.getId());
        assertThat(item.getId()).isNotEmpty();
        assertThat(item.getId().length()).isEqualTo(24);
        assertThat(item.getDescription()).isEqualTo(request.description());
        assertThat(item.getName()).isEqualTo(request.name());
        assertThat(item.getCode()).isEqualTo(request.code());
        assertNotNull(item.getUpdateDate());
        assertThat(item.getCreateDate()).isNotNull();
    }

    @Test
    @DisplayName("Create item - negative scenario: code is already present")
    void whenCodeExistsThenItShouldNotCreateDuplicate() throws Exception {
        // given
        ItemCreateRequest request = new ItemCreateRequest(
                "MacBook Clone", "APPLE_MAC_M3", "Clone desc");
        // when
        mockMvc.perform(post("/api/v1/items/dto")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(Utils.toJson(request)))
                .andExpect(status().isOk());
        // then
        long count = repository.findAll().stream()
                .filter(it -> it.getCode().equals("APPLE_MAC_M3"))
                .count();
        assertEquals(1, count, "Має залишитися тільки 1 запис з таким кодом");
    }

    @Test
    @DisplayName("Update - happy path")
    void whenUpdateExistingItemThenItShouldUpdateDB() throws Exception {
        // given
        Item existingItem = repository.findAll().get(0);
        String id = existingItem.getId();
        ItemUpdateRequest updateRequest = new ItemUpdateRequest(
                id, "Updated Mac", "UPDATED_CODE", "Updated Desc");
        // when
        mockMvc.perform(put("/api/v1/items/dto")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(Utils.toJson(updateRequest)))
                .andExpect(status().isOk());
        // then
        Item updatedItem = repository.findById(id).orElseThrow();
        assertEquals("Updated Mac", updatedItem.getName());
        assertEquals("UPDATED_CODE", updatedItem.getCode());
        assertFalse(updatedItem.getUpdateDate().isEmpty());
    }

    @Test
    @DisplayName("Update - negative")
    void whenUpdateNonExistingItemThenItShouldReturnNull() throws Exception {
        // given
        ItemUpdateRequest updateRequest = new ItemUpdateRequest(
                "fake_id_12345678901234567890", "Ghost", "GHOST", "Desc");
        // when
        mockMvc.perform(put("/api/v1/items/dto")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(Utils.toJson(updateRequest)))
                .andExpect(status().isOk());
        // then
        assertFalse(repository.existsById("fake_id_12345678901234567890"));
    }

    @Test
    @DisplayName("Get one - positive")
    void whenGetByIdThenReturnItem() throws Exception {
        Item item = repository.findAll().get(1);

        mockMvc.perform(get("/api/v1/items/" + item.getId()))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("Get one - negative")
    void whenGetUnknownIdThenReturnOkOrEmpty() throws Exception {
        mockMvc.perform(get("/api/v1/items/unknown_id_123"))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("Delete - positive")
    void whenDeleteByIdThenItemShouldBeRemoved() throws Exception {
        Item item = repository.findAll().get(2);
        mockMvc.perform(delete("/api/v1/items/" + item.getId()))
                .andExpect(status().isOk());

        assertFalse(repository.existsById(item.getId()));
    }

    @Test
    @DisplayName("Delete - negative")
    void whenDeleteUnknownIdThenReturnOk() throws Exception {
        mockMvc.perform(delete("/api/v1/items/fake_id_123"))
                .andExpect(status().isOk());

        assertEquals(3, repository.count());
    }

    @Test
    void whenGetAllThenReturnListOf3() throws Exception {
        mockMvc.perform(get("/api/v1/items/"))
                .andExpect(status().isOk());
        assertEquals(3, repository.count());
    }

    @Test
    void whenCreateLegacyItemThenItWorks() throws Exception {
        Item newItem = new Item("Legacy Item", "LEGACY_01", "Old way");

        mockMvc.perform(post("/api/v1/items/")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(Utils.toJson(newItem)))
                .andExpect(status().isOk());

        assertTrue(repository.findAll().stream()
                .anyMatch(i -> i.getCode().equals("LEGACY_01")));
    }
}
