package chnu.edu.kn.bondareva.archunit;/*
  @author   User
  @project   ArchUnit
  @class  AuditTest
  @version  1.0.0 
  @since 01.12.2025 - 14.47
*/

import chnu.edu.kn.bondareva.archunit.model.Item;
import chnu.edu.kn.bondareva.archunit.service.ItemService;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class AuditTest {

    @Autowired
    ItemService underTest;

    @BeforeAll
    void beforeAll() {
    }

    @BeforeEach
    void setUp() {
        Item macbook = new Item("MacBook Pro M3", "APPLE_MAC_M3", "###test");
        Item iphone = new Item("iPhone 15 Pro", "APPLE_IPHONE_15", "###test");
        Item pixel = new Item("Google Pixel 8", "GOOGLE_PIXEL_8", "###test");
        underTest.createAll(List.of(macbook, iphone, pixel));
    }

    @AfterEach
    void tearDown() {
        List<Item> itemsToDelete = underTest.getAll().stream()
                .filter(item -> item.getDescription().contains("###test"))
                .toList();
        for (Item item : itemsToDelete) {
            underTest.delById(item.getId());
        }
        // underTest.deleteAll(itemsToDelete);
    }

    @AfterAll
    void afterAll() {}

    @Test
    void testSetShouldContains_3_Records_ToTest(){
        List<Item> itemsToDelete = underTest.getAll().stream()
                .filter(item -> item.getDescription().contains("###test"))
                .toList();
        assertEquals(3,itemsToDelete.size());
    }

    @Test
    void whenCreateNewItemThenAuditIsFullPresent(){
        // given
        Item item = new Item("Sony PlayStation 5", "SONY_PS5", "Gaming console ###test");
        // when

        Item ItemCreated =  underTest.create(item);
        // then
        assertNotNull(ItemCreated);
        assertNotNull(ItemCreated.getId());
        assertNotNull(ItemCreated.getCreateDate());
        assertNotNull(ItemCreated.getLastModifiedDate());
        assertEquals(item.getCreateDate(), ItemCreated.getLastModifiedDate());

    }

    @Test
    void whenUpdateItemThenAuditIs(){
        // given
        Item item = new Item("Sony PlayStation 5", "SONY_PS5", "Gaming console ###test");
        // when

        Item itemCreated =  underTest.create(item);
        Item itemUpdated =  underTest.update(item);
        // then
        assertNotNull(itemCreated);
        assertNotNull(itemCreated.getId());
        assertNotNull(itemCreated.getCreateDate());
        assertNotNull(itemCreated.getLastModifiedDate());
        assertTrue(item.getCreateDate().isBefore(itemUpdated.getLastModifiedDate()));
    }

    @Test
    void whenUpdateItemThenAuditIsCorrect() throws InterruptedException {
        // given
        Item item = new Item("Xbox Series X", "MS_XBOX", "Console ###test");
        Item itemCreated = underTest.create(item);
        Thread.sleep(100); // Пауза, щоб час відрізнявся

        // when
        if (itemCreated.getUpdateDate() != null) {
            itemCreated.getUpdateDate().add(LocalDateTime.now());
        }
        itemCreated.setName("Xbox Series X Updated");
        Item itemUpdated = underTest.update(itemCreated);
        // then
        assertNotNull(itemCreated.getCreateDate());
        assertFalse(itemUpdated.getUpdateDate().isEmpty());

        LocalDateTime lastUpdate = itemUpdated.getUpdateDate().get(itemUpdated.getUpdateDate().size() - 1);
        assertTrue(lastUpdate.isAfter(itemCreated.getCreateDate()));
    }

    @Test
    void whenUpdateItemThenCreatedDateShouldNotChange() throws InterruptedException {
        // given
        Item item = new Item("Nintendo Switch", "NINTENDO", "Portable ###test");
        Item createdItem = underTest.create(item);
        LocalDateTime originalCreationDate = createdItem.getCreateDate();
        Thread.sleep(50);

        // when
        createdItem.setName("Nintendo Switch OLED");
        createdItem.getUpdateDate().add(LocalDateTime.now());
        Item updatedItem = underTest.update(createdItem);
        // then
        assertEquals(originalCreationDate, updatedItem.getCreateDate());
    }

    @Test
    void whenUpdateItemTwiceThenHistoryShouldHaveTwoRecords() {
        // given
        Item item = new Item("iPad Air", "APPLE_IPAD", "Tablet ###test");
        Item created = underTest.create(item);
        // when
        created.getUpdateDate().add(LocalDateTime.now());
        Item update1 = underTest.update(created);

        update1.getUpdateDate().add(LocalDateTime.now());
        Item update2 = underTest.update(update1);
        // then
        assertEquals(2, update2.getUpdateDate().size());
    }
}