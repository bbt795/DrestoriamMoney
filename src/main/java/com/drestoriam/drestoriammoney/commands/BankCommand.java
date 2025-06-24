package com.drestoriam.drestoriammoney.commands;

import com.drestoriam.drestoriammoney.events.BankInteract;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class BankCommand implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {

        if(!(sender instanceof Player)) {
            System.out.println("[DrestoriamMoney] Must send command via player");
            return true;
        }

        Player player = (Player) sender;

        BankInteract bankInteract = new BankInteract();
        bankInteract.openInventory(player);

        return true;
    }

}
