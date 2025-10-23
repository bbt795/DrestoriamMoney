package com.drestoriam.drestoriammoney.commands;

import com.drestoriam.drestoriammoney.DrestoriamMoney;
import com.drestoriam.drestoriammoney.classes.Money;
import com.drestoriam.drestoriammoney.util.MoneyUtil;
import com.mordonia.mcore.MCoreAPI;
import org.bukkit.ChatColor;
import org.bukkit.NamespacedKey;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import static com.drestoriam.drestoriammoney.DrestoriamMoney.tag;

public class TaxPay implements CommandExecutor {

    private MCoreAPI mCoreAPI;

    public TaxPay(MCoreAPI mCoreAPI){

        this.mCoreAPI = mCoreAPI;

    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {

        if(!(sender instanceof Player)) {
            System.out.println("[DrestoriamMoney] Must send command via player");
            return true;
        }

        Player player = (Player) sender;
        String pUUID = player.getUniqueId().toString();
        String playerKingdom = mCoreAPI.getmPlayerManager().getPlayerMap().get(pUUID).getKingdom();
        List<String> configList =  DrestoriamMoney.getPlugin().getConfig().getStringList("citybanks." + playerKingdom + ".unpaid");
        String rpName = mCoreAPI.getmPlayerManager().getPlayerMap().get(pUUID).getmName().toString();

        if(!configList.contains(rpName)){

            player.sendMessage(tag + ChatColor.RED + "You have no overdue taxes!");
            return true;

        }

        BigDecimal taxAmount = new BigDecimal(DrestoriamMoney.getPlugin().getConfig().getString("citybanks." + playerKingdom + ".taxes"));
        Money money = MoneyUtil.inventoryCoins(player.getInventory());

        if(money.getInventoryBalance().compareTo(taxAmount) < 0){

            player.sendMessage(tag + ChatColor.RED + "You do not have the funds to pay your taxes. Try again later.");
            return true;

        }

        Inventory pInv = player.getInventory();
        int[] coinCounts = MoneyUtil.makeChange(money, taxAmount);
        ArrayList<ItemStack> moneyStack = MoneyUtil.getMoneyItems(money);

        MoneyUtil.coinHelper(coinCounts[0] - money.getDenom1(), moneyStack.get(0), pInv);
        MoneyUtil.coinHelper(coinCounts[1] - money.getDenom2(), moneyStack.get(1), pInv);
        MoneyUtil.coinHelper(coinCounts[2] - money.getDenom3(), moneyStack.get(2), pInv);
        MoneyUtil.coinHelper(coinCounts[3] - money.getDenom4(), moneyStack.get(3), pInv);

        BigDecimal cityBalance = new BigDecimal(DrestoriamMoney.getPlugin().getConfig().getString("citybanks." + playerKingdom + ".balance"));
        DrestoriamMoney.getPlugin().getConfig().set("citybanks." + playerKingdom + ".balance", cityBalance.add(taxAmount).toString());

        PersistentDataContainer playerInfo = player.getPersistentDataContainer();
        NamespacedKey taxKey = new NamespacedKey(DrestoriamMoney.getPlugin(), "taxTimer");
        playerInfo.set(taxKey, PersistentDataType.LONG, System.currentTimeMillis());

        DrestoriamMoney.getPlugin().getConfig().set("citybanks." + playerKingdom + ".unpaid", configList.remove(rpName));
        DrestoriamMoney.getPlugin().saveConfig();

        player.sendMessage(tag + ChatColor.GREEN + "Taxes successfully paid!");

        return true;
    }
}
