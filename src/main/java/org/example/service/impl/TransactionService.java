package org.example.service.impl;

import org.example.currency.CurrencyType;
import org.example.data.entity.Account;
import org.example.data.entity.ExchangeRate;
import org.example.data.entity.Transaction;
import org.example.data.entity.User;
import org.example.repository.AccountRepository;
import org.example.repository.ExchangeRateRepository;
import org.example.repository.TransactionRepository;
import org.example.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.List;

public class TransactionService {



    private final UserRepository userRepository;
    private final AccountRepository accountRepository;
    private final ExchangeRateRepository exchangeRateRepository;
    private final TransactionRepository transactionRepository;

    private final CurrencyServiceImplPB currencyRate = new CurrencyServiceImplPB();

    public TransactionService(UserRepository userRepository, AccountRepository accountRepository,
                              ExchangeRateRepository exchangeRateRepository, TransactionRepository transactionRepository) {
        this.userRepository = userRepository;
        this.accountRepository = accountRepository;
        this.exchangeRateRepository = exchangeRateRepository;
        this.transactionRepository = transactionRepository;
    }

    public void increaseUserAccountBalance(User user, CurrencyType currency, double amount) {
        Account account = accountRepository.findByUserAndCurrency(user, currency)
                .orElseThrow(() -> new IllegalArgumentException("Не знайдено обліковий запис для користувача та валюти"));

        account.setBalance(account.getBalance() + amount);
        accountRepository.update(account);
    }

    public void reduceUserAccountBalance(User user, CurrencyType currency, double amount) {
        Account account = accountRepository.findByUserAndCurrency(user, currency)
                .orElseThrow(() -> new IllegalArgumentException("Не знайдено обліковий запис для користувача та валюти"));

        account.setBalance(account.getBalance() - amount);
        accountRepository.update(account);
    }

    public void transferMoney(User sourceUser, User targetUser, double amount, CurrencyType currency) {
        Account sourceAccount = accountRepository.findByUserAndCurrency(sourceUser, currency)
                .orElseThrow(() -> new IllegalArgumentException("Вихідний рахунок не знайдено для користувача та валюти"));

        Account targetAccount = accountRepository.findByUserAndCurrency(targetUser, currency)
                .orElseThrow(() -> new IllegalArgumentException("Цільовий рахунок не знайдено для користувача та валюти"));

        if (sourceAccount.getBalance() < amount) {
            throw new IllegalArgumentException("Недостатньо коштів на вихідному рахунку");
        }


        targetAccount.setBalance(targetAccount.getBalance() + amount);
        sourceAccount.setBalance(sourceAccount.getBalance() - amount);

        accountRepository.update(sourceAccount);
        accountRepository.update(targetAccount);
    }

    public void convertCurrency(User user, double amount, CurrencyType sourceCurrency, CurrencyType targetCurrency) {

        Account targetAccount = accountRepository.findByUserAndCurrency(user, targetCurrency)
                .orElseThrow(() -> new IllegalArgumentException("Цільовий рахунок не знайдено для користувача та валюти"));

        Account sourceAccount = accountRepository.findByUserAndCurrency(user, sourceCurrency)
                .orElseThrow(() -> new IllegalArgumentException("Не знайдено обліковий запис для користувача та вихідної валюти"));
        if (sourceAccount.getBalance() < amount) {
            throw new IllegalArgumentException("Недостатньо коштів на рахунку для конвертації валюти");
        }
        double exchangeRate = getExchangeRate(sourceCurrency, targetCurrency);
        double convertedAmount = amount * exchangeRate;

        targetAccount.setBalance(targetAccount.getBalance() + convertedAmount);
        sourceAccount.setBalance(sourceAccount.getBalance() - amount);
        accountRepository.update(sourceAccount);
        accountRepository.update(targetAccount);
    }

    public double getAccountBalanceInUah(User user) {
        double totalBalanceInUah = 0.0;
        List<Account> accounts = accountRepository.findByUser(user);
        for (Account account : accounts) {
            double exchangeRate = getExchangeRate(account.getCurrency(), CurrencyType.UAH);
            totalBalanceInUah += account.getBalance() * exchangeRate;
        }
        return totalBalanceInUah;
    }

    private double getExchangeRate(CurrencyType sourceCurrency, CurrencyType targetCurrency) {
        ExchangeRate exchangeRate = exchangeRateRepository.findByBaseCurrencyAndTargetCurrency(sourceCurrency, targetCurrency)
                .orElseThrow(() -> new IllegalArgumentException("Курс обміну не знайдено для вказаних валют"));
        return exchangeRate.getRate();
    }

}