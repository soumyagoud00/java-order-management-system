package com.soumya.order_management_system.service;

import com.soumya.order_management_system.entity.Order;
import com.soumya.order_management_system.entity.OrderStatus;
import com.soumya.order_management_system.entity.Product;
import com.soumya.order_management_system.exception.InsufficientStockException;
import com.soumya.order_management_system.repository.OrderRepository;
import com.soumya.order_management_system.repository.ProductRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;
import com.soumya.order_management_system.exception.InvalidOrderException;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class OrderServiceTest {

    @Mock
    private OrderRepository orderRepository;

    @Mock
    private ProductRepository productRepository;

    private OrderService orderService;

    @BeforeEach
    void setUp() {
        orderService = new OrderService(
                orderRepository,
                productRepository
        );
    }

    @Test
    void shouldPlaceOrderWhenStockIsAvailable() {

        Product product = new Product(
                "Wireless Mouse",
                "Electronics",
                800,
                10
        );

        product.setId(1L);

        Order order = new Order();
        order.setCustomerName("Soumya");
        order.setCustomerEmail("soumya@gmail.com");
        order.setProductId(1L);
        order.setQuantity(2);

        when(productRepository.findById(1L))
                .thenReturn(Optional.of(product));

        when(productRepository.save(product))
                .thenReturn(product);

        when(orderRepository.save(order))
                .thenAnswer(invocation -> {
                    Order savedOrder = invocation.getArgument(0);
                    savedOrder.setId(1L);
                    return savedOrder;
                });

        Order result = orderService.placeOrder(order);

        assertNotNull(result);
        assertEquals(1L, result.getId());
        assertEquals(1600.0, result.getTotalAmount(), 0.001);
        assertEquals(OrderStatus.CONFIRMED, result.getStatus());
        assertNotNull(result.getCreatedAt());

        assertEquals(8, product.getStockQuantity());

        verify(productRepository).findById(1L);
        verify(productRepository).save(product);
        verify(orderRepository).save(order);
    }

    @Test
    void shouldRejectOrderWhenStockIsInsufficient() {

        Product product = new Product(
                "Wireless Mouse",
                "Electronics",
                800,
                3
        );

        product.setId(1L);

        Order order = new Order();
        order.setCustomerName("Soumya");
        order.setCustomerEmail("soumya@gmail.com");
        order.setProductId(1L);
        order.setQuantity(5);

        when(productRepository.findById(1L))
                .thenReturn(Optional.of(product));

        InsufficientStockException exception =
                assertThrows(
                        InsufficientStockException.class,
                        () -> orderService.placeOrder(order)
                );

        assertEquals(
                "Insufficient stock. Available quantity: 3",
                exception.getMessage()
        );

        assertEquals(3, product.getStockQuantity());

        verify(productRepository).findById(1L);
        verify(productRepository, never()).save(any(Product.class));
        verify(orderRepository, never()).save(any(Order.class));
    }
    @Test
    void shouldCancelOrderAndRestoreStock() {

        Product product = new Product(
                "Wireless Mouse",
                "Electronics",
                800,
                8
        );
        product.setId(1L);

        Order order = new Order();
        order.setId(1L);
        order.setProductId(1L);
        order.setQuantity(2);
        order.setStatus(OrderStatus.CONFIRMED);

        when(orderRepository.findById(1L))
                .thenReturn(Optional.of(order));

        when(productRepository.findById(1L))
                .thenReturn(Optional.of(product));

        when(productRepository.save(product))
                .thenReturn(product);

        when(orderRepository.save(order))
                .thenReturn(order);

        Order result = orderService.cancelOrder(1L);

        assertEquals(OrderStatus.CANCELLED, result.getStatus());
        assertEquals(10, product.getStockQuantity());

        verify(orderRepository).findById(1L);
        verify(productRepository).findById(1L);
        verify(productRepository).save(product);
        verify(orderRepository).save(order);
    }

    @Test
    void shouldRejectAlreadyCancelledOrder() {

        Order order = new Order();
        order.setId(1L);
        order.setProductId(1L);
        order.setQuantity(2);
        order.setStatus(OrderStatus.CANCELLED);

        when(orderRepository.findById(1L))
                .thenReturn(Optional.of(order));

        InvalidOrderException exception =
                assertThrows(
                        InvalidOrderException.class,
                        () -> orderService.cancelOrder(1L)
                );

        assertEquals(
                "Order is already cancelled",
                exception.getMessage()
        );

        verify(orderRepository).findById(1L);
        verify(productRepository, never()).findById(anyLong());
        verify(productRepository, never()).save(any(Product.class));
        verify(orderRepository, never()).save(any(Order.class));
    }
}