package ru.relex.model;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.util.Date;

@Entity
@Table(name = "Exchange_rates")
@Getter
@Setter
public class ExchangeRate {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "currency_id")
    private int id;

    @Column(name = "currency")
    private String currency;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "updated_at")
    private Date updatedAt;

    @Column(name = "to_rub")
    private String toRub;

    @Column(name = "to_btc")
    private String toBtc;

    @Column(name = "to_ton")
    private String toTon;

    @Column(name = "full_currency_name")
    private String fullCurrencyName;
}