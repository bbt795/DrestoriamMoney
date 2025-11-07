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

public class PlayerPay implements CommandExecutor {

    private HashMap<String, PlayerBank> bankSheet;

    public PlayerPay(HashMap<String, PlayerBank> bankSheet){

        this.bankSheet = bankSheet;

    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {

        if(!(sender instanceof Player)) {
            System.out.println("[DrestoriamMoney] Must send command via player");
            return true;
        }

        Player player = (Player) sender;

        if(args.length != 2){

            player.sendMessage(tag + ChatColor.RED + "Invalid Format. Please use /pay (username) (amount)");
            return true;

        }

        Player target = Bukkit.getPlayerExact(args[0]);

        if(target == null){

            player.sendMessage(tag + ChatColor.RED + "Player is offline or does not exist. Please try again");
            return true;

        }

        try {

            PlayerBank pb = bankSheet.get(player.getUniqueId().toString());
            BigDecimal balance = pb.getBankBalance();

            BigDecimal payAmount = new BigDecimal(args[1]);

            if(balance.compareTo(payAmount) > 0){

                Money money = new Money(payAmount);
                money.payOut(target);
                pb.setBalance(balance.subtract(payAmount));

            }

            Money money = MoneyUtil.inventoryCoins(player.getInventory());

            if(money.getInventoryBalance().compareTo(payAmount) < 0){

                player.sendMessage(tag + ChatColor.RED + "You do not have the funds to pay this player. Try again later.");
                return true;

            }

            Inventory pInv = player.getInventory();
            int[] coinCounts = MoneyUtil.makeChange(money, payAmount);
            ArrayList<ItemStack> moneyStack = MoneyUtil.getMoneyItems(money);

            MoneyUtil.coinHelper(coinCounts[0] - money.getDenom1(), moneyStack.get(0), pInv);
            MoneyUtil.coinHelper(coinCounts[1] - money.getDenom2(), moneyStack.get(1), pInv);
            MoneyUtil.coinHelper(coinCounts[2] - money.getDenom3(), moneyStack.get(2), pInv);
            MoneyUtil.coinHelper(coinCounts[3] - money.getDenom4(), moneyStack.get(3), pInv);

            Money payOut = new Money(payAmount);
            payOut.payOut(target);


        } catch (NumberFormatException e){

            player.sendMessage(tag + ChatColor.RED + "Please provide a number and try again");
            return true;

        }

        player.sendMessage(tag + ChatColor.GREEN + "Successfully paid player");

        return true;

    }

}
