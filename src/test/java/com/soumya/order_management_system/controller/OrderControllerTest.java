package com.soumya.order_management_system.controller;

import com.soumya.order_management_system.entity.Order;
import com.soumya.order_management_system.entity.OrderStatus;
import com.soumya.order_management_system.service.OrderService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(OrderController.class)
class OrderControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private OrderService orderService;

    @Test
    void shouldReturn201WhenOrderIsPlaced() throws Exception {

        Order savedOrder = new Order();
        savedOrder.setId(1L);
        savedOrder.setCustomerName("Soumya");
        savedOrder.setCustomerEmail("soumya@gmail.com");
        savedOrder.setProductId(1L);
        savedOrder.setQuantity(2);
        savedOrder.setTotalAmount(1600);
        savedOrder.setStatus(OrderStatus.CONFIRMED);

        when(orderService.placeOrder(any(Order.class)))
                .thenReturn(savedOrder);

        mockMvc.perform(
                        post("/api/orders")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content("""
                                        {
                                          "customerName": "Soumya",
                                          "customerEmail": "soumya@gmail.com",
                                          "productId": 1,
                                          "quantity": 2
                                        }
                                        """)
                )
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.customerName")
                        .value("Soumya"))
                .andExpect(jsonPath("$.totalAmount")
                        .value(1600))
                .andExpect(jsonPath("$.status")
                        .value("CONFIRMED"));
    }

    @Test
    void shouldReturn400ForInvalidOrder() throws Exception {

        mockMvc.perform(
                        post("/api/orders")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content("""
                                        {
                                          "customerName": "",
                                          "customerEmail": "wrong-email",
                                          "productId": 1,
                                          "quantity": 0
                                        }
                                        """)
                )
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.customerName")
                        .value("Customer name is required"))
                .andExpect(jsonPath("$.customerEmail")
                        .value("Enter a valid email address"))
                .andExpect(jsonPath("$.quantity")
                        .value("Quantity must be at least 1"));
    }
}