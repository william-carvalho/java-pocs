package com.example.guitarfactorysystem.service;

import com.example.guitarfactorysystem.dto.CreateCustomGuitarItemRequest;
import com.example.guitarfactorysystem.dto.CreateCustomGuitarRequest;
import com.example.guitarfactorysystem.dto.CustomGuitarOrderItemResponse;
import com.example.guitarfactorysystem.dto.CustomGuitarOrderResponse;
import com.example.guitarfactorysystem.entity.CustomGuitarOrder;
import com.example.guitarfactorysystem.entity.CustomGuitarOrderItem;
import com.example.guitarfactorysystem.entity.GuitarComponent;
import com.example.guitarfactorysystem.entity.GuitarModel;
import com.example.guitarfactorysystem.enums.OrderStatus;
import com.example.guitarfactorysystem.exception.BusinessValidationException;
import com.example.guitarfactorysystem.exception.ResourceNotFoundException;
import com.example.guitarfactorysystem.repository.CustomGuitarOrderRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class CustomGuitarService {

    private final CustomGuitarOrderRepository repository;
    private final GuitarModelService guitarModelService;
    private final ComponentService componentService;

    public CustomGuitarService(CustomGuitarOrderRepository repository,
                               GuitarModelService guitarModelService,
                               ComponentService componentService) {
        this.repository = repository;
        this.guitarModelService = guitarModelService;
        this.componentService = componentService;
    }

    @Transactional
    public CustomGuitarOrderResponse create(CreateCustomGuitarRequest request) {
        GuitarModel guitarModel = guitarModelService.findOrThrow(request.getGuitarModelId());
        Map<Long, Integer> requestedQuantities = aggregateQuantities(request.getItems());
        Map<Long, GuitarComponent> componentsById = new LinkedHashMap<>();

        for (Map.Entry<Long, Integer> entry : requestedQuantities.entrySet()) {
            GuitarComponent component = componentService.findOrThrow(entry.getKey());
            if (component.getQuantityInStock() < entry.getValue()) {
                throw new BusinessValidationException("Insufficient stock for component " + component.getName() +
                        ". Available: " + component.getQuantityInStock() + ", requested: " + entry.getValue());
            }
            componentsById.put(component.getId(), component);
        }

        CustomGuitarOrder order = new CustomGuitarOrder();
        order.setCustomerName(request.getCustomerName().trim());
        order.setGuitarModel(guitarModel);
        order.setStatus(OrderStatus.CREATED);
        order.setCreatedAt(LocalDateTime.now());

        List<CustomGuitarOrderItem> orderItems = new ArrayList<>();
        BigDecimal componentsTotal = BigDecimal.ZERO;

        for (Map.Entry<Long, Integer> entry : requestedQuantities.entrySet()) {
            GuitarComponent component = componentsById.get(entry.getKey());
            Integer quantity = entry.getValue();

            component.setQuantityInStock(component.getQuantityInStock() - quantity);

            CustomGuitarOrderItem item = new CustomGuitarOrderItem();
            item.setOrder(order);
            item.setComponent(component);
            item.setQuantity(quantity);
            orderItems.add(item);

            componentsTotal = componentsTotal.add(component.getUnitPrice().multiply(BigDecimal.valueOf(quantity)));
        }

        order.setItems(orderItems);
        order.setTotalPrice(guitarModel.getBasePrice().add(componentsTotal));

        return toResponse(repository.save(order));
    }

    @Transactional(readOnly = true)
    public List<CustomGuitarOrderResponse> list() {
        return repository.findAllByOrderByCreatedAtDescIdDesc()
                .stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public CustomGuitarOrderResponse get(Long id) {
        return toResponse(findOrThrow(id));
    }

    @Transactional(readOnly = true)
    public CustomGuitarOrder findOrThrow(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Custom guitar order not found for id " + id));
    }

    public CustomGuitarOrderResponse toResponse(CustomGuitarOrder order) {
        CustomGuitarOrderResponse response = new CustomGuitarOrderResponse();
        response.setOrderId(order.getId());
        response.setCustomerName(order.getCustomerName());
        response.setGuitarModel(guitarModelService.toSummary(order.getGuitarModel()));
        response.setStatus(order.getStatus());
        response.setCreatedAt(order.getCreatedAt());
        response.setItems(order.getItems().stream().map(this::toItemResponse).collect(Collectors.toList()));
        response.setBasePrice(order.getGuitarModel().getBasePrice());
        response.setTotalPrice(order.getTotalPrice());
        if (order.getWorkOrder() != null) {
            response.setWorkOrderId(order.getWorkOrder().getId());
            response.setWorkOrderNumber(order.getWorkOrder().getWorkOrderNumber());
        }
        return response;
    }

    private CustomGuitarOrderItemResponse toItemResponse(CustomGuitarOrderItem item) {
        CustomGuitarOrderItemResponse response = new CustomGuitarOrderItemResponse();
        response.setComponentId(item.getComponent().getId());
        response.setComponentName(item.getComponent().getName());
        response.setCategory(item.getComponent().getCategory());
        response.setQuantity(item.getQuantity());
        response.setUnitPrice(item.getComponent().getUnitPrice());
        response.setTotalPrice(item.getComponent().getUnitPrice().multiply(BigDecimal.valueOf(item.getQuantity())));
        return response;
    }

    private Map<Long, Integer> aggregateQuantities(List<CreateCustomGuitarItemRequest> items) {
        Map<Long, Integer> aggregated = new LinkedHashMap<>();
        for (CreateCustomGuitarItemRequest item : items) {
            aggregated.merge(item.getComponentId(), item.getQuantity(), Integer::sum);
        }
        return aggregated;
    }
}
