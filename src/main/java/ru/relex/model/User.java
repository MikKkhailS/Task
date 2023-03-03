package ru.relex.model;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.util.List;

@Entity
@Table(name = "Users")
@Getter
@Setter
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_id")
    private int id;

    @Column(name = "username")
    private String username;

    @Column(name = "email")
    private String email;

    @Column(name = "user_role")
    private String role;

    @Column(name = "wallet_number")
    private String walletNumber;

    @Column(name = "btc_balance")
    private String btcBalance;

    @Column(name = "ton_balance")
    private String tonBalance;

    @Column(name = "rub_balance")
    private String rubBalance;

    @OneToMany(mappedBy = "user", fetch = FetchType.LAZY, cascade = {CascadeType.DETACH,
            CascadeType.MERGE, CascadeType.PERSIST, CascadeType.REFRESH})
    private List<Operation> operations;

    public void addOperation(Operation operation) {
        this.operations.add(operation);
        operation.setUser(this);
    }

    @Transient
    private String withdrawToCard;

    @Transient
    private String withdrawToWallet;

    @Transient
    private String currency;

    @Transient
    private String amount;

    @Transient
    private String dateFrom;

    @Transient
    private String dateTo;

    @Transient
    private String currTo;
}