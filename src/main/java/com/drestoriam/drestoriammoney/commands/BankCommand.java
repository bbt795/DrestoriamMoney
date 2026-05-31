package com.drestoriam.drestoriammoney.commands;

import com.drestoriam.drestoriammoney.DrestoriamMoney;
import com.drestoriam.drestoriammoney.events.BankInteract;
import com.mordonia.mcore.MCoreAPI;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import static com.drestoriam.drestoriammoney.DrestoriamMoney.tag;

public class BankCommand implements CommandExecutor {

    private final MCoreAPI mCoreAPI;

    public BankCommand(MCoreAPI mCoreAPI){

        this.mCoreAPI = mCoreAPI;

    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {

        if(!(sender instanceof Player)) {
            System.out.println("[DrestoriamMoney] Must send command via player");
            return true;
        }

        Player player = (Player) sender;

        if(args.length != 0){

            player.sendMessage(tag + ChatColor.RED + "Please use /bank");
            return true;

        }

        BankInteract bankInteract = new BankInteract(mCoreAPI);
        bankInteract.openInventory(player);

        return true;
    }

}
