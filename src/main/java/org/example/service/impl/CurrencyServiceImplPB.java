package org.example.service.impl;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import org.example.currency.CurrencyType;
import org.example.data.entity.ExchangeRate;
import org.example.repository.ExchangeRateRepository;
import org.example.service.CurrencuService;
import org.example.data.dto.CurrencyItemDtoPB;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;
import org.jsoup.Jsoup;

import java.io.IOException;
import java.lang.reflect.Type;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class CurrencyServiceImplPB implements CurrencuService {

    private final static String URL = "https://api.privatbank.ua/p24api/pubinfo?exchange&json&coursid=11";
    static SessionFactory factory = new Configuration().configure().buildSessionFactory();
    ExchangeRateRepository exchangeRateRepository = new ExchangeRateRepository(factory);

    @Override
    public void getAndSaveCurrencyRates() throws IOException {
        List<ExchangeRate> byDate = exchangeRateRepository.findByDate(LocalDate.now());
        if (byDate.isEmpty()) {

            String jsonString = Jsoup.connect(URL)
                    .ignoreContentType(true)
                    .get()
                    .body()
                    .text();

            System.out.println(jsonString);

            Type type = TypeToken
                    .getParameterized(List.class, CurrencyItemDtoPB.class)
                    .getType();

            List<CurrencyItemDtoPB> allCurrencies;
            allCurrencies = new Gson().fromJson(jsonString, type);
            List<ExchangeRate> exchangeRates = new ArrayList<>();

            CurrencyItemDtoPB usdRate = allCurrencies.stream()
                    .filter(currencyItemDtoPB -> currencyItemDtoPB.getCcy().equals(CurrencyType.USD) && currencyItemDtoPB.getBase_ccy().equals(CurrencyType.UAH))
                    .findFirst()
                    .get();
            CurrencyItemDtoPB eurRate = allCurrencies.stream()
                    .filter(currencyItemDtoPB -> currencyItemDtoPB.getCcy().equals(CurrencyType.EUR) && currencyItemDtoPB.getBase_ccy().equals(CurrencyType.UAH))
                    .findFirst()
                    .get();
            ExchangeRate usdEurRate = new ExchangeRate(
                    null,
                    CurrencyType.USD,
                    CurrencyType.EUR,
                    usdRate.getBuy() / eurRate.getBuy(),
                    LocalDate.now()
            );
            ExchangeRate eurUsdRate = new ExchangeRate(
                    null,
                    CurrencyType.EUR,
                    CurrencyType.USD,
                    eurRate.getBuy() / usdRate.getBuy(),
                    LocalDate.now()
            );
            exchangeRates.add(usdEurRate);
            exchangeRates.add(eurUsdRate);

            allCurrencies.forEach(currencyItemDtoPB -> {
                ExchangeRate exchangeRate1 = new ExchangeRate(
                        null,
                        currencyItemDtoPB.getBase_ccy(),
                        currencyItemDtoPB.getCcy(),
                        currencyItemDtoPB.getBuy(),
                        LocalDate.now()
                );
                ExchangeRate exchangeRate2 = new ExchangeRate(
                        null,
                        currencyItemDtoPB.getCcy(),
                        currencyItemDtoPB.getBase_ccy(),
                        currencyItemDtoPB.getSale(),
                        LocalDate.now()
                );
                exchangeRates.add(exchangeRate1);
                exchangeRates.add(exchangeRate2);
            });
            exchangeRateRepository.saveAll(exchangeRates);
        }
    }
}