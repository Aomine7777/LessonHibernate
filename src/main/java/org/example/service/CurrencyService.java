package org.example.service;

import java.io.IOException;

public interface CurrencyService {
    String getCurrencyRates() throws IOException;
    void saveCurrencyRates() throws IOException;
}
