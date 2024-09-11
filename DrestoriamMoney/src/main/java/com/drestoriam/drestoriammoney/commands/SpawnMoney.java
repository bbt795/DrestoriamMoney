package com.drestoriam.drestoriammoney.commands;

import com.drestoriam.drestoriammoney.classes.Money;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import static com.drestoriam.drestoriammoney.DrestoriamMoney.tag;

public class SpawnMoney implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {

        if(!(sender instanceof Player)) {
            System.out.println("[DrestoriamMoney] Must send command via player");
            return true;
        }

        Player player = (Player) sender;

        if(player.hasPermission("dremoney.*")){

            Money adminPay = new Money(64, 64, 64, 64);
            adminPay.payOut(player);

        } else {

            player.sendMessage(tag + ChatColor.RED + "You do not have permission for this command");
            return true;

        }

        player.sendMessage(tag + ChatColor.GREEN + "Successfully spawned money");

        return true;
    }
}
