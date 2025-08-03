package org.example.capstone1db.Service;

import lombok.RequiredArgsConstructor;
import org.example.capstone1db.Model.Category;
import org.example.capstone1db.Model.Product;
import org.example.capstone1db.Repository.ProductRepository;
import org.example.capstone1db.Service.CategoryService;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Service
public class ProductService {
    private final ProductRepository productRepository;
    private final CategoryService categoryService;
    private Map<Integer, ArrayList<Double>> productRateHistory = new LinkedHashMap<>();

    public List<Product> getAllProduct() {
        return productRepository.findAll();
    }

    public Integer addProduct(Product product) {
        boolean categoryFound = false;
        for (Category c : categoryService.getAllCategory()) {
            if (c.getId().equals(product.getCategoryID())) {
                categoryFound = true;
                break;
            }
        }

        if (!categoryFound) {
            return -1; // Category not found
        }

        List<Product> existingProducts = productRepository.findAll();
        for (Product p : existingProducts) {
            if (p.getName().equals(product.getName()) &&
                    p.getCategoryID().equals(product.getCategoryID())) {
                return -2; // Duplicated product
            }
        }
        product.setIsSeasonalProduct(false);
        product.setProductRate(0.0);
        product.setOffer(0.0);
        product.setDiscount20(false);
        productRepository.save(product);
        return 1; // Success
    }

    public Boolean updateProduct(Integer id, Product product) {
        Product oldProduct = productRepository.findById(id).orElse(null);
        if (oldProduct == null) {
            return false;
        }

        oldProduct.setProductRate(product.getProductRate());
        oldProduct.setIsSeasonalProduct(product.getIsSeasonalProduct());
        oldProduct.setProductRate(product.getProductRate());
        oldProduct.setName(product.getName());
        oldProduct.setOffer(product.getOffer());
        oldProduct.setPrice(product.getPrice());
        oldProduct.setCategoryID(product.getCategoryID());
        oldProduct.setDiscount20(product.getDiscount20());

        productRepository.save(oldProduct);
        return true;
    }

    public Boolean deleteProduct(Integer id) {
        Product product = productRepository.findById(id).orElse(null);
        if (product == null) {
            return false;
        }

        productRepository.delete(product);
        return true;
    }

    public List<Product> filterByRate(){
        if(productRepository.findAll().isEmpty()){
            return null;
        }

        return productRepository.findAll().stream()
                .sorted((p1, p2) -> Double.compare(p2.getProductRate(), p1.getProductRate()))
                .limit(10)
                .collect(Collectors.toCollection(ArrayList::new));
    }

    public Map<Integer, ArrayList<Double>> getProductRateHistory(){
        return productRateHistory;
    }


}
