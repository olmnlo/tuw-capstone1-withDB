package org.example.capstone1db.Controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.example.capstone1db.Api.ApiResponse;
import org.example.capstone1db.Model.MerchantStock;
import org.example.capstone1db.Service.MerchantStockService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/merchant-stock")
public class MerchantStockController {

    private final MerchantStockService merchantStockService;

    @GetMapping
    public ResponseEntity<?> getMerchantStocks() {
        return ResponseEntity.status(HttpStatus.OK).body(merchantStockService.getAllMerchantStocks());
    }

    @PostMapping
    public ResponseEntity<?> addMerchantStock(@Valid @RequestBody MerchantStock merchantStock, Errors errors) {
        if (errors.hasErrors()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errors.getFieldError().getDefaultMessage());
        }
        int msg = merchantStockService.addMerchantStock(merchantStock);
        if (msg == -1) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ApiResponse("merchant not found"));
        }
        if (msg == -2){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ApiResponse("product not found"));
        }
        if(msg == -3){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ApiResponse("stock is duplicated"));
        }
        return ResponseEntity.status(HttpStatus.OK).body(new ApiResponse("merchant stock added successfully"));

    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateMerchantStock(@PathVariable Integer id, @Valid @RequestBody MerchantStock merchantStock, Errors errors) {
        if (errors.hasErrors()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errors.getFieldError().getDefaultMessage());
        }

        boolean updated = merchantStockService.updateMerchantStock(id, merchantStock);
        if (updated) {
            return ResponseEntity.status(HttpStatus.OK).body(new ApiResponse("Merchant stock updated successfully"));
        }

        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ApiResponse("Merchant stock not found"));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteMerchantStock(@PathVariable Integer id) {
        boolean deleted = merchantStockService.deleteMerchantStock(id);
        if (deleted) {
            return ResponseEntity.status(HttpStatus.OK).body(new ApiResponse("Merchant stock deleted successfully"));
        }

        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ApiResponse("Merchant stock not found"));
    }



    @PostMapping("/buy/user/{userId}/product/{productId}/merchant/{merchantId}")
    public ResponseEntity<?> buyDirectlyFromStock(@PathVariable Integer userId, @PathVariable Integer productId, @PathVariable Integer merchantId) {
        int msg = merchantStockService.buyDirectly(userId, productId, merchantId);
        switch (msg) {
            case 1:
                return ResponseEntity.status(HttpStatus.OK).body(new ApiResponse("thank you for buying come again"));
            case 2:
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ApiResponse("user balance less than product price"));
            case 3:
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ApiResponse("product not found"));
            case 4:
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ApiResponse("user not found"));
            default:
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ApiResponse("merchant not found"));

        }

    }

    @PostMapping("/{merchantId}/product/{productId}/toggle-bigdeal/{discount}")
    public ResponseEntity<?> toggleSeasonalProduct(@PathVariable Integer productId, @PathVariable Integer merchantId, @PathVariable Double discount) {
        int msg = merchantStockService.seasonalProducts(merchantId, productId, discount);
        if (msg == -1) {
            return ResponseEntity.status(HttpStatus.OK).body(new ApiResponse("discount not applicable check discount value"));
        } else if (msg == 1) {
            return ResponseEntity.status(HttpStatus.OK).body(new ApiResponse("product now updated successfully: it is in seasonal product offers now"));
        } else if (msg == 2) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ApiResponse("product not found"));
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ApiResponse("merchant not found"));
        }
    }

    @GetMapping("/performance/user/{userId}/quality")
    public ResponseEntity<?> merchantPerformance(@PathVariable Integer userId){
        Map<Integer, Object> merchantPerform = merchantStockService.merchantPerformance(userId);
        if (merchantPerform == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("user not found");
        }

        return ResponseEntity.status(HttpStatus.OK).body(merchantPerform);
    }


    @PutMapping("/{merchantId}/product/{productId}/subscribed-discount")
    public ResponseEntity<?> toggleDiscount20(@PathVariable Integer merchantId, @PathVariable Integer productId){
        int msg = merchantStockService.toggleDiscount20(merchantId,productId);

        if(msg == -1 ){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("merchant not found");
        }else if (msg == -2){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("product not found");
        }
        return ResponseEntity.status(HttpStatus.OK).body(new ApiResponse("discount updated"));
    }


}
