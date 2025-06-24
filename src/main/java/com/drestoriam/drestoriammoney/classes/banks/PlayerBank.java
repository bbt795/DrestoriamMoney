package com.drestoriam.drestoriammoney.classes.banks;

import com.drestoriam.drestoriammoney.DrestoriamMoney;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;

import java.math.BigDecimal;

public class PlayerBank extends Bank implements Listener {

    public PlayerBank(Player owner){

        this.setOwner(owner);
        this.setName(owner.getDisplayName() + "'s Bank");

    }

    @Override
    public BigDecimal getBankBalance() {

        PersistentDataContainer playerBalance = this.getOwner().getPersistentDataContainer();
        NamespacedKey key = new NamespacedKey(DrestoriamMoney.getPlugin(), "moneyBalance");

        if(playerBalance.has(key, PersistentDataType.STRING)) {

            return new BigDecimal(playerBalance.get(key, PersistentDataType.STRING));

        } else {

            playerBalance.set(key, PersistentDataType.STRING, "0.00");
            return getBankBalance();

        }


    }

    @Override
    public void setBalance(BigDecimal balance){

        PersistentDataContainer playerBalance = this.getOwner().getPersistentDataContainer();
        NamespacedKey key = new NamespacedKey(DrestoriamMoney.getPlugin(), "moneyBalance");

        if(playerBalance.has(key, PersistentDataType.STRING)){

            playerBalance.set(key, PersistentDataType.STRING, balance.toString());

        } else {

            playerBalance.set(key, PersistentDataType.STRING, "0.00");
            setBalance(balance);

        }

    }

}
