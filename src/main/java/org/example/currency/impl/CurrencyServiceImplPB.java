package org.example.currency.impl;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import org.example.currency.CurrencuService;
import org.example.currency.CurrencyType;
import org.example.currency.dto.CurrencyItemDtoPB;
import org.jsoup.Jsoup;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.List;

public class CurrencyServiceImplPB implements CurrencuService {
    static String url = "https://api.privatbank.ua/p24api/pubinfo?exchange&json&coursid=11";
    List<CurrencyItemDtoPB> allCurrencies;
    @Override
    public double getSaleRate(CurrencyType ccy) throws IOException {
        CurrencyItemDtoPB neededDto = getDtoObject(ccy);
        return Double.parseDouble(neededDto.getSale());
    }

    @Override
    public double getBuyRate(CurrencyType ccy) throws IOException {
        CurrencyItemDtoPB neededDto = getDtoObject(ccy);
        return Double.parseDouble(neededDto.getBuy());
    }
    public CurrencyItemDtoPB getDtoObject(CurrencyType ccy) throws IOException {
        doRequest();
        CurrencyItemDtoPB current = allCurrencies.stream()
                .filter((c) -> c.getCcy().equals(ccy))
                .filter((c) -> c.getBase_ccy() == CurrencyType.UAH)
                .findFirst()
                .orElseThrow();
        System.out.println(current);

        return current;
    }
    private void doRequest() throws IOException {

        String jsonString = Jsoup.connect(url)
                .ignoreContentType(true)
                .get()
                .body()
                .text();

        System.out.println(jsonString);

        Type type = TypeToken
                .getParameterized(List.class, CurrencyItemDtoPB.class)
                .getType();

        allCurrencies = new Gson().fromJson(jsonString, type);
    }
}
