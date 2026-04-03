package com.example.guitarfactorysystem.service;

import com.example.guitarfactorysystem.dto.CreateWorkOrderRequest;
import com.example.guitarfactorysystem.dto.CreateWorkOrderResponse;
import com.example.guitarfactorysystem.dto.UpdateWorkOrderStatusRequest;
import com.example.guitarfactorysystem.dto.WorkOrderResponse;
import com.example.guitarfactorysystem.entity.CustomGuitarOrder;
import com.example.guitarfactorysystem.entity.CustomGuitarOrderItem;
import com.example.guitarfactorysystem.entity.WorkOrder;
import com.example.guitarfactorysystem.enums.OrderStatus;
import com.example.guitarfactorysystem.enums.WorkOrderStatus;
import com.example.guitarfactorysystem.exception.BusinessValidationException;
import com.example.guitarfactorysystem.exception.ResourceNotFoundException;
import com.example.guitarfactorysystem.repository.WorkOrderRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class WorkOrderService {

    private final WorkOrderRepository repository;
    private final CustomGuitarService customGuitarService;

    public WorkOrderService(WorkOrderRepository repository, CustomGuitarService customGuitarService) {
        this.repository = repository;
        this.customGuitarService = customGuitarService;
    }

    @Transactional
    public CreateWorkOrderResponse create(CreateWorkOrderRequest request) {
        CustomGuitarOrder order = customGuitarService.findOrThrow(request.getCustomGuitarOrderId());
        if (repository.existsByCustomGuitarOrder_Id(order.getId())) {
            throw new BusinessValidationException("Work order already exists for custom guitar order " + order.getId());
        }
        if (order.getStatus() == OrderStatus.CANCELLED) {
            throw new BusinessValidationException("Cannot create work order for a cancelled custom guitar order");
        }

        WorkOrder workOrder = new WorkOrder();
        workOrder.setCustomGuitarOrder(order);
        workOrder.setStatus(WorkOrderStatus.OPEN);
        workOrder.setCreatedAt(LocalDateTime.now());
        workOrder.setWorkOrderNumber("PENDING");

        WorkOrder saved = repository.save(workOrder);
        saved.setWorkOrderNumber(String.format("WO-%05d", saved.getId()));
        return toCreateResponse(saved);
    }

    @Transactional(readOnly = true)
    public List<WorkOrderResponse> list() {
        return repository.findAllByOrderByCreatedAtDescIdDesc()
                .stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public WorkOrderResponse get(Long id) {
        return toResponse(findOrThrow(id));
    }

    @Transactional
    public WorkOrderResponse updateStatus(Long id, UpdateWorkOrderStatusRequest request) {
        WorkOrder workOrder = findOrThrow(id);
        WorkOrderStatus currentStatus = workOrder.getStatus();
        WorkOrderStatus newStatus = request.getStatus();

        if ((currentStatus == WorkOrderStatus.CANCELLED || currentStatus == WorkOrderStatus.FINISHED) && currentStatus != newStatus) {
            throw new BusinessValidationException("Finished or cancelled work order cannot change status");
        }

        workOrder.setStatus(newStatus);
        syncOrderStatus(workOrder, currentStatus, newStatus);
        return toResponse(workOrder);
    }

    @Transactional(readOnly = true)
    public WorkOrder findOrThrow(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Work order not found for id " + id));
    }

    private void syncOrderStatus(WorkOrder workOrder, WorkOrderStatus currentStatus, WorkOrderStatus newStatus) {
        CustomGuitarOrder order = workOrder.getCustomGuitarOrder();

        if (newStatus == WorkOrderStatus.OPEN) {
            order.setStatus(OrderStatus.CREATED);
            return;
        }

        if (newStatus == WorkOrderStatus.IN_PROGRESS) {
            order.setStatus(OrderStatus.IN_PRODUCTION);
            return;
        }

        if (newStatus == WorkOrderStatus.FINISHED) {
            order.setStatus(OrderStatus.DONE);
            return;
        }

        if (newStatus == WorkOrderStatus.CANCELLED) {
            if (currentStatus != WorkOrderStatus.CANCELLED) {
                restoreInventory(order);
            }
            order.setStatus(OrderStatus.CANCELLED);
        }
    }

    private void restoreInventory(CustomGuitarOrder order) {
        for (CustomGuitarOrderItem item : order.getItems()) {
            item.getComponent().setQuantityInStock(item.getComponent().getQuantityInStock() + item.getQuantity());
        }
    }

    private CreateWorkOrderResponse toCreateResponse(WorkOrder workOrder) {
        CreateWorkOrderResponse response = new CreateWorkOrderResponse();
        response.setId(workOrder.getId());
        response.setCustomGuitarOrderId(workOrder.getCustomGuitarOrder().getId());
        response.setWorkOrderNumber(workOrder.getWorkOrderNumber());
        response.setStatus(workOrder.getStatus());
        response.setCreatedAt(workOrder.getCreatedAt());
        return response;
    }

    private WorkOrderResponse toResponse(WorkOrder workOrder) {
        WorkOrderResponse response = new WorkOrderResponse();
        response.setId(workOrder.getId());
        response.setWorkOrderNumber(workOrder.getWorkOrderNumber());
        response.setStatus(workOrder.getStatus());
        response.setCreatedAt(workOrder.getCreatedAt());
        response.setCustomGuitarOrderId(workOrder.getCustomGuitarOrder().getId());
        response.setCustomerName(workOrder.getCustomGuitarOrder().getCustomerName());
        response.setGuitarModelName(workOrder.getCustomGuitarOrder().getGuitarModel().getName());
        return response;
    }
}
