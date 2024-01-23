package org.example;


import org.example.currency.CurrencyType;
import org.example.entity.Account;
import org.example.entity.User;
import org.example.repository.AccountRepository;
import org.example.repository.ExchangeRateRepository;
import org.example.repository.TransactionRepository;
import org.example.repository.UserRepository;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;

import java.io.IOException;
import java.util.List;
import java.util.Scanner;

public class AppLauncher {
    public static void main(String[] args) throws IOException {

        SessionFactory factory = new Configuration().configure().buildSessionFactory();

        AccountRepository accountRepository = new AccountRepository(factory);
        TransactionRepository transactionRepository = new TransactionRepository(factory);
        ExchangeRateRepository exchangeRateRepository = new ExchangeRateRepository(factory);
        UserRepository userRepository = new UserRepository(factory);
        Scanner scanner = new Scanner(System.in);

        User user = new User();

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
                    System.out.println("Введи id клієнта: ");
                    long userId = scanner.nextInt();
                    if (userRepository.findByID(userId) == null) {
                        user.setUsername("JohnDoe");
                        userRepository.save(user);
                    } else {

                    }
                    break;
                case 2:
                    System.out.println(" ");
                    break;
                case 3:
                    System.out.println(" ");
                    break;
            }
        }
    }
}



        /*while(true){
            System.out.println("Меню");
            System.out.println("Обери 1, якщо бажаешь поповнити рахунок користувачу в потрібній йому валюті");
            System.out.println("Обери 2, якщо бажаешь зробити переказ грошей з одного рахунку на інший");
            System.out.println("Обери 3, якщо бажаешь конвертувати валюту в рамках рахунків одного користувача");
            System.out.println("Обери 4, якщо бажаешь отримати суму на рахунку в гривні (розрахунок за курсом на цей день)");

            int userChoice = scanner.nextInt();
            scanner.nextLine();

            switch (userChoice){
                case 1:
                    System.out.println("Введи id клієнта: ");
                    int userId = scanner.nextInt();

                    scanner.nextLine();
                    System.out.println("Введи потрібну валюту: ");
                    name = scanner.nextLine();
                    System.out.println("Введи номер телефону: ");
                    phoneNumber = scanner.nextLine();
                    Student student = new Student(name, phoneNumber);
                    contactsStudent.put(numberStudent, student);
                    System.out.println("Номер успішно додан в записну книгу");
                    break;
                case 2:
                    System.out.println("Введи номер студента якого хочешь отримати");
                    int numberToRetrive = scanner.nextInt();
                    if(contactsStudent.containsKey(numberToRetrive)){
                        Student studentToRetrive = contactsStudent.get(numberToRetrive);;
                        System.out.println(studentToRetrive + " був відображений із контактів" );
                    } else {
                        System.out.println("Контакт не знайдено");
                    }
                    break;
                case 3:
                    System.out.println("Введи номер студента якого бажаєш видалити");
                    int numberToRemove = scanner.nextInt();
                    if (contactsStudent.containsKey(numberToRemove)){
                        contactsStudent.remove(numberToRemove);
                        System.out.println("Контакт успішно видаленний");
                    } else{
                        System.out.println("Контакт не знайдено");
                    }
                    break;

            }

        }

    }
}
*/