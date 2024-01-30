package org.example;


import org.example.currency.CurrencyType;
import org.example.data.entity.User;
import org.example.repository.AccountRepository;
import org.example.repository.ExchangeRateRepository;
import org.example.repository.TransactionRepository;
import org.example.repository.UserRepository;
import org.example.service.CurrencuService;
import org.example.service.impl.CurrencyServiceImplPB;
import org.example.service.impl.TransactionService;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;

import java.io.IOException;
import java.util.Scanner;

public class AppLauncher {
    static SessionFactory factory = new Configuration().configure().buildSessionFactory();

    static CurrencuService currencyServiceImplPB = new CurrencyServiceImplPB();
    static AccountRepository accountRepository = new AccountRepository(factory);
    static TransactionRepository transactionRepository = new TransactionRepository(factory);
    static ExchangeRateRepository exchangeRateRepository = new ExchangeRateRepository(factory);
    static UserRepository userRepository = new UserRepository(factory);
    static TransactionService transactionServic = new TransactionService(userRepository, accountRepository, exchangeRateRepository,transactionRepository);
    static Scanner scanner = new Scanner(System.in);

    static User user = new User();
    public static void main(String[] args) throws IOException {
        currencyServiceImplPB.getAndSaveCurrencyRates();
        while (true) {
            System.out.println("Меню");
            System.out.println("Обери 1, якщо бажаешь поповнити рахунок користувачу в потрібній йому валюті");
            System.out.println("Обери 2, якщо бажаешь зробити переказ грошей з одного рахунку на інший");
            System.out.println("Обери 3, якщо бажаешь конвертувати валюту в рамках рахунків одного користувача");
            System.out.println("Обери 4, якщо бажаешь отримати суму на рахунку в гривні (розрахунок за курсом на цей день)");

            int userChoice = scanner.nextInt();
            scanner.nextLine();

            switch (userChoice) {
                case 1:
                    System.out.println("Введи id пользователя: ");
                    long userId = scanner.nextLong();
                    User user = userRepository.findByID(userId);
                    if (user == null) {
                        user = new User();
                        user.setUsername("JohnDoe");
                        userRepository.save(user);
                    } else {
                        System.out.println("Пользователь найден в базе данных, его имя: " + user.getUsername());
                    }

                    System.out.println("Введи сумму пополнения: ");
                    double inputAmount = scanner.nextDouble();

                    scanner.nextLine();

                    System.out.println("Введи тип валюты (USD, EUR или UAH): ");
                    String currencyInput = scanner.nextLine().toUpperCase();
                    CurrencyType inputCurrency = CurrencyType.valueOf(currencyInput);

                    transactionServic.increaseUserAccountBalance(user, inputCurrency, inputAmount);
                    break;
                case 2:
                    System.out.println("Введіть id користувача, який здійснює переказ: ");
                    long sourceUserId = scanner.nextInt();
                    User sourceUser = userRepository.findByID(sourceUserId);
                    if (sourceUser == null) {
                        System.out.println("Користувача з таким ID не знайдено.");
                        break;
                    }

                    System.out.println("Введіть id користувача, на рахунок якого буде здійснено переказ: ");
                    long targetUserId = scanner.nextInt();
                    User targetUser = userRepository.findByID(targetUserId);
                    if (targetUser == null) {
                        System.out.println("Користувача з таким ID не знайдено.");
                        break;
                    }

                    System.out.println("Введіть суму переказу: ");
                    double transferAmount = scanner.nextDouble();

                    // Отримання інформації від користувача
                    System.out.println("Введіть валюту переказу (USD, EUR або UAH): ");
                    scanner.nextLine(); // Очистка буфера вводу
                    String transferCurrencyInput = scanner.nextLine();
                    CurrencyType transferCurrency = CurrencyType.valueOf(transferCurrencyInput.toUpperCase());

                    transactionServic.transferMoney(sourceUser, targetUser, transferAmount, transferCurrency);
                    break;
                case 3:
                    // Отримання інформації від користувача
                    System.out.println("Введіть id користувача для конвертації валют: ");
                    long userId3 = scanner.nextInt();
                    User user3 = userRepository.findByID(userId3);
                    if (user3 == null) {
                        System.out.println("Користувача з таким ID не знайдено.");
                        break;
                    }
                    // Отримання інформації від користувача
                    System.out.println("Введіть суму для конвертації: ");
                    double convertAmount = scanner.nextDouble();

                    // Отримання інформації від користувача
                    System.out.println("Введіть вихідну валюту (USD, EUR або UAH): ");
                    scanner.nextLine(); // Очистка буфера вводу
                    String sourceCurrencyInput = scanner.nextLine();
                    CurrencyType sourceCurrency = CurrencyType.valueOf(sourceCurrencyInput.toUpperCase());

                    // Отримання інформації від користувача
                    System.out.println("Введіть цільову валюту (USD, EUR або UAH): ");
                    String targetCurrencyInput = scanner.nextLine();
                    CurrencyType targetCurrency = CurrencyType.valueOf(targetCurrencyInput.toUpperCase());

                    transactionServic.convertCurrency(user3, convertAmount, sourceCurrency, targetCurrency);
                    break;
                case 4:
                    System.out.println("Введіть id користувача: ");
                    long userId1 = scanner.nextInt();
                    User user1 = userRepository.findByID(userId1);
                    if (user1 == null) {
                        System.out.println("Користувача з таким ID не знайдено.");
                        break;
                    }

                    // Отримання суми на рахунку в гривні з урахуванням поточного курсу на день
                    double balanceInUah = transactionServic.getAccountBalanceInUah(user1);

                    System.out.println("Сума на рахунку користувача в гривні: " + balanceInUah + " UAH");
            }
        }
    }
}