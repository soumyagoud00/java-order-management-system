package com.soumya.order_management_system.service;

import com.soumya.order_management_system.entity.Order;
import com.soumya.order_management_system.entity.OrderStatus;
import com.soumya.order_management_system.entity.Product;
import com.soumya.order_management_system.exception.InsufficientStockException;
import com.soumya.order_management_system.exception.InvalidOrderException;
import com.soumya.order_management_system.exception.ResourceNotFoundException;
import com.soumya.order_management_system.repository.OrderRepository;
import com.soumya.order_management_system.repository.ProductRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class OrderService {

    private final OrderRepository orderRepository;
    private final ProductRepository productRepository;

    public OrderService(
            OrderRepository orderRepository,
            ProductRepository productRepository) {

        this.orderRepository = orderRepository;
        this.productRepository = productRepository;
    }

    @Transactional
    public Order placeOrder(Order order) {

        Product product = productRepository
                .findById(order.getProductId())
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Product not found with ID: "
                                + order.getProductId()
                ));

        if (order.getQuantity() > product.getStockQuantity()) {
            throw new InsufficientStockException(
                    "Insufficient stock. Available quantity: "
                            + product.getStockQuantity()
            );
        }

        double totalAmount =
                product.getPrice() * order.getQuantity();

        order.setTotalAmount(totalAmount);
        order.setStatus(OrderStatus.CONFIRMED);
        order.setCreatedAt(LocalDateTime.now());

        int remainingStock =
                product.getStockQuantity() - order.getQuantity();

        product.setStockQuantity(remainingStock);
        productRepository.save(product);

        return orderRepository.save(order);
    }

    public List<Order> getAllOrders() {
        return orderRepository.findAll();
    }

    public Order getOrderById(Long id) {
        return orderRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Order not found with ID: " + id
                ));
    }

    @Transactional
    public Order cancelOrder(Long id) {

        Order order = getOrderById(id);

        if (order.getStatus() == OrderStatus.CANCELLED) {
            throw new InvalidOrderException(
                    "Order is already cancelled"
            );
        }

        Product product = productRepository
                .findById(order.getProductId())
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Product not found with ID: "
                                + order.getProductId()
                ));

        int restoredStock =
                product.getStockQuantity() + order.getQuantity();

        product.setStockQuantity(restoredStock);
        productRepository.save(product);

        order.setStatus(OrderStatus.CANCELLED);

        return orderRepository.save(order);
    }
}