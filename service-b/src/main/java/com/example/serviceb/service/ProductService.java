package com.example.serviceb.service;

import com.example.serviceb.dto.CreateProductRequest;
import com.example.serviceb.dto.ProductDTO;
import com.example.serviceb.exception.ResourceNotFoundException;
import com.example.serviceb.model.Product;
import com.example.serviceb.repository.ProductRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ProductService {

    private final ProductRepository productRepo;

    public ProductService(ProductRepository productRepo) {
        this.productRepo = productRepo;
    }

    public List<ProductDTO> getAllProducts(int page, int size) {
        int offset = page * size;
        return productRepo.findAll(offset, size).stream().map(this::toDTO).toList();
    }

    public long countAll() {
        return productRepo.count();
    }

    public ProductDTO getProductById(Long id) {
        Product product = productRepo.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Product not found with id: " + id));
        return toDTO(product);
    }

    public ProductDTO createProduct(CreateProductRequest request) {
        Product product = new Product();
        product.setName(request.getName());
        product.setDescription(request.getDescription());
        product.setPrice(request.getPrice());
        product.setStockQuantity(request.getStockQuantity());
        return toDTO(productRepo.save(product));
    }

    public ProductDTO updateProduct(Long id, CreateProductRequest request) {
        Product product = productRepo.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Product not found with id: " + id));
        product.setName(request.getName());
        product.setDescription(request.getDescription());
        product.setPrice(request.getPrice());
        product.setStockQuantity(request.getStockQuantity());
        return toDTO(productRepo.save(product));
    }

    public void deleteProduct(Long id) {
        if (!productRepo.deleteById(id)) {
            throw new ResourceNotFoundException("Product not found with id: " + id);
        }
    }

    private ProductDTO toDTO(Product p) {
        return new ProductDTO(p.getId(), p.getName(), p.getDescription(), p.getPrice(), p.getStockQuantity());
    }
}
