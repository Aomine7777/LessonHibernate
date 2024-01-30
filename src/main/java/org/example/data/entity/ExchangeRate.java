package org.example.data.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.example.currency.CurrencyType;

import java.time.LocalDate;
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "exchange_rates",schema = "bank")
public class ExchangeRate {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(name = "base_currency", nullable = false)
    private CurrencyType baseCurrency;

    @Enumerated(EnumType.STRING)
    @Column(name = "target_currency", nullable = false)
    private CurrencyType targetCurrency;

    @Column(nullable = false)
    private double rate;
    @Column(nullable = false)
    private LocalDate date;
}
