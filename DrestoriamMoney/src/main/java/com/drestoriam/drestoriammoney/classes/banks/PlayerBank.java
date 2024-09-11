package com.drestoriam.drestoriammoney.classes.banks;

import com.drestoriam.drestoriammoney.DrestoriamMoney;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;

public class PlayerBank extends Bank implements Listener {

    public PlayerBank(Player owner){

        this.setOwner(owner);
        this.setName(owner.getDisplayName() + "'s Bank");

    }

    @Override
    public double getBalance() {

        PersistentDataContainer playerBalance = this.getOwner().getPersistentDataContainer();
        NamespacedKey key = new NamespacedKey(DrestoriamMoney.getPlugin(), "moneyBalance");

        if(playerBalance.has(key, PersistentDataType.DOUBLE)) {

            return playerBalance.get(key, PersistentDataType.DOUBLE);

        } else {

            playerBalance.set(key, PersistentDataType.DOUBLE, 0.0);
            return getBalance();

        }


    }

    @Override
    public void setBalance(double balance){

        PersistentDataContainer playerBalance = this.getOwner().getPersistentDataContainer();
        NamespacedKey key = new NamespacedKey(DrestoriamMoney.getPlugin(), "moneyBalance");

        if(playerBalance.has(key, PersistentDataType.DOUBLE)){

            double newBalance = getBalance() + balance;

            playerBalance.set(key, PersistentDataType.DOUBLE, newBalance);

        } else {

            playerBalance.set(key, PersistentDataType.DOUBLE, 0.0);
            setBalance(balance);

        }

    }

}
