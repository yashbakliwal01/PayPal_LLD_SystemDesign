package com.paypal.util;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Random;

public class TransactionIdGenerator {

    private static final Random RANDOM = new Random();

    public static String generateTransactionRef() {

        String datePart = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss"));
        int randomPart = 100000 + RANDOM.nextInt(900000);
        return "TXN-" + datePart + "-" + randomPart;
    }
}