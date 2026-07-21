package com.soumya.order_management_system.controller;

import com.soumya.order_management_system.entity.Product;
import com.soumya.order_management_system.service.ProductService;
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

@WebMvcTest(ProductController.class)
class ProductControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private ProductService productService;

    @Test
    void shouldReturn201WhenProductIsCreated() throws Exception {

        Product savedProduct = new Product(
                "Wireless Mouse",
                "Electronics",
                800,
                10
        );

        savedProduct.setId(1L);

        when(productService.createProduct(any(Product.class)))
                .thenReturn(savedProduct);

        mockMvc.perform(
                        post("/api/products")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content("""
                                        {
                                          "name": "Wireless Mouse",
                                          "category": "Electronics",
                                          "price": 800,
                                          "stockQuantity": 10
                                        }
                                        """)
                )
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name")
                        .value("Wireless Mouse"))
                .andExpect(jsonPath("$.price").value(800))
                .andExpect(jsonPath("$.stockQuantity").value(10));
    }

    @Test
    void shouldReturn400ForInvalidProduct() throws Exception {

        mockMvc.perform(
                        post("/api/products")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content("""
                                        {
                                          "name": "",
                                          "category": "Electronics",
                                          "price": -500,
                                          "stockQuantity": -2
                                        }
                                        """)
                )
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.name")
                        .value("Product name is required"))
                .andExpect(jsonPath("$.price")
                        .value("Price must be greater than zero"))
                .andExpect(jsonPath("$.stockQuantity")
                        .value("Stock quantity cannot be negative"));
    }
}