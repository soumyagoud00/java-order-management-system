package com.soumya.order_management_system.service;

import com.soumya.order_management_system.entity.Product;
import com.soumya.order_management_system.exception.ResourceNotFoundException;
import com.soumya.order_management_system.repository.ProductRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ProductServiceTest {

    @Mock
    private ProductRepository productRepository;

    private ProductService productService;

    @BeforeEach
    void setUp() {
        productService = new ProductService(productRepository);
    }

    @Test
    void shouldCreateProductWhenInputIsValid() {

        Product product = new Product(
                "Wireless Mouse",
                "Electronics",
                800,
                10
        );

        Product savedProduct = new Product(
                "Wireless Mouse",
                "Electronics",
                800,
                10
        );

        savedProduct.setId(1L);

        when(productRepository.save(product))
                .thenReturn(savedProduct);

        Product result = productService.createProduct(product);

        assertNotNull(result);
        assertEquals(1L, result.getId());
        assertEquals("Wireless Mouse", result.getName());
        assertEquals(800, result.getPrice());

        verify(productRepository).save(product);
    }

    @Test
    void shouldThrowExceptionWhenProductDoesNotExist() {

        when(productRepository.findById(99L))
                .thenReturn(Optional.empty());

        ResourceNotFoundException exception =
                assertThrows(
                        ResourceNotFoundException.class,
                        () -> productService.getProductById(99L)
                );

        assertEquals(
                "Product not found with ID: 99",
                exception.getMessage()
        );

        verify(productRepository).findById(99L);
    }
    @Test
    void shouldReturnAllProducts() {

        Product mouse = new Product(
                "Wireless Mouse",
                "Electronics",
                800,
                10
        );

        Product keyboard = new Product(
                "USB Keyboard",
                "Electronics",
                1000,
                5
        );

        when(productRepository.findAll())
                .thenReturn(List.of(mouse, keyboard));

        List<Product> products = productService.getAllProducts();

        assertEquals(2, products.size());
        assertEquals("Wireless Mouse", products.get(0).getName());
        assertEquals("USB Keyboard", products.get(1).getName());

        verify(productRepository).findAll();
    }

    @Test
    void shouldDeleteExistingProduct() {

        Product product = new Product(
                "Wireless Mouse",
                "Electronics",
                800,
                10
        );

        product.setId(1L);

        when(productRepository.findById(1L))
                .thenReturn(Optional.of(product));

        productService.deleteProduct(1L);

        verify(productRepository).findById(1L);
        verify(productRepository).delete(product);
    }
}