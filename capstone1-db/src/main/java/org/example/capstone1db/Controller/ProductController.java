package org.example.capstone1db.Controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.example.capstone1db.Api.ApiResponse;
import org.example.capstone1db.Model.Product;
import org.example.capstone1db.Service.ProductService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/product")
public class ProductController {

    private final ProductService productService;

    @GetMapping
    public ResponseEntity<?> getProducts() {
        return ResponseEntity.status(HttpStatus.OK).body(productService.getAllProduct());
    }

    @PostMapping
    public ResponseEntity<?> addProduct(@Valid @RequestBody Product product, Errors errors) {
        if (errors.hasErrors()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errors.getFieldError().getDefaultMessage());
        }
        Integer msg = productService.addProduct(product);
        if (msg == -1) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ApiResponse("category not found"));
        }else if (msg == -2){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ApiResponse("product is duplicated"));
        }else {
            return ResponseEntity.status(HttpStatus.OK).body(new ApiResponse("product add successfully"));
        }

    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateProduct(@PathVariable Integer id, @Valid @RequestBody Product product, Errors errors) {
        if (errors.hasErrors()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errors.getFieldError().getDefaultMessage());
        }

        boolean updated = productService.updateProduct(id, product);
        if (updated) {
            return ResponseEntity.status(HttpStatus.OK).body(new ApiResponse("Product updated successfully"));
        }

        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ApiResponse("Product not found"));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteProduct(@PathVariable Integer id) {
        boolean deleted = productService.deleteProduct(id);
        if (deleted) {
            return ResponseEntity.status(HttpStatus.OK).body(new ApiResponse("Product deleted successfully"));
        }

        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ApiResponse("Product not found"));
    }


    //5 endpoints
    //1: filter top 10 products
    @GetMapping("/top")
    public ResponseEntity<?> filterByTop10Rate(){
        List<Product> filtered = productService.filterByRate();
        if(filtered == null){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ApiResponse("no products"));
        }else {
            return ResponseEntity.status(HttpStatus.OK).body(filtered);
        }
    }
}
