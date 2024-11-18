package com.epam.jmp.multithreading;

import java.math.BigDecimal;
import java.util.*;
import java.util.concurrent.*;
import java.io.*;
import java.util.logging.*;

class Account {
    private final String accountId;
    private final Map<String, BigDecimal> currencies = new HashMap<>();

    public Account(String accountId) {
        this.accountId = accountId;
    }

    public synchronized void deposit(String currency, BigDecimal amount) {
        currencies.put(currency, currencies.getOrDefault(currency, BigDecimal.ZERO).add(amount));
    }

    public synchronized boolean withdraw(String currency, BigDecimal amount) {
        BigDecimal currentAmount = currencies.get(currency);
        if (currentAmount != null && currentAmount.compareTo(amount) >= 0) {
            currencies.put(currency, currentAmount.subtract(amount));
            return true;
        }
        return false;
    }

    public synchronized BigDecimal getBalance(String currency) {
        return currencies.getOrDefault(currency, BigDecimal.ZERO);
    }

    public String getAccountId() {
        return accountId;
    }
}

class CurrencyExchangeService {
    private final Map<String, BigDecimal> exchangeRates = new HashMap<>();
    private final ExecutorService executorService = Executors.newFixedThreadPool(4);
    private final Logger logger = Logger.getLogger(CurrencyExchangeService.class.getName());

    public void setExchangeRate(String fromCurrency, String toCurrency, BigDecimal rate) {
        exchangeRates.put(fromCurrency + "_" + toCurrency, rate);
    }

    public BigDecimal getExchangeRate(String fromCurrency, String toCurrency) {
        return exchangeRates.getOrDefault(fromCurrency + "_" + toCurrency, BigDecimal.ONE);
    }

    public synchronized void exchangeCurrency(Account account, String fromCurrency, String toCurrency, BigDecimal amount) throws Exception {
        if (account.withdraw(fromCurrency, amount)) {
            BigDecimal rate = getExchangeRate(fromCurrency, toCurrency);
            BigDecimal exchangedAmount = amount.multiply(rate);
            account.deposit(toCurrency, exchangedAmount);
            logger.info("Exchanged " + amount + " " + fromCurrency + " to " + exchangedAmount + " " + toCurrency);
        } else {
            throw new Exception("Insufficient funds");
        }
    }

    public void simulateMultipleExchanges(List<Account> accounts, String fromCurrency, String toCurrency, BigDecimal amount) {
        for (Account account : accounts) {
            executorService.submit(() -> {
                try {
                    exchangeCurrency(account, fromCurrency, toCurrency, amount);
                } catch (Exception e) {
                    logger.warning("Error while exchanging currency: " + e.getMessage());
                }
            });
        }
    }
}

public class CurrencyExchangeApp {
    public static void main(String[] args) throws InterruptedException {
        Logger logger = Logger.getLogger(CurrencyExchangeApp.class.getName());
        CurrencyExchangeService service = new CurrencyExchangeService();

        // Setup Exchange Rates
        service.setExchangeRate("USD", "EUR", new BigDecimal("0.85"));
        service.setExchangeRate("EUR", "USD", new BigDecimal("1.18"));

        // Create accounts
        Account account1 = new Account("user1");
        Account account2 = new Account("user2");
        account1.deposit("USD", new BigDecimal("1000"));
        account2.deposit("USD", new BigDecimal("500"));

        // List of accounts
        List<Account> accounts = Arrays.asList(account1, account2);

        // Simulate multiple exchanges
        service.simulateMultipleExchanges(accounts, "USD", "EUR", new BigDecimal("100"));

        // Wait for tasks to complete
        Thread.sleep(5000);

        // Print account balances after exchange
        System.out.println("Account 1 Balance (USD): " + account1.getBalance("USD"));
        System.out.println("Account 1 Balance (EUR): " + account1.getBalance("EUR"));
        System.out.println("Account 2 Balance (USD): " + account2.getBalance("USD"));
        System.out.println("Account 2 Balance (EUR): " + account2.getBalance("EUR"));
    }
}
