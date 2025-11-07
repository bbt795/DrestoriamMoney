package com.drestoriam.drestoriammoney.classes;

import org.bukkit.entity.Player;

import java.math.BigDecimal;

public class PlayerBank {

    private Player owner;
    private String name;
    private BigDecimal balance;

    public PlayerBank(Player owner){

        this.setOwner(owner);
        this.setName(owner.getDisplayName() + "'s Bank");
        this.setBalance(new BigDecimal(0));

    }

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
