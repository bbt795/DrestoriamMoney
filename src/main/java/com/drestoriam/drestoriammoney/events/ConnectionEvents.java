package com.drestoriam.drestoriammoney.events;

import com.drestoriam.drestoriammoney.DrestoriamMoney;
import com.drestoriam.drestoriammoney.classes.PlayerBank;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;

import java.math.BigDecimal;
import java.util.HashMap;

public class ConnectionEvents implements Listener {

    private HashMap<String, PlayerBank> bankSheet;

    public ConnectionEvents(HashMap<String, PlayerBank> bankSheet){

        this.bankSheet = bankSheet;

    }

    @EventHandler
    public void onConnect(PlayerJoinEvent event){

        Player player = event.getPlayer();
        String uuid = player.getUniqueId().toString();

        PersistentDataContainer playerInfo = player.getPersistentDataContainer();
        NamespacedKey balancekey = new NamespacedKey(DrestoriamMoney.getPlugin(), "moneyBalance");

        if(!playerInfo.has(balancekey, PersistentDataType.STRING)){

            playerInfo.set(balancekey, PersistentDataType.STRING, "0.00");

        }

        PlayerBank playerBank = new PlayerBank(player);
        playerBank.setBalance(new BigDecimal(playerInfo.get(balancekey,PersistentDataType.STRING)));

        bankSheet.put(uuid, playerBank);

    }

    @EventHandler
    public void onDisconnect(PlayerQuitEvent event){

        Player player = event.getPlayer();
        String uuid = player.getUniqueId().toString();

        PersistentDataContainer playerInfo = player.getPersistentDataContainer();
        NamespacedKey balancekey = new NamespacedKey(DrestoriamMoney.getPlugin(), "moneyBalance");

        PlayerBank playerBank = bankSheet.get(uuid);
        BigDecimal balance = playerBank.getBankBalance();

        playerInfo.set(balancekey, PersistentDataType.STRING , balance.toString());

        bankSheet.remove(uuid);

    }

}
