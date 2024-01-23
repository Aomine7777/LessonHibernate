package org.example.currency.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import org.example.currency.CurrencyType;

@Builder
@Data
@AllArgsConstructor
public class CurrencyItemDtoPB {
        private CurrencyType ccy;
        private CurrencyType base_ccy;
        private String buy;
        private String sale;
}
