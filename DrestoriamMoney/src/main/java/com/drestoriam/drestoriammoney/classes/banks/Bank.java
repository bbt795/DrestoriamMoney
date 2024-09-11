package com.drestoriam.drestoriammoney.classes.banks;

import org.bukkit.entity.Player;

public class Bank {

    private Player owner;
    private String name;
    private double balance;

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

    public double getBalance() {
        return balance;
    }

    public void setBalance(double balance){
        this.balance = balance;
    }

}
