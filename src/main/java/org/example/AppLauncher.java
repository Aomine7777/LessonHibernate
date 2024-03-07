package org.example;


import org.example.currency.CurrencyType;
import org.example.data.entity.Account;
import org.example.data.entity.ExchangeRate;
import org.example.data.entity.User;
import org.example.exeptions.CustomException;
import org.example.repository.AccountRepository;
import org.example.repository.ExchangeRateRepository;
import org.example.repository.UserRepository;
import org.example.service.CurrencyService;
import org.example.service.impl.PrivatBankCurrencyService;
import org.example.service.impl.TransactionService;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;

import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

public class AppLauncher {
    static SessionFactory factory = new Configuration().configure().buildSessionFactory();

    static PrivatBankCurrencyService currencyServiceImplPB = new PrivatBankCurrencyService();
    static AccountRepository accountRepository = new AccountRepository(factory);
    public static ExchangeRateRepository exchangeRateRepository = new ExchangeRateRepository(factory);
    static UserRepository userRepository = new UserRepository(factory);
    static TransactionService transactionService = new TransactionService(accountRepository, exchangeRateRepository);
    static Scanner scanner = new Scanner(System.in);

    static Optional<User> userOptional = Optional.empty();

    public static void main(String[] args) throws IOException {
        currencyServiceImplPB.saveCurrencyRates();
        while (true) {
            System.out.println("Меню");
            System.out.println("Обери 0, якщо бажаешь створити користувача");
            System.out.println("Обери 1, якщо бажаешь створити акаунт для користувача");
            System.out.println("Обери 2, якщо бажаешь поповнити рахунок користувачу в потрібній йому валюті");
            System.out.println("Обери 3, якщо бажаешь зробити переказ грошей з одного рахунку на інший");
            System.out.println("Обери 4, якщо бажаешь конвертувати валюту в рамках рахунків одного користувача");
            System.out.println("Обери 5, якщо бажаешь отримати суму на рахунку в гривні (розрахунок за курсом на цей день)");
            System.out.println("Обери 6, якщо бажаешь зняти кошти з рахунку");
            System.out.println("Обери 7, якщо бажаешь отримати курс валют на потрібну дати");
            System.out.println("Обери 8, якщо бажаешь отримати баланс з усіх рахунків у валюті користувача");


            int userChoice = scanner.nextInt();
            scanner.nextLine();

            switch (userChoice) {
                case 0:
                    System.out.println("Створення користувача.");
                    System.out.println("Введіть ім'я нового користувача:");
                    String newUserName = scanner.nextLine();

                    User newUser = new User();
                    newUser.setUsername(newUserName);

                    Optional<User> savedUserOptional = userRepository.save(newUser);

                    if (savedUserOptional.isPresent()) {
                        System.out.println("Новий користувач успішно створений.");
                        System.out.println("ID нового користувача: " + savedUserOptional.get().getId());
                    } else {
                        System.out.println("Помилка при створенні нового користувача.");
                    }
                    break;

                case 1:
                    System.out.println("Створення рахунку для користувача");
                    System.out.println("Введіть айді користувача для створення рахунку");
                    long findId = scanner.nextLong();
                    scanner.nextLine();

                    Optional<User> userOptional11 = userRepository.getById(findId);
                    if (userOptional11.isPresent()) {
                        System.out.println("Введіть валюту рахунку( наприклад, USD, EUR, UAH):");
                        String currencyInput = scanner.nextLine();
                        CurrencyType currencyType;
                        try {
                            currencyType = CurrencyType.valueOf(currencyInput.toUpperCase());
                        } catch (CustomException e) {
                            System.out.println("Некоректна валюта! Використовуйте лише USD, EUR або UAH.");
                            break;
                        }
                        System.out.println("Введіть початковий баланс рахуну:");
                        double initialBalance = scanner.nextDouble();

                        Optional<Account> savedAccountOptional = accountRepository.createAccountForUser(userOptional11.get(), currencyType, initialBalance);

                        if (savedAccountOptional.isPresent()) {
                            System.out.println("Новий рахуенок успішно створений");
                            System.out.println("ID нового рахунку" + savedAccountOptional.get().getId());
                        } else {
                            System.out.println("Помилка при створенні нового рахунку");
                        }
                    } else {
                        System.out.println("Користувача з введеним ідинтифікатором не знайдено");
                    }
                    break;

                case 2:
                    System.out.println("Введи id пользователя: ");
                    long userId = scanner.nextLong();
                    userOptional = userRepository.getById(userId);
                    if (!userOptional.isPresent()) {
                        System.out.println("Користувач за таким Id немає в системі");
                        break;
                    } else {
                        User existingUser = userOptional.get();
                        System.out.println("Пользователь найден в базе данных, его имя: " + existingUser.getUsername());
                    }

                    System.out.println("Введи сумму пополнения: ");
                    double inputAmount = scanner.nextDouble();
                    if (inputAmount < 0) {
                        System.out.println("Ви ввели відємне число, приберіть мінус перед числом");
                        break;
                    }
                    scanner.nextLine();

                    CurrencyType inputCurrency = null;
                    boolean validCurrency = false;
                    while (!validCurrency) {
                        System.out.println("Введи тип валюты (USD, EUR или UAH): ");
                        String currencyInput = scanner.nextLine().toUpperCase();
                        try {
                            inputCurrency = CurrencyType.valueOf(currencyInput);
                            validCurrency = true;
                        } catch (CustomException e) {
                            System.out.println("Некоректний ввід валюти. Введи правильний тип валюти (USD, EUR або UAH).");
                        }
                    }
                    transactionService.modifyUserAccountBalance(userOptional, inputCurrency, inputAmount);
                    break;
                case 3:
                    System.out.println("Введіть id користувача, який здійснює переказ: ");
                    long sourceUserId = scanner.nextInt();
                    Optional<User> sourceUser = userRepository.getById(sourceUserId);
                    if (!sourceUser.isPresent()) {
                        System.out.println("Користувача з таким ID не знайдено.");
                        break;
                    }

                    System.out.println("Введіть id користувача, на рахунок якого буде здійснено переказ: ");
                    long targetUserId = scanner.nextInt();
                    Optional<User> targetUser = userRepository.getById(targetUserId);
                    if (!targetUser.isPresent()) {
                        System.out.println("Користувача з таким ID не знайдено.");
                        break;
                    }

                    System.out.println("Введіть суму переказу: ");
                    double transferAmount = scanner.nextDouble();

                    // Отримання інформації від користувача
                    validCurrency = false;
                    while (!validCurrency) {
                        System.out.println("Введіть валюту переказу (USD, EUR або UAH): ");
                        scanner.nextLine(); // Очистка буфера вводу
                        String transferCurrencyInput = scanner.nextLine();
                        CurrencyType transferCurrency = CurrencyType.valueOf(transferCurrencyInput.toUpperCase());

                        transactionService.transferMoney(sourceUser, targetUser, transferAmount, transferCurrency);
                        break;
                    }
                    break;
                case 4:
                    // Отримання інформації від користувача
                    System.out.println("Введіть id користувача для конвертації валют: ");
                    long conversionUserId = scanner.nextInt();
                    Optional<User> conversionUser = userRepository.getById(conversionUserId);
                    if (!conversionUser.isPresent()) {
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

                    transactionService.convertCurrency(conversionUser, convertAmount, sourceCurrency, targetCurrency);
                    break;
                case 5:
                    System.out.println("Введіть id користувача: ");
                    long findUserId = scanner.nextInt();
                    Optional<User> findUser = userRepository.getById(findUserId);
                    if (!findUser.isPresent()) {
                        System.out.println("Користувача з таким ID не знайдено.");
                        break;
                    }

                    // Отримання суми на рахунку в гривні з урахуванням поточного курсу на день
                    double balanceInUah = transactionService.getAccountBalanceInUah(findUser);

                    System.out.println("Сума на рахунку користувача в гривні: " + balanceInUah + " UAH");
                case 6:
                    System.out.println("Введіть id користувача: ");
                    userId = scanner.nextLong();
                    userOptional = userRepository.getById(userId);
                    if (!userOptional.isPresent()) {
                        System.out.println("Користувача з таким ID не знайдено.");
                        break;
                    } else {
                        User existingUser = userOptional.get();
                        System.out.println("Користувач знайден у базі данних, його ім'я: " + existingUser.getUsername());
                    }
                    System.out.println("Введи сумму зняття");
                    inputAmount = scanner.nextDouble();

                    if (inputAmount > 0) {
                        System.out.println("Ви ввели додаткове число, додайте мінус перед числом");
                        break;
                    }
                    scanner.nextLine();
                    inputCurrency = null;
                    validCurrency = false;
                    while (!validCurrency) {
                        System.out.println("Введіть тип валюти (USD, EUR или UAH): ");
                        String currencyInput = scanner.nextLine().toUpperCase();
                        try {
                            inputCurrency = CurrencyType.valueOf(currencyInput);
                            validCurrency = true;
                        } catch (CustomException e) {
                            System.out.println("Неккоректний ввід валюти. Введи правильний тип валюти (USD, EUR або UAH).");
                        }
                    }
                    transactionService.modifyUserAccountBalance(userOptional, inputCurrency, inputAmount);
                    System.out.println("З " + inputCurrency + " рахунку знято: " + inputAmount + "(" + inputCurrency + ")");
                    break;

                case 7:
                    System.out.println("Введи потрібну дату в форматі 'dd-MM-yyyy': ");
                    String dateString = scanner.nextLine();

                    if (!dateString.matches("\\d{2}-\\d{2}-\\d{4}}")) {
                        System.out.println("Некоректний формат дати. Дата повинна бути в форматі 'dd-MM-yyyy'.");
                        break;
                    }

                    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy");
                    LocalDate date;

                    try {
                        date = LocalDate.parse(dateString, formatter);
                    } catch (CustomException e) {
                        System.out.println("Помилка при розборі дати: " + e.getMessage());
                        break;
                    }
                    currencyServiceImplPB.printCurrencyRatesByDate(date);


                case 8:
                    System.out.println("Введіть id користувача: ");
                    userId = scanner.nextLong();
                    userOptional = userRepository.getById(userId);
                    if (!userOptional.isPresent()) {
                        System.out.println("Користувача з таким ID не знайдено.");
                        break;
                    } else {
                        User existingUser = userOptional.get();
                        System.out.println("Користувач знайден у базі данних, його рахунки: " + existingUser.getUsername());
                        List<Account> userAccounts = accountRepository.findByUser(Optional.of(existingUser));
                        Map<String, Double> collect = userAccounts.stream().collect(Collectors.toMap(account -> account.getCurrency().toString(), account -> account.getBalance()));
                        System.out.println("Мапа рахунків користувача: " + collect);
                        break;
                    }
            }
        }
    }
}