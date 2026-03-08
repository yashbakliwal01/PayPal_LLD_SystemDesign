package com.paypal.controller;

import java.util.Map;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.paypal.dto.WalletDTO;
import com.paypal.entity.Wallet;
import com.paypal.repository.WalletRepository;

@RestController
@RequestMapping("/api/wallet")
public class WalletController {

	private static final Logger logger = LoggerFactory.getLogger(WalletController.class);
   
	@Autowired
    private WalletRepository walletRepository;

    @GetMapping("/{userId}")
    public ResponseEntity<?> getWalletByUserId(@PathVariable Long userId) {
        
    	logger.info("Fetching wallet for userId: {}", userId);
        
    	Optional<Wallet> walletOpt = walletRepository.findByUserId(userId);

        if (walletOpt.isEmpty()) {
        	logger.warn("Wallet not found for userId={}", userId);
            return ResponseEntity.status(404).body(
            		Map.of(
            				"status", "failed",
            				"message", "Wallet not found for user id: "+userId, 
            				"userId", userId
            				));
        }
        
        Wallet wallet = walletOpt.get();
        WalletDTO walletDTO = new WalletDTO(wallet.getId(), wallet.getUser().getId(), wallet.getBalance());
        
        logger.info("Wallet fetched Successfully | userId={} | balnce={}", userId, wallet.getBalance());
        
        return ResponseEntity.ok(walletDTO);
    }
}
