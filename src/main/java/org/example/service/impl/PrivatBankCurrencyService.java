package org.example.service.impl;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import org.example.currency.CurrencyType;
import org.example.data.entity.ExchangeRate;
import org.example.repository.ExchangeRateRepository;
import org.example.service.CurrencyService;
import org.example.data.dto.CurrencyItemDtoPB;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;
import org.jsoup.Jsoup;

import java.io.IOException;
import java.lang.reflect.Type;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class PrivatBankCurrencyService implements CurrencyService {

    private final static String URL = "https://api.privatbank.ua/p24api/pubinfo?exchange&json&coursid=11";
    static SessionFactory factory = new Configuration().configure().buildSessionFactory();
    ExchangeRateRepository exchangeRateRepository = new ExchangeRateRepository(factory);

    @Override
    public void saveCurrencyRates() throws IOException {
        List<ExchangeRate> byDate = exchangeRateRepository.findByDate(LocalDate.now());
        if (byDate.isEmpty()) {

            String jsonString = getCurrencyRates();
            List<CurrencyItemDtoPB> allCurrencies = parseCurrencyRatesJson(jsonString);
            saveExchangeRates(allCurrencies);
        }
    }

    private List<CurrencyItemDtoPB> parseCurrencyRatesJson(String jsonString) {
        Type type = TypeToken.getParameterized(List.class, CurrencyItemDtoPB.class).getType();
        return new Gson().fromJson(jsonString, type);
    }
    @Override
    public String getCurrencyRates() throws IOException {
        return Jsoup.connect(URL).ignoreContentType(true).get().body().text();
    }

    private void saveExchangeRates(List<CurrencyItemDtoPB> allCurrencies) {
        List<ExchangeRate> exchangeRates = new ArrayList<>();

        CurrencyItemDtoPB usdRate = allCurrencies.stream().filter(currencyItemDtoPB -> currencyItemDtoPB.getCcy().equals(CurrencyType.USD) && currencyItemDtoPB.getBase_ccy().equals(CurrencyType.UAH)).findFirst().orElseThrow(() -> new RuntimeException("USD rate not found"));
        CurrencyItemDtoPB eurRate = allCurrencies.stream().filter(currencyItemDtoPB -> currencyItemDtoPB.getCcy().equals(CurrencyType.EUR) && currencyItemDtoPB.getBase_ccy().equals(CurrencyType.UAH)).findFirst().orElseThrow(() -> new RuntimeException("EUR rate not found"));
        ExchangeRate usdEurRate = new ExchangeRate(null, CurrencyType.USD, CurrencyType.EUR, usdRate.getBuy() / eurRate.getBuy(), LocalDate.now());
        ExchangeRate eurUsdRate = new ExchangeRate(null, CurrencyType.EUR, CurrencyType.USD, eurRate.getBuy() / usdRate.getBuy(), LocalDate.now());
        exchangeRates.add(usdEurRate);
        exchangeRates.add(eurUsdRate);

        allCurrencies.forEach(currencyItemDtoPB -> {
            ExchangeRate exchangeRate1 = new ExchangeRate(null, currencyItemDtoPB.getBase_ccy(), currencyItemDtoPB.getCcy(), currencyItemDtoPB.getBuy(), LocalDate.now());
            ExchangeRate exchangeRate2 = new ExchangeRate(null, currencyItemDtoPB.getCcy(), currencyItemDtoPB.getBase_ccy(), currencyItemDtoPB.getSale(), LocalDate.now());
            exchangeRates.add(exchangeRate1);
            exchangeRates.add(exchangeRate2);
        });
        exchangeRateRepository.saveAll(exchangeRates);
    }
    public void printCurrencyRatesByDate(LocalDate date){
        List<ExchangeRate> exchangeRates = exchangeRateRepository.findByDate(date);

        if (exchangeRates.isEmpty()){
            System.out.println("Для вказаннох дати курс валют відсутній в базі даних.");
        } else {
            System.out.println("Ви ввели дату: " + date + ", курс валют:");
            for (ExchangeRate rate : exchangeRates) {
                String formattedRate = String.format("%s -> %s: %.2f", rate.getBaseCurrency(), rate.getTargetCurrency(), rate.getRate());
                System.out.println(formattedRate);
            }
        }
    }
}