package com.drestoriam.drestoriammoney.commands;

import com.drestoriam.drestoriammoney.classes.PlayerBank;
import com.drestoriam.drestoriammoney.events.BankInteract;
import com.mordonia.mcore.MCoreAPI;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.HashMap;

public class BankCommand implements CommandExecutor {

    private HashMap<String, PlayerBank> bankSheet;
    private final MCoreAPI mCoreAPI;

    public BankCommand(HashMap<String, PlayerBank> bankSheet, MCoreAPI mCoreAPI){

        this.bankSheet = bankSheet;
        this.mCoreAPI = mCoreAPI;

    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {

        if(!(sender instanceof Player)) {
            System.out.println("[DrestoriamMoney] Must send command via player");
            return true;
        }

        Player player = (Player) sender;

        BankInteract bankInteract = new BankInteract(bankSheet, mCoreAPI);
        bankInteract.openInventory(player);

        return true;
    }

}
