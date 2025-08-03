package org.example.capstone1db.Service;

import lombok.RequiredArgsConstructor;
import org.example.capstone1db.Model.Merchant;
import org.example.capstone1db.Repository.MerchantRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class MerchantService {

    private final MerchantRepository merchantRepository;

    public List<Merchant> getAllMerchant() {
        return merchantRepository.findAll();
    }

    public Boolean addMerchant(Merchant merchant) {
        List<Merchant> merchants = merchantRepository.findAll();
        for (Merchant m : merchants) {
            if (m.getName().equalsIgnoreCase(merchant.getName())) {
                return false; // duplicated name
            }
        }

        merchantRepository.save(merchant);
        return true;
    }

    public Boolean updateMerchant(Integer id, Merchant merchant) {
        Merchant oldMerchant = merchantRepository.findById(id).orElse(null);
        if (oldMerchant == null) {
            return false;
        }

        List<Merchant> merchants = merchantRepository.findAll();
        for (Merchant m : merchants) {
            if (!m.getId().equals(id) && m.getName().equalsIgnoreCase(merchant.getName())) {
                return false; // duplicate name with another merchant
            }
        }

        oldMerchant.setName(merchant.getName());
        merchantRepository.save(oldMerchant);
        return true;
    }

    public Boolean deleteMerchant(Integer id) {
        Merchant merchant = merchantRepository.findById(id).orElse(null);
        if (merchant == null) {
            return false;
        }

        merchantRepository.delete(merchant);
        return true;
    }
}
