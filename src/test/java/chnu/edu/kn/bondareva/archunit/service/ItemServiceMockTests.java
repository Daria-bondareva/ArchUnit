package chnu.edu.kn.bondareva.archunit.service;/*
  @author   User
  @project   ArchUnit
  @class  ItemServiceMockTests
  @version  1.0.0 
  @since 30.11.2025 - 23.41
*/

import chnu.edu.kn.bondareva.archunit.model.Item;
import chnu.edu.kn.bondareva.archunit.repository.ItemRepository;
import chnu.edu.kn.bondareva.archunit.request.ItemCreateRequest;
import chnu.edu.kn.bondareva.archunit.request.ItemUpdateRequest;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat; // Для assertThat
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
public class ItemServiceMockTests {
    @Mock
    private ItemRepository mockRepository;

    private ItemService underTest;

    @Captor
    private ArgumentCaptor<Item> argumentCaptor;

    private ItemCreateRequest request;
    private Item item;

    @BeforeEach
    void setUp (){
        MockitoAnnotations.openMocks(this);
        underTest = new ItemService(mockRepository);
    }
    @AfterEach
    void tearsDown(){

    }

    @DisplayName("Create new item. Happy path")
    @Test
    void whenInsertNewItemAndCodeNotExistsThenOk(){
        //given
        request = new ItemCreateRequest("MacBook", "APPLE01", "Laptop");
        item = Item.builder()
                .name(request.name())
                .code(request.code())
                .description(request.description())
                .build();
        given(mockRepository.existsByCode(request.code())).willReturn(false);
        //when
        underTest.create(request);
        // then
        then(mockRepository).should().save(argumentCaptor.capture());
        Item itemToSave = argumentCaptor.getValue();
        assertThat(itemToSave.getName()).isEqualTo(request.name());
        assertNotNull(itemToSave.getCreatedDate());
        assertTrue(itemToSave.getCreatedDate().isBefore(LocalDateTime.now().plusSeconds(1)));
        assertTrue(itemToSave.getUpdateDate().isEmpty());
        verify(mockRepository).save(itemToSave);
        verify(mockRepository, times(1)).existsByCode(request.code());
        verify(mockRepository, times(1)).save(itemToSave);
    }

    @DisplayName("Create new item: Wrong Path (Duplicate)")
    @Test
    void whenInsertItemAndCodeExistsThenReturnNull() {
        // given
        ItemCreateRequest request = new ItemCreateRequest("MacBook", "APPLE01", "Laptop");
        given(mockRepository.existsByCode(request.code())).willReturn(true);
        // when
        Item result = underTest.create(request);
        // then
        assertThat(result).isNull();
        verify(mockRepository, never()).save(any(Item.class));
    }

    @DisplayName("Update item: Happy Path")
    @Test
    void whenUpdateItemAndIdExistsThenOk() {
        // given
        String id = "100";
        ItemUpdateRequest updateRequest = new ItemUpdateRequest(id, "New Name", "CODE_NEW", "New Desc");

        Item existingItem = new Item("Old Name", "CODE_OLD", "Old Desc");
        existingItem.setId(id);
        existingItem.setUpdateDate(new ArrayList<>());

        given(mockRepository.findById(id)).willReturn(Optional.of(existingItem));
        // when
        underTest.update(updateRequest);
        // then
        then(mockRepository).should().save(argumentCaptor.capture());
        Item capturedItem = argumentCaptor.getValue();

        assertThat(capturedItem.getName()).isEqualTo("New Name");
        assertThat(capturedItem.getCode()).isEqualTo("CODE_NEW");
        assertTrue(capturedItem.getUpdateDate().size() == 1);

        verify(mockRepository).save(capturedItem);
    }

    @DisplayName("Update item: Wrong Path (Not Found)")
    @Test
    void whenUpdateItemAndIdNotExistsThenReturnNull() {
        // given
        ItemUpdateRequest updateRequest = new ItemUpdateRequest("999", "Name", "Code", "Desc");
        given(mockRepository.findById("999")).willReturn(Optional.empty());
        // when
        Item result = underTest.update(updateRequest);
        // then
        assertThat(result).isNull();
        verify(mockRepository, never()).save(any(Item.class));
    }

    @DisplayName("Delete item: Verify call")
    @Test
    void whenDeleteByIdThenRepositoryMethodCalled() {
        // given
        String id = "555";
        // when
        underTest.delById(id);
        // then
        verify(mockRepository, times(1)).deleteById(id);
    }
}
