package com.drestoriam.drestoriammoney.events;

import com.drestoriam.drestoriammoney.DrestoriamMoney;
import com.drestoriam.drestoriammoney.classes.Money;
import com.drestoriam.drestoriammoney.classes.banks.PlayerBank;
import com.drestoriam.drestoriammoney.util.MoneyUtil;
import com.mordonia.mcore.MCoreAPI;
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

        Double bankBalance = playerInfo.get(balanceKey, PersistentDataType.DOUBLE);
        Long taxTime = playerInfo.get(taxKey, PersistentDataType.LONG);

        if(bankBalance == null){

            playerInfo.set(balanceKey, PersistentDataType.DOUBLE, 0.0);

        }

        if (taxTime == null){

            playerInfo.set(taxKey, PersistentDataType.LONG, System.currentTimeMillis());
            return;

        }

        if(!((System.currentTimeMillis() - taxTime) > 604800000L))
            return;


        Player player = event.getPlayer();
        String playerKingdom = this.mCoreAPI.getKingdomManager().getKingdomMap().get(player.getUniqueId().toString()).getName();

        double taxAmount = plugin.getConfig().getDouble("citybanks." + playerKingdom + ".taxes");
        double cityBalance = plugin.getConfig().getDouble("citybanks." + playerKingdom + ".balance");

        PlayerBank pBank = new PlayerBank(player);
        double balance = pBank.getBalance();

        Inventory pInv = player.getInventory();

        if(balance >= taxAmount){

            pBank.setBalance(balance - taxAmount);
            plugin.getConfig().set("citybanks." + playerKingdom + ".balance", cityBalance + taxAmount);
            playerInfo.set(taxKey, PersistentDataType.LONG, System.currentTimeMillis());
            player.sendMessage(tag + ChatColor.GREEN + "Taxes successfully paid!");
            return;

        }

        Money money = MoneyUtil.inventoryCoins(pInv);

        if(money.getBalance() < taxAmount){

            player.sendMessage(tag + ChatColor.RED + "You do not have the funds to pay your taxes. OFF WITH YOUR HEAD!!!");
            return;

        }

        int[] coinCounts = MoneyUtil.makeChange(money, taxAmount);
        ItemStack[] moneyStack = MoneyUtil.getMoneyItems(money);

        MoneyUtil.coinHelper(coinCounts[0] - money.getDenom1(), moneyStack[0], pInv);
        MoneyUtil.coinHelper(coinCounts[1] - money.getDenom2(), moneyStack[1], pInv);
        MoneyUtil.coinHelper(coinCounts[2] - money.getDenom3(), moneyStack[2], pInv);
        MoneyUtil.coinHelper(coinCounts[3] - money.getDenom4(), moneyStack[3], pInv);

        plugin.getConfig().set("citybanks." + playerKingdom + ".balance", cityBalance + plugin.getConfig().getDouble("citybanks." + playerKingdom + ".taxes"));
        playerInfo.set(taxKey, PersistentDataType.LONG, System.currentTimeMillis());
        player.sendMessage(tag + ChatColor.GREEN + "Taxes successfully paid!");

    }



}