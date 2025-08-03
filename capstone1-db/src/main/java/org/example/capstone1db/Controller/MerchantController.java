package org.example.capstone1db.Controller;

import jakarta.validation.Valid;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.example.capstone1db.Api.ApiResponse;
import org.example.capstone1db.Model.Merchant;
import org.example.capstone1db.Service.MerchantService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/merchant")
public class MerchantController {

    private final MerchantService merchantService;


    @GetMapping
    public ResponseEntity<?> getMerchants(){
        return ResponseEntity.status(HttpStatus.OK).body(merchantService.getAllMerchant());
    }

    @PostMapping
    public ResponseEntity<?> addMerchant(@Valid @RequestBody Merchant merchant, Errors error){
        if (error.hasErrors()){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error.getFieldError().getDefaultMessage());
        }
        if (merchantService.addMerchant(merchant)) {
            return ResponseEntity.status(HttpStatus.OK).body(new ApiResponse("merchant add successfully"));
        }
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ApiResponse("merchant is duplicated"));
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateMerchant(@PathVariable Integer id,@Valid @RequestBody Merchant merchant, Errors error){
        if (error.hasErrors()){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error.getFieldError().getDefaultMessage());
        }

        if(merchantService.updateMerchant(id, merchant)){
            return ResponseEntity.status(HttpStatus.OK).body(new ApiResponse("merchant updated successfully"));
        }

        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ApiResponse("merchant not found"));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteMerchant(@PathVariable Integer id){
        if(merchantService.deleteMerchant(id)){
            return ResponseEntity.status(HttpStatus.OK).body(new ApiResponse("merchant deleted successfully"));
        }
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ApiResponse("merchant not found"));
    }

}
