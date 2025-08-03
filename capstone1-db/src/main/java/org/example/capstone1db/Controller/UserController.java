package org.example.capstone1db.Controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.example.capstone1db.Api.ApiResponse;
import org.example.capstone1db.Model.Product;
import org.example.capstone1db.Model.User;
import org.example.capstone1db.Service.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/user")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;


    @GetMapping
    public ResponseEntity<?> getAll() {
        return ResponseEntity.status(HttpStatus.OK).body(userService.getAllUsers());
    }

    @PostMapping
    public ResponseEntity<?> addUser(@Valid @RequestBody User user, Errors errors) {
        if (errors.hasErrors()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errors.getFieldError().getDefaultMessage());
        }
        if (userService.addUser(user)) {
            return ResponseEntity.status(HttpStatus.OK).body(new ApiResponse("user added successfully"));
        }
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ApiResponse("user is duplicated"));

    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateUser(@PathVariable Integer id, @Valid @RequestBody User user, Errors error) {
        if (error.hasErrors()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error.getFieldError().getDefaultMessage());
        }

        if (userService.updateUser(id, user)) {
            return ResponseEntity.status(HttpStatus.OK).body(new ApiResponse("user updated successfully"));
        }

        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ApiResponse("user not found"));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteUser(@PathVariable Integer id) {
        if (userService.deleteUser(id)) {
            return ResponseEntity.status(HttpStatus.OK).body(new ApiResponse("user deleted successfully"));
        }
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ApiResponse("user not found"));
    }

    //5 endpoints
    //2: compare two products
    @GetMapping("/compare")
    public ResponseEntity<?> compareTwoProducts(@RequestParam("product1") Integer productId1, @RequestParam("product2") Integer productId2) {
        Map<Integer, Map<String, Boolean>> compared = userService.compareTwoProducts(productId1, productId2);
        if (compared == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ApiResponse("some products not found check the id again"));
        } else {
            return ResponseEntity.status(HttpStatus.OK).body(compared);
        }
    }

    //5 endpoints
    //3: discount for subscribed users
    @GetMapping("/{userId}/discount")
    public ResponseEntity<?> getDiscount(@PathVariable Integer userId) {
        List<Product> discount = userService.discount(userId);

        if (discount == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ApiResponse("user not found"));
        } else if (discount.isEmpty()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ApiResponse("no discounts"));
        } else {
            return ResponseEntity.status(HttpStatus.OK).body(discount);
        }
    }

    @PostMapping("/{userId}/product/{productId}/rate/{rate}")
    public ResponseEntity<?> rateProduct(@PathVariable Integer userId, @PathVariable Integer productId, @PathVariable double rate) {
        if (rate > 5 || rate < 0) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ApiResponse("rate must be 0 to 5"));
        }
        if (userService.rateProduct(productId, userId, rate)) {
            return ResponseEntity.status(HttpStatus.OK).body(new ApiResponse("thank you for rating product"));
        }
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ApiResponse("you did not buy this product"));
    }

    @PostMapping("/{userId}/subscribe")
    public ResponseEntity<?> makeSubscribe(@PathVariable Integer userId){
        if(userService.subscribe(userId)){
            return ResponseEntity.status(HttpStatus.OK).body(new ApiResponse("thank you for Subscribe"));
        }
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ApiResponse("user not have balance"));
    }



}
