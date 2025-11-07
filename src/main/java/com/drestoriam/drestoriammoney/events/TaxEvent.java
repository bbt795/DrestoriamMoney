package com.drestoriam.drestoriammoney.events;

import com.drestoriam.drestoriammoney.DrestoriamMoney;
import com.drestoriam.drestoriammoney.classes.Money;
import com.drestoriam.drestoriammoney.classes.PlayerBank;
import com.drestoriam.drestoriammoney.util.MoneyUtil;
import com.mordonia.mcore.MCoreAPI;
import com.mordonia.mcore.data.palyerdata.MPlayer;
import org.bukkit.ChatColor;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.plugin.Plugin;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import static com.drestoriam.drestoriammoney.DrestoriamMoney.tag;

public class TaxEvent implements Listener {

    private final MCoreAPI mCoreAPI;
    private HashMap<String, PlayerBank> bankSheet;

    public TaxEvent(MCoreAPI mCoreAPI, HashMap<String, PlayerBank> bankSheet){

        this.mCoreAPI = mCoreAPI;
        this.bankSheet = bankSheet;

    }

    @EventHandler
    public void onJoin(PlayerJoinEvent event){

        Plugin plugin = DrestoriamMoney.getPlugin();
        PersistentDataContainer playerInfo = event.getPlayer().getPersistentDataContainer();
        NamespacedKey balanceKey = new NamespacedKey(plugin, "moneyBalance");
        NamespacedKey taxKey = new NamespacedKey(plugin, "taxTimer");

        if(!playerInfo.has(balanceKey, PersistentDataType.STRING)){

            playerInfo.set(balanceKey, PersistentDataType.STRING, "0.00");

        }

        Long taxTime = playerInfo.get(taxKey, PersistentDataType.LONG);

        if (taxTime == null){

            playerInfo.set(taxKey, PersistentDataType.LONG, System.currentTimeMillis());
            return;

        }

        //604800000ms = 1 week
        if(!((System.currentTimeMillis() - taxTime) > 604800000L))
            return;


        Player player = event.getPlayer();
        MPlayer playerKingdom = this.mCoreAPI.getmPlayerManager().getPlayerMap().get(player.getUniqueId().toString());
        String kingdomName;

        if(playerKingdom == null || playerKingdom.getKingdom().equalsIgnoreCase("Nomad")){

            playerInfo.set(taxKey, PersistentDataType.LONG, System.currentTimeMillis());
            player.sendMessage(tag + ChatColor.BLUE + "You do not belong to a kingdom, therefore, you owe no taxes!");
            return;

        } else {

            kingdomName = playerKingdom.getKingdom();

        }

        BigDecimal taxAmount = new BigDecimal(plugin.getConfig().getString("citybanks." + kingdomName + ".taxes"));
        BigDecimal cityBalance = new BigDecimal(plugin.getConfig().getString("citybanks." + kingdomName + ".balance"));
        List<String> configList =  plugin.getConfig().getStringList("citybanks." + playerKingdom + ".unpaid");

        String rpName = this.mCoreAPI.getmPlayerManager().getPlayerMap().get(player.getUniqueId().toString()).getmName().toString();

        PlayerBank pBank = bankSheet.get(player.getUniqueId().toString());
        BigDecimal balance = pBank.getBankBalance();

        if(balance.compareTo(taxAmount) >= 0){

            pBank.setBalance(balance.subtract(taxAmount));
            plugin.getConfig().set("citybanks." + kingdomName + ".balance", cityBalance.add(taxAmount).toString());
            playerInfo.set(taxKey, PersistentDataType.LONG, System.currentTimeMillis());
            plugin.saveConfig();
            player.sendMessage(tag + ChatColor.GREEN + "Taxes successfully paid!");

            if(configList.contains(rpName)){

                DrestoriamMoney.getPlugin().getConfig().set("citybanks." + playerKingdom + ".unpaid", configList.remove(rpName));

            }

            return;

        }

        Inventory pInv = player.getInventory();
        Money money = MoneyUtil.inventoryCoins(pInv);

        if(money.getInventoryBalance().compareTo(taxAmount) < 0){

            player.sendMessage(tag + ChatColor.RED + "You do not have the funds to pay your taxes. Your leader will be alerted.");

            if(configList.contains(rpName)) return;

            configList.add(rpName);
            plugin.getConfig().set("citybanks." + playerKingdom + ".unpaid", configList);
            return;

        }

        int[] coinCounts = MoneyUtil.makeChange(money, taxAmount);
        ArrayList<ItemStack> moneyStack = MoneyUtil.getMoneyItems(money);

        MoneyUtil.coinHelper(coinCounts[0] - money.getDenom1(), moneyStack.get(0), pInv);
        MoneyUtil.coinHelper(coinCounts[1] - money.getDenom2(), moneyStack.get(1), pInv);
        MoneyUtil.coinHelper(coinCounts[2] - money.getDenom3(), moneyStack.get(2), pInv);
        MoneyUtil.coinHelper(coinCounts[3] - money.getDenom4(), moneyStack.get(3), pInv);

        plugin.getConfig().set("citybanks." + kingdomName + ".balance", cityBalance.add(taxAmount).toString());
        playerInfo.set(taxKey, PersistentDataType.LONG, System.currentTimeMillis());

        if(configList.contains(rpName)){

            DrestoriamMoney.getPlugin().getConfig().set("citybanks." + playerKingdom + ".unpaid", configList.remove(rpName));

        }

        plugin.saveConfig();
        player.sendMessage(tag + ChatColor.GREEN + "Taxes successfully paid!");

    }

}
