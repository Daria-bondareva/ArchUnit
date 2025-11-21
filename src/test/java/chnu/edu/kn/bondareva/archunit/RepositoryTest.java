package chnu.edu.kn.bondareva.archunit;/*
  @author   User
  @project   ArchUnit
  @class  RepositoryTest
  @version  1.0.0 
  @since 21.11.2025 - 22.41
*/

import chnu.edu.kn.bondareva.archunit.config.AuditionConfiguration;
import chnu.edu.kn.bondareva.archunit.model.Item;
import chnu.edu.kn.bondareva.archunit.repository.ItemRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.mongo.DataMongoTest;
import org.springframework.context.annotation.Import;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@DataMongoTest
@Import(AuditionConfiguration.class)
public class RepositoryTest {
    @Autowired
    ItemRepository underTest;

    @BeforeEach
    void setUp() {
        Item macbook = new Item("1", "MacBook Pro M3", "Apple", "###test");
        Item iphone = new Item("2", "iPhone 15 Pro", "Apple", "###test");
        Item pixel = new Item("3", "Google Pixel 8", "Google", "###test");
        Item sonyHeadphones = new Item("4", "Sony WH-1000XM5", "Sony", "###test");

        underTest.saveAll(List.of(macbook, iphone, pixel, sonyHeadphones));
    }

    @AfterEach
    void tearDown() {
        List<Item> itemsToDelete = underTest.findAll().stream()
                .filter(item -> item.getDescription().contains("###test"))
                .toList();
        underTest.deleteAll(itemsToDelete);
    }

    @Test
    void testSetShouldContains_4_Records_ToTest(){
        List<Item> items = underTest.findAll().stream()
                .filter(item -> item.getDescription().contains("###test"))
                .toList();
        assertEquals(4, items.size(), "Має бути 4 гаджети з методу setUp");
    }

    @Test
    void shouldGiveIdForNewRecord() {
        // given
        Item mouse = new Item("Logitech MX Master", "Logitech", "###test");
        // when
        underTest.save(mouse);

        Item itemFromDb = underTest.findAll().stream()
                .filter(item -> item.getName().equals("Logitech MX Master"))
                .findFirst().orElse(null);

        // then
        assertNotNull(itemFromDb);
        assertNotNull(itemFromDb.getId());
        assertFalse(itemFromDb.getId().isEmpty());
        assertEquals(24, itemFromDb.getId().length(), "Standard Mongo ID length");
    }

    @Test
    void whenRecordHasIdThenItIsPossibleToSave_UpdateMacbook() {
        Item updatedMac = Item.builder()
                .id("1")
                .name("MacBook Pro M3 Max") // Upgrade проца
                .code("Apple")
                .description("###test-upgraded")
                .build();

        // when
        underTest.save(updatedMac);

        Item itemFromDb = underTest.findById("1").orElseThrow();

        // then
        assertEquals("MacBook Pro M3 Max", itemFromDb.getName());
        assertEquals("###test-upgraded", itemFromDb.getDescription());
    }

    @Test
    void shouldFindItemsByCodeNative() {
        List<Item> appleProducts = underTest.findByCode("Apple");

        assertFalse(appleProducts.isEmpty());
        assertEquals(2, appleProducts.size());
        assertTrue(appleProducts.stream().anyMatch(i -> i.getName().equals("iPhone 15 Pro")));
    }

    @Test
    void shouldHandleEmptyStringId() {
        // given
        Item item = new Item("", "No ID Gadget", "TestBrand", "###test");

        // when
        underTest.save(item);

        // then
        assertTrue(underTest.existsById(""));

        // cleanup
        underTest.deleteById("");
    }

    @Test
    void shouldUpdatePixelToNewVersion() {
        // given - беремо Pixel 8 (id="3")
        Item pixel = underTest.findById("3").orElseThrow();

        // when - вийшла нова версія Android
        pixel.setDescription("###test - Android 15 Beta Installed");
        underTest.save(pixel);

        // then
        Item updatedPixel = underTest.findById("3").orElseThrow();
        assertTrue(updatedPixel.getDescription().contains("Android 15"));
    }

    @Test
    void shouldDeleteSonyHeadphones() {
        // given
        assertTrue(underTest.existsById("4"));

        // when
        underTest.deleteById("4");

        // then
        assertFalse(underTest.existsById("4"));
    }

    @Test
    void shouldPopulateAuditFields() {
        // given
        Item ipad = new Item("iPad Air", "Apple", "###test");

        // when
        Item saved = underTest.save(ipad);

        // then
        assertNotNull(saved.getCreatedDate(), "Дата створення має бути авто-заповнена");
        assertNotNull(saved.getCreatedBy(), "Автор має бути авто-заповнений");

        assertEquals(System.getProperty("user.name"), saved.getCreatedBy());
    }

    @Test
    void shouldFindAppleProductsUsingStream() {
        List<Item> appleGadgets = underTest.findAll().stream()
                .filter(item -> "Apple".equals(item.getCode()))
                .toList();

        assertEquals(2, appleGadgets.size());
    }

    @Test
    void shouldReturnEmptyForUnknownId() {
        Optional<Item> result = underTest.findById("999-non-existent");
        assertTrue(result.isEmpty());
    }
}
