package com.drestoriam.drestoriammoney.commands;

import com.drestoriam.drestoriammoney.classes.Money;
import com.drestoriam.drestoriammoney.classes.PlayerBank;
import com.drestoriam.drestoriammoney.util.MoneyUtil;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;

import static com.drestoriam.drestoriammoney.DrestoriamMoney.tag;

public class MoneyReset implements CommandExecutor {

    private HashMap<String, PlayerBank> bankSheet;

    public MoneyReset(HashMap<String, PlayerBank> bankSheet){

        this.bankSheet = bankSheet;

    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {

        if(!(sender instanceof Player)) {
            System.out.println("[DrestoriamMoney] Must send command via player");
            return true;
        }

        Player player = (Player) sender;

        if(!player.hasPermission("dremoney.*")){

            player.sendMessage(tag + ChatColor.RED + "You do not have permission for this command");
            return true;

        }

        if(args.length != 1){

            player.sendMessage(tag + ChatColor.RED + "Please use /moneyreset (username)");
            return true;

        }

        Player target = Bukkit.getPlayerExact(args[0]);

        if(target == null){

            player.sendMessage(tag + ChatColor.RED + "Player is offline or does not exist. Please try again");
            return true;

        }

        PlayerBank playerBank = bankSheet.get(target.getUniqueId().toString());
        playerBank.setBalance(new BigDecimal("0.00"));

        //Need Add: Remove money from Inventory instead of Bank

        Inventory tInv = target.getInventory();
        Money money = MoneyUtil.inventoryCoins(tInv);

        ArrayList<ItemStack> moneyStack = MoneyUtil.getMoneyItems(money);

        MoneyUtil.coinHelper(-money.getDenom1(), moneyStack.get(0), tInv);
        MoneyUtil.coinHelper(-money.getDenom2(), moneyStack.get(1), tInv);
        MoneyUtil.coinHelper(-money.getDenom3(), moneyStack.get(2), tInv);
        MoneyUtil.coinHelper(-money.getDenom4(), moneyStack.get(3), tInv);

        player.sendMessage(tag + ChatColor.GREEN + "Successfully reset");

        return true;

    }
}
