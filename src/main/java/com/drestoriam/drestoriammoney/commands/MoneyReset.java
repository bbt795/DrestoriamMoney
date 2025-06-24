package com.drestoriam.drestoriammoney.commands;

import com.drestoriam.drestoriammoney.classes.banks.PlayerBank;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.math.BigDecimal;

import static com.drestoriam.drestoriammoney.DrestoriamMoney.tag;

public class MoneyReset implements CommandExecutor {

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

        PlayerBank tBank = new PlayerBank(target);
        tBank.setBalance(new BigDecimal("0.00"));

        player.sendMessage(tag + ChatColor.GREEN + "Successfully reset");

        return true;

    }
}
