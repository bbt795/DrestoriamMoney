package com.drestoriam.drestoriammoney.events;

import com.drestoriam.drestoriammoney.DrestoriamMoney;
import com.drestoriam.drestoriammoney.classes.Money;
import com.drestoriam.drestoriammoney.classes.banks.PlayerBank;
import com.drestoriam.drestoriammoney.util.MoneyUtil;
import com.mordonia.mcore.MCoreAPI;
import com.mordonia.mcore.data.citydata.MCity;
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

import static com.drestoriam.drestoriammoney.DrestoriamMoney.tag;

public class TaxEvent implements Listener {

    private final MCoreAPI mCoreAPI;

    public TaxEvent(MCoreAPI mCoreAPI){

        this.mCoreAPI = mCoreAPI;

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

        if(!((System.currentTimeMillis() - taxTime) > 604800000L))
            return;


        Player player = event.getPlayer();
        MCity playerKingdom = this.mCoreAPI.getKingdomManager().getKingdomMap().get(player.getUniqueId().toString());
        String kingdomName;

        if(playerKingdom == null || playerKingdom.getName().equals("Nomad")){

            playerInfo.set(taxKey, PersistentDataType.LONG, System.currentTimeMillis());
            player.sendMessage(tag + ChatColor.BLUE + "You do not belong to a kingdom, therefore, you owe no taxes!");
            return;

        } else {

            kingdomName = playerKingdom.getName();

        }

        BigDecimal taxAmount = new BigDecimal(plugin.getConfig().getString("citybanks." + kingdomName + ".taxes"));
        BigDecimal cityBalance = new BigDecimal(plugin.getConfig().getString("citybanks." + kingdomName + ".balance"));

        PlayerBank pBank = new PlayerBank(player);
        BigDecimal balance = pBank.getBankBalance();

        Inventory pInv = player.getInventory();

        if(balance.compareTo(taxAmount) >= 0){

            pBank.setBalance(balance.subtract(taxAmount));
            plugin.getConfig().set("citybanks." + kingdomName + ".balance", cityBalance.add(taxAmount).toString());
            playerInfo.set(taxKey, PersistentDataType.LONG, System.currentTimeMillis());
            player.sendMessage(tag + ChatColor.GREEN + "Taxes successfully paid!");
            return;

        }

        Money money = MoneyUtil.inventoryCoins(pInv);

        if(money.getInventoryBalance().compareTo(taxAmount) < 0){

            player.sendMessage(tag + ChatColor.RED + "You do not have the funds to pay your taxes. OFF WITH YOUR HEAD!!!");
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
        player.sendMessage(tag + ChatColor.GREEN + "Taxes successfully paid!");

    }



}
