package org.example.data.dto;

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
        private Double buy;
        private Double sale;
}
