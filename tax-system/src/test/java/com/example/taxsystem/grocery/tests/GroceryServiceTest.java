package com.example.taxsystem.grocery.tests;

import com.example.taxsystem.converter.application.ConverterEngine;
import com.example.taxsystem.grocery.api.CreateGroceryItemRequest;
import com.example.taxsystem.grocery.api.GroceryItemResponse;
import com.example.taxsystem.grocery.application.GroceryService;
import com.example.taxsystem.grocery.domain.GroceryStatus;
import com.example.taxsystem.grocery.infrastructure.GroceryItemEntity;
import com.example.taxsystem.grocery.infrastructure.GroceryRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;

import javax.persistence.EntityNotFoundException;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class GroceryServiceTest {

    @Mock
    private GroceryRepository groceryRepository;

    @Mock
    private ConverterEngine converterEngine;

    @Captor
    private ArgumentCaptor<GroceryItemEntity> entityCaptor;

    @InjectMocks
    private GroceryService groceryService;

    @Test
    void shouldCreatePendingItemAndTrimName() {
        CreateGroceryItemRequest request = new CreateGroceryItemRequest();
        request.setName("  oranges  ");

        GroceryItemEntity saved = new GroceryItemEntity();
        saved.setId(7L);
        saved.setName("oranges");
        saved.setStatus(GroceryStatus.PENDING);

        GroceryItemResponse response = new GroceryItemResponse();
        response.setId(7L);
        response.setName("oranges");
        response.setStatus(GroceryStatus.PENDING);

        when(groceryRepository.save(any(GroceryItemEntity.class))).thenReturn(saved);
        when(converterEngine.convert(saved, com.example.taxsystem.grocery.domain.GroceryItem.class))
                .thenReturn(new com.example.taxsystem.grocery.domain.GroceryItem());
        when(converterEngine.convert(any(com.example.taxsystem.grocery.domain.GroceryItem.class), eq(GroceryItemResponse.class)))
                .thenReturn(response);

        GroceryItemResponse created = groceryService.create(request);

        verify(groceryRepository).save(entityCaptor.capture());
        assertThat(entityCaptor.getValue().getName()).isEqualTo("oranges");
        assertThat(entityCaptor.getValue().getStatus()).isEqualTo(GroceryStatus.PENDING);
        assertThat(created.getId()).isEqualTo(7L);
    }

    @Test
    void shouldMarkExistingItemAsDone() {
        GroceryItemEntity entity = new GroceryItemEntity();
        entity.setId(99L);
        entity.setName("coffee");
        entity.setStatus(GroceryStatus.PENDING);

        GroceryItemResponse response = new GroceryItemResponse();
        response.setId(99L);
        response.setStatus(GroceryStatus.DONE);

        doReturn(Optional.of(entity)).when(groceryRepository).findById(99L);
        when(groceryRepository.save(any(GroceryItemEntity.class))).thenAnswer(invocation -> invocation.getArgument(0));
        when(converterEngine.convert(any(GroceryItemEntity.class), eq(com.example.taxsystem.grocery.domain.GroceryItem.class)))
                .thenReturn(new com.example.taxsystem.grocery.domain.GroceryItem());
        when(converterEngine.convert(any(com.example.taxsystem.grocery.domain.GroceryItem.class), eq(GroceryItemResponse.class)))
                .thenReturn(response);

        GroceryItemResponse result = groceryService.markDone(99L);

        verify(groceryRepository).findById(eq(99L));
        verify(groceryRepository).save(any(GroceryItemEntity.class));
        assertThat(entity.getStatus()).isEqualTo(GroceryStatus.DONE);
        assertThat(entity.getCompletedAt()).isNotNull();
        assertThat(result.getStatus()).isEqualTo(GroceryStatus.DONE);
    }

    @Test
    void shouldPropagateDeleteFailure() {
        GroceryItemEntity entity = new GroceryItemEntity();
        entity.setId(5L);

        when(groceryRepository.findById(5L)).thenReturn(Optional.of(entity));
        doThrow(new IllegalStateException("delete failed")).when(groceryRepository).delete(any(GroceryItemEntity.class));

        assertThatThrownBy(() -> groceryService.remove(5L))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("delete failed");
    }

    @Test
    void shouldFailWhenItemDoesNotExist() {
        when(groceryRepository.findById(404L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> groceryService.redo(404L))
                .isInstanceOf(EntityNotFoundException.class)
                .hasMessageContaining("404");
    }
}
