package com.drestoriam.drestoriammoney.classes.banks;

import org.bukkit.entity.Player;

import java.math.BigDecimal;

public class Bank {

    private Player owner;
    private String name;
    private BigDecimal balance;

    public Player getOwner() {
        return owner;
    }

    public void setOwner(Player owner) {
        this.owner = owner;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public BigDecimal getBankBalance() {
        return balance;
    }

    public void setBalance(BigDecimal balance){
        this.balance = balance;
    }

}
