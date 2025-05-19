package com.paypal.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.paypal.entity.Wallet;
import com.paypal.entity.WalletDTO;
import com.paypal.repository.WalletRepository;

@RestController
@RequestMapping("/api/wallet")
public class WalletController {

    @Autowired
    private WalletRepository walletRepository;

    @GetMapping("/{userId}")
    public ResponseEntity<?> getWalletByUserId(@PathVariable Long userId) {
        Wallet wallet = walletRepository.findByUserId(userId);

        if (wallet == null) {
            return ResponseEntity.status(404).body("Wallet not found for user id: " + userId);
        }

        WalletDTO walletDTO = new WalletDTO(wallet.getId(), wallet.getId(), wallet.getBalance());
        return ResponseEntity.ok(walletDTO);
    }
}
