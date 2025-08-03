package org.example.capstone1db.Service;

import lombok.RequiredArgsConstructor;
import org.example.capstone1db.Model.MerchantStock;
import org.example.capstone1db.Model.Merchant;
import org.example.capstone1db.Model.Product;
import org.example.capstone1db.Model.User;
import org.example.capstone1db.Repository.MerchantStockRepositroy;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
@RequiredArgsConstructor
public class MerchantStockService {

    private final MerchantStockRepositroy merchantStockRepository;
    private final UserService userService;
    private final ProductService productService;
    private final MerchantService merchantService;

    public List<MerchantStock> getAllMerchantStocks() {
        return merchantStockRepository.findAll();
    }

    public int addMerchantStock(MerchantStock stock) {

        for (MerchantStock ms : merchantStockRepository.findAll()) {
            if (ms.getProductId().equals(stock.getProductId()) && ms.getMerchantId().equals(stock.getMerchantId())) {
                return -3; // duplicate stock
            }
        }

        if (!merchantService.getAllMerchant().stream().anyMatch(m -> m.getId().equals(stock.getMerchantId()))) {
            return -1; // merchant not found
        }

        // Check product exists
        if (!productService.getAllProduct().stream().anyMatch(p -> p.getId().equals(stock.getProductId()))) {
            return -2; // product not found
        }

        stock.setSoldProducts(0);
        stock.setIsSeasonalProduct(false);
        stock.setMerchantRate(0.0);
        merchantStockRepository.save(stock);
        return 1; // success
    }

    public boolean updateMerchantStock(Integer id, MerchantStock updatedStock) {
        MerchantStock oldStock = merchantStockRepository.findById(id).orElse(null);
        if (oldStock == null) {
            return false;
        }

        // Keep old merchantRate and soldProducts like your ArrayList logic
        updatedStock.setMerchantRate(oldStock.getMerchantRate());
        updatedStock.setSoldProducts(oldStock.getSoldProducts());

        // Check duplicate productId + merchantId with different id
        for (MerchantStock ms : merchantStockRepository.findAll()) {
            if (!ms.getId().equals(id) &&
                    ms.getProductId().equals(updatedStock.getProductId()) &&
                    ms.getMerchantId().equals(updatedStock.getMerchantId())) {
                return false; // duplicate
            }
        }

        // Update fields
        oldStock.setProductId(updatedStock.getProductId());
        oldStock.setMerchantId(updatedStock.getMerchantId());
        oldStock.setStock(updatedStock.getStock());
        oldStock.setIsSeasonalProduct(updatedStock.getIsSeasonalProduct());
        oldStock.setSoldProducts(updatedStock.getSoldProducts());
        oldStock.setMerchantRate(updatedStock.getMerchantRate());

        merchantStockRepository.save(oldStock);
        return true;
    }

    public boolean deleteMerchantStock(Integer id) {
        MerchantStock stock = merchantStockRepository.findById(id).orElse(null);
        if (stock == null) {
            return false;
        }
        merchantStockRepository.delete(stock);
        return true;
    }


    public boolean addToStock(Integer productId, Integer merchantId, Integer amount) {
        for (MerchantStock m : merchantStockRepository.findAll()) {
            if (m.getProductId().equals(productId) && m.getMerchantId().equals(merchantId)) {
                m.setStock(m.getStock() + amount);
                merchantStockRepository.save(m);
                return true;
            }
        }
        return false;
    }


    // Buy directly with full checks like your old code
    public int buyDirectly(Integer userId, Integer productId, Integer merchantId) {
        for (MerchantStock m : merchantStockRepository.findAll()) {
            if (m.getMerchantId().equals(merchantId)) {
                for (User u : userService.getAllUsers()) {
                    if (u.getId().equals(userId)) {
                        for (Product p : productService.getAllProduct()) {
                            if (p.getId().equals(productId)) {
                                if (p.getPrice() <= u.getBalance() && p.getIsSeasonalProduct() && p.getDiscount20() && m.getStock() > 0) {
                                    double discount = p.getOffer() + 0.2;
                                    u.setBalance(u.getBalance() - (p.getPrice() - (p.getPrice() * discount)));
                                    m.setStock(m.getStock() - 1);
                                    m.setSoldProducts(m.getSoldProducts() + 1);

                                    userService.getUserHistory().computeIfAbsent(userId, k -> new ArrayList<>());
                                    if (!userService.getUserHistory().get(userId).contains(p.getId())) {
                                        userService.getUserHistory().get(userId).add(p.getId());
                                    }
                                    merchantStockRepository.save(m);
                                    return 1; // thank you for buying come again
                                } else if (p.getPrice() <= u.getBalance() && p.getIsSeasonalProduct() && m.getStock() > 0) {
                                    u.setBalance(u.getBalance() - (p.getPrice() - (p.getPrice() * p.getOffer())));
                                    m.setStock(m.getStock() - 1);
                                    m.setSoldProducts(m.getSoldProducts() + 1);

                                    userService.getUserHistory().computeIfAbsent(userId, k -> new ArrayList<>());
                                    if (!userService.getUserHistory().get(userId).contains(p.getId())) {
                                        userService.getUserHistory().get(userId).add(p.getId());
                                    }
                                    merchantStockRepository.save(m);
                                    return 1; // thank you for buying come again
                                } else if (p.getPrice() <= u.getBalance() && p.getDiscount20() && m.getStock() > 0) {
                                    u.setBalance(u.getBalance() - (p.getPrice() - (p.getPrice() * 0.2)));
                                    m.setStock(m.getStock() - 1);
                                    m.setSoldProducts(m.getSoldProducts() + 1);


                                    userService.getUserHistory().computeIfAbsent(userId, k -> new ArrayList<>());
                                    if (!userService.getUserHistory().get(userId).contains(p.getId())) {
                                        userService.getUserHistory().get(userId).add(p.getId());
                                    }
                                    merchantStockRepository.save(m);
                                    return 1; // thank you for buying come again
                                } else if (p.getPrice() <= u.getBalance()) {
                                    u.setBalance(u.getBalance() - p.getPrice());
                                    m.setStock(m.getStock() - 1);
                                    m.setSoldProducts(m.getSoldProducts() + 1);

                                    userService.getUserHistory().computeIfAbsent(userId, k -> new ArrayList<>());
                                    if (!userService.getUserHistory().get(userId).contains(p.getId())) {
                                        userService.getUserHistory().get(userId).add(p.getId());
                                    }
                                    merchantStockRepository.save(m);
                                    return 1; // thank you for buying come again
                                } else {
                                    return 2; // user balance less than product price
                                }
                            }
                        }
                        return 3; // product not found
                    }
                }
                return 4; // user not found
            }
        }
        return 5; // merchant not found
    }


    // Seasonal products endpoint logic
    public int seasonalProducts(Integer merchantId, Integer productId, Double discount) {
        if (discount >= 1) {
            discount = discount / 100;
        } else if (discount < 0) {
            return -1;
        }

        for (MerchantStock m : merchantStockRepository.findAll()) {
            if (m.getMerchantId().equals(merchantId)) {
                if (m.getProductId().equals(productId)) {
                    for (Product p : productService.getAllProduct()) {
                        if (p.getId().equals(productId)) {
                            p.setOffer(discount);
                            if (p.getIsSeasonalProduct() == null || m.getIsSeasonalProduct() == null){
                                p.setIsSeasonalProduct(false);
                                m.setIsSeasonalProduct(false);
                            }
                            p.setIsSeasonalProduct(!m.getIsSeasonalProduct());
                            m.setIsSeasonalProduct(!m.getIsSeasonalProduct());
                            merchantStockRepository.save(m);
                            productService.updateProduct(p.getId(),p);
                            return 1; // product now updated successfully: in seasonal offers
                        }
                    }
                    return 2; // product not found
                }
                return 2; // product not found (merchant has no such product stock)
            }
        }
        return 3; // merchant not found
    }


    // Merchants performance endpoint logic
    public Map<Integer, Object> merchantPerformance(Integer userId) {
        User foundUser = null;
        for (User u : userService.getAllUsers()) {
            if (u.getId().equals(userId)) {
                foundUser = u;
                break;
            }
        }

        if (foundUser == null) {
            return null;
        }

        Map<Integer, Double> merchantScoreSumMap = new HashMap<>();
        Map<Integer, Integer> merchantScoreCountMap = new HashMap<>();
        Map<Integer, Integer> merchantStockMap = new HashMap<>();

        // Get max sold for normalization
        int maxSold = 0;
        List<MerchantStock> allStocks = merchantStockRepository.findAll();
        for (MerchantStock stock : allStocks) {
            maxSold = Math.max(maxSold, stock.getSoldProducts());
        }
        if (maxSold == 0) maxSold = 1;

        // Map products to their rates
        Map<Integer, Double> productRateMap = new HashMap<>();
        for (Product p : productService.getAllProduct()) {
            productRateMap.put(p.getId(), p.getProductRate());
        }

        // Main loop
        for (MerchantStock stock : allStocks) {
            Integer merchantId = stock.getMerchantId();
            Double rate = productRateMap.get(stock.getProductId());

            // Always count stock
            merchantStockMap.put(
                    merchantId,
                    merchantStockMap.getOrDefault(merchantId, 0) + stock.getStock()
            );

            // Skip scoring if rate is null or 0
            if (rate == null || rate == 0.0) {
                continue;
            }

            stock.setMerchantRate(rate);
            updateMerchantStock(merchantId, stock); // optional

            int sold = stock.getSoldProducts();
            if (sold == 0) sold = 1;

            double normalizedSold = (double) sold / maxSold;
            double ratio = (rate * 0.7) + (normalizedSold * 0.3);

            merchantScoreSumMap.put(
                    merchantId,
                    merchantScoreSumMap.getOrDefault(merchantId, 0.0) + ratio
            );
            merchantScoreCountMap.put(
                    merchantId,
                    merchantScoreCountMap.getOrDefault(merchantId, 0) + 1
            );
        }

        // Collect all merchant IDs
        Set<Integer> allMerchantIds = new HashSet<>();
        allMerchantIds.addAll(merchantStockMap.keySet());
        allMerchantIds.addAll(merchantScoreSumMap.keySet());

        // Sort merchants by average score
        List<Integer> sortedMerchantIds = new ArrayList<>(allMerchantIds);
        sortedMerchantIds.sort((a, b) -> {
            double scoreA = merchantScoreSumMap.containsKey(a)
                    ? merchantScoreSumMap.get(a) / merchantScoreCountMap.get(a)
                    : 0.0;
            double scoreB = merchantScoreSumMap.containsKey(b)
                    ? merchantScoreSumMap.get(b) / merchantScoreCountMap.get(b)
                    : 0.0;
            return Double.compare(scoreB, scoreA);
        });

        // Final output
        Map<Integer, Object> output = new LinkedHashMap<>();
        for (Integer merchantId : sortedMerchantIds) {
            Map<String, Object> merchantInfo = new LinkedHashMap<>();

            if (merchantScoreSumMap.containsKey(merchantId)) {
                double total = merchantScoreSumMap.get(merchantId);
                int count = merchantScoreCountMap.get(merchantId);
                double avg = total / count;
                merchantInfo.put("Score", String.format("%.2f", avg));
            } else {
                merchantInfo.put("Score", 0.0);
            }

            int stock = merchantStockMap.getOrDefault(merchantId, 0);
            merchantInfo.put("Stock", stock);

            output.put(merchantId, merchantInfo);
        }

        return output;
    }



    public int toggleDiscount20(Integer merchantId, Integer productId) {
        for (MerchantStock m : merchantStockRepository.findAll()) {
            if (m.getMerchantId().equals(merchantId)) {
                if (m.getProductId().equals(productId)) {
                    for (Product p : productService.getAllProduct()) {
                        if (p.getId().equals(productId)) {
                            p.setDiscount20(!p.getDiscount20());
                            productService.updateProduct(p.getId(),p);
                            return 1; // discount updated
                        }
                    }
                    return -2; // product not found
                }
            }
        }
        return -1; // merchant not found
    }
}
