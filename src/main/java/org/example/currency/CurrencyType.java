package org.example.currency;


import lombok.Getter;

@Getter
public enum CurrencyType {
    UAH(980), EUR(978), USD(840);
    private final int ISOCode;


    CurrencyType(int isoCode) {
        ISOCode = isoCode;
    }
}
