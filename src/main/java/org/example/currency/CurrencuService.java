package org.example.currency;

import java.io.IOException;

public interface CurrencuService {
    double getSaleRate(CurrencyType ccy) throws IOException;
    double getBuyRate(CurrencyType ccy) throws IOException;
}
