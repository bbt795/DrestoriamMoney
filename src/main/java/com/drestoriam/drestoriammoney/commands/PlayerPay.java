package com.drestoriam.drestoriammoney.commands;

import com.drestoriam.drestoriammoney.classes.Money;
import com.drestoriam.drestoriammoney.classes.banks.PlayerBank;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.math.BigDecimal;

import static com.drestoriam.drestoriammoney.DrestoriamMoney.tag;

public class PlayerPay implements CommandExecutor {

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

            PlayerBank pb = new PlayerBank(player);
            BigDecimal balance = pb.getBankBalance();
            BigDecimal payAmount = new BigDecimal(args[1]);

            if(balance.compareTo(payAmount) > 0){

                Money money = new Money(payAmount);
                money.payOut(target);
                pb.setBalance(balance.subtract(payAmount));

            } else {

                player.sendMessage(tag + ChatColor.RED + "You can't have negative balance");
                return true;

            }

        } catch (NumberFormatException e){

            player.sendMessage(tag + ChatColor.RED + "Please provide a number and try again");
            return true;

        }

        player.sendMessage(tag + ChatColor.GREEN + "Successfully paid player");

        return true;

    }

}
