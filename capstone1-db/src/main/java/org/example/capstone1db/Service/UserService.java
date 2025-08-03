package org.example.capstone1db.Service;

import lombok.RequiredArgsConstructor;
import org.example.capstone1db.Model.Product;
import org.example.capstone1db.Model.User;
import org.example.capstone1db.Repository.UserRepository;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final ProductService productService;

    private Map<Integer, List<Integer>> userHistory = new HashMap<>();

    public List<User> getAllUsers(){
        return userRepository.findAll();
    }

    public Boolean addUser(User user){
        boolean exists = userRepository.findAll().stream()
                .anyMatch(u -> u.getUsername().equals(user.getUsername()) || u.getEmail().equals(user.getEmail()));
        if (exists) {
            return false;
        }
        userRepository.save(user);
        return true;
    }

    public Boolean updateUser(Integer id, User user){
        Optional<User> optionalOldUser = userRepository.findById(id);
        if (optionalOldUser.isEmpty()){
            return false;
        }

        User oldUser = optionalOldUser.get();

        oldUser.setBalance(user.getBalance());
        oldUser.setEmail(user.getEmail());
        oldUser.setRole(user.getRole());
        oldUser.setUsername(user.getUsername());
        oldUser.setPassword(user.getPassword());

        userRepository.save(oldUser);
        return true;
    }

    public Boolean deleteUser(Integer id){
        Optional<User> optionalUser = userRepository.findById(id);
        if (optionalUser.isEmpty()){
            return false;
        }
        userRepository.delete(optionalUser.get());
        return true;
    }


    public void addProductToUserHistory(Integer userId, Integer productId) {
        userHistory.putIfAbsent(userId, new ArrayList<>());
        List<Integer> products = userHistory.get(userId);
        if (!products.contains(productId)) {
            products.add(productId);
        }
    }

    //5 endpoints
    //3.discount on subscribed users only
    public ArrayList<Product> discount(Integer userId){
        ArrayList<Product> discount = new ArrayList<>();
        int userIndex = -1;
        for (int i = 0; i < userRepository.findAll().size(); i++) {
            if(userRepository.findAll().get(i).getId().equals(userId)){
                userIndex = i;
            }
        }
        if(userIndex == -1){
            return null;
        }

        for(Product p : productService.getAllProduct()){
            if(p.getDiscount20() && userRepository.findAll().get(userIndex).getSubscribed()){
                double discountedPrice = p.getPrice() - (p.getPrice() * 0.2);
                Product discountedProduct = new Product(p.getId(),p.getName(),discountedPrice,p.getCategoryID(), p.getProductRate(), p.getDiscount20(), p.getIsSeasonalProduct(), p.getOffer());
                discount.add(discountedProduct);
            }
        }
        return discount;
    }



    public boolean rateProduct(Integer productId, Integer userId , double rating){
        Map<Integer, ArrayList<Double>> productRatings = productService.getProductRateHistory();
        // 1. Check if the user has a history
        if (!userHistory.containsKey(userId)) {
            System.out.println("User not found.");
            return false;
        }

        // 2. Check if the user has this product in history
        if (!userHistory.get(userId).contains(productId)) {
            return false;
        }

        productRatings.putIfAbsent(productId, new ArrayList<>());
        productRatings.get(productId).add(rating);
        double total = 0;
        for (Double r : productRatings.get(productId)){
            total+=r;
        }
        total = total/productRatings.get(productId).size();

        for (Product p : productService.getAllProduct()){
            if (p.getId().equals(productId)){
                p.setProductRate(total);
                productService.updateProduct(p.getId(),p);
            }
        }


        userHistory.get(userId).remove(productId);

        return true;
    }

    public boolean subscribe(Integer userId){
        for (User u : userRepository.findAll()){
            if(u.getId().equals(userId)){
                if(u.getBalance() >= 60){
                    u.setSubscribed(true);
                    u.setBalance(u.getBalance()-60);
                    updateUser(u.getId(),u);
                    return true;
                }
            }
        }
        return false;
    }

    public Map<Integer, Map<String, Boolean>> compareTwoProducts(Integer productId1, Integer productId2) {
        Product product1 = null;
        Product product2 = null;
        for (Product p : productService.getAllProduct()) {
            if (p.getId().equals(productId1)) {
                product1 = p;
            }
            if (p.getId().equals(productId2)) {
                product2 = p;
            }
            if (product1 != null && product2 != null) {
                break;
            }
        }
        if (product1 == null || product2 == null) {
            return null;
        }
        Map<String, Boolean> comparison1 = new LinkedHashMap<>();
        Map<String, Boolean> comparison2 = new LinkedHashMap<>();
        if (product1.getPrice() < product2.getPrice()) {
            comparison1.put("price", true);
            comparison2.put("price", false);
        } else if (product1.getPrice() > product2.getPrice()) {
            comparison1.put("price", false);
            comparison2.put("price", true);
        } else {
            comparison1.put("price", true);
            comparison2.put("price", true);
        }
        if (product1.getProductRate() > product2.getProductRate()) {
            comparison1.put("rate", true);
            comparison2.put("rate", false);
        } else if (product1.getProductRate() < product2.getProductRate()) {
            comparison1.put("rate", false);
            comparison2.put("rate", true);
        } else {
            comparison1.put("rate", true);
            comparison2.put("rate", true);
        }
        Map<Integer, Map<String, Boolean>> result = new LinkedHashMap<>();
        result.put(product1.getId(), comparison1);
        result.put(product2.getId(), comparison2);
        return result;
    }




    public Map<Integer, List<Integer>> getUserHistory(){
        return userHistory;
    }
}
