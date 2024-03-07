package org.example.service.impl;

import org.example.currency.CurrencyType;
import org.example.data.entity.Account;
import org.example.data.entity.User;
import org.example.exeptions.CustomException;
import org.example.repository.AccountRepository;
import org.example.repository.ExchangeRateRepository;

import java.util.List;
import java.util.Optional;

import static org.example.currency.CurrencyType.UAH;

public class TransactionService {
    private final AccountRepository accountRepository;
    private final ExchangeRateRepository exchangeRateRepository;

    public TransactionService(AccountRepository accountRepository,
                              ExchangeRateRepository exchangeRateRepository) {
        this.accountRepository = accountRepository;
        this.exchangeRateRepository = exchangeRateRepository;
    }

    public void modifyUserAccountBalance(Optional<User> user, CurrencyType currency, double amount) {
        Account account = accountRepository.findByUserAndCurrency(user, currency)
                .orElseThrow(() -> new CustomException("Не знайдено обліковий запис для користувача та валюти"));

        double newBalance = account.getBalance() + amount;
        if (newBalance < 0) {
            throw new CustomException("Недостатньо коштів на рахунку");
        }
        account.setBalance(newBalance);
        accountRepository.update(account);
    }

    public void transferMoney(Optional<User> sourceUser, Optional<User> targetUser, double amount, CurrencyType currency) {
        Account sourceAccount = accountRepository.findByUserAndCurrency(sourceUser, currency)
                .orElseThrow(() -> new CustomException("Вихідний рахунок не знайдено для користувача та валюти"));

        Account targetAccount = accountRepository.findByUserAndCurrency(targetUser, currency)
                .orElseThrow(() -> new CustomException("Цільовий рахунок не знайдено для користувача та валюти"));

        if (sourceAccount.getBalance() < amount) {
            throw new CustomException("Недостатньо коштів на вихідному рахунку");
        }

        targetAccount.setBalance(targetAccount.getBalance() + amount);
        sourceAccount.setBalance(sourceAccount.getBalance() - amount);

        accountRepository.update(sourceAccount);
        accountRepository.update(targetAccount);
    }

    public void convertCurrency(Optional<User> user, double amount, CurrencyType sourceCurrency, CurrencyType targetCurrency) {

        Account targetAccount = accountRepository.findByUserAndCurrency(user, targetCurrency)
                .orElseThrow(() -> new CustomException("Цільовий рахунок не знайдено для користувача та валюти"));

        Account sourceAccount = accountRepository.findByUserAndCurrency(user, sourceCurrency)
                .orElseThrow(() -> new CustomException("Не знайдено обліковий запис для користувача та вихідної валюти"));
        if (sourceAccount.getBalance() < amount) {
            throw new CustomException("Недостатньо коштів на рахунку для конвертації валюти");
        }
        double exchangeRate = exchangeRateRepository.getExchangeRate(sourceCurrency, targetCurrency);
        double convertedAmount = amount * exchangeRate;

        targetAccount.setBalance(targetAccount.getBalance() + convertedAmount);
        sourceAccount.setBalance(sourceAccount.getBalance() - amount);
        accountRepository.update(sourceAccount);
        accountRepository.update(targetAccount);
    }

    public double getAccountBalanceInUah(Optional<User> user) {
        List<Account> accounts = accountRepository.findByUser(user);
        if (accounts != null) {
            return accounts.stream()
                    .mapToDouble(account -> {
                        double exchangeRate = UAH.equals(account.getCurrency()) ? 1 : exchangeRateRepository.getExchangeRate(account.getCurrency(), UAH);
                        return account.getBalance() * exchangeRate;
                    })
                    .sum();
        } else {
            return 0.0;
        }
    }
}