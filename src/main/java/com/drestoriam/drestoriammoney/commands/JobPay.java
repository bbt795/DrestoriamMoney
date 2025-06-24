package com.drestoriam.drestoriammoney.commands;

import com.drestoriam.drestoriammoney.DrestoriamMoney;
import com.drestoriam.drestoriammoney.classes.Money;
import org.bukkit.ChatColor;
import org.bukkit.NamespacedKey;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.plugin.Plugin;

import java.util.ArrayList;

import static com.drestoriam.drestoriammoney.DrestoriamMoney.tag;

public class JobPay implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {

        if(!(sender instanceof Player)) {
            System.out.println("[DrestoriamMoney] Must send command via player");
            return true;
        }


        Player player = (Player) sender;
        ArrayList<String> permissionList = new ArrayList<>();
        Plugin plugin = DrestoriamMoney.getPlugin();

        for(String key: plugin.getConfig().getConfigurationSection("jobs").getKeys(false)){

            for(String subKey: plugin.getConfig().getConfigurationSection("jobs." + key).getKeys(false)){

                if(subKey.equals("permission")){

                    permissionList.add(plugin.getConfig().getString("jobs." + key + "." + subKey));

                }

            }

        }

        String playerJob = "";

        for(String permission: permissionList){

            if(player.hasPermission(permission)){

                playerJob = permission.substring(permission.lastIndexOf('.') + 1);

            }

        }

        if(playerJob.isEmpty()){

            player.sendMessage(tag + ChatColor.RED + "Your job isn't properly registered or you don't have one!");
            return true;

        }

        String payPath = "jobs." + playerJob + ".pay.";

        Money payMoney = new Money(plugin.getConfig().getInt(payPath + "1st"), plugin.getConfig().getInt(payPath + "2nd"),
                plugin.getConfig().getInt(payPath + "3rd"), plugin.getConfig().getInt(payPath + "4th"));

        PersistentDataContainer cooldown = player.getPersistentDataContainer();
        NamespacedKey key = new NamespacedKey(plugin, "jobcooldown");

        if(cooldown.has(key, PersistentDataType.LONG)){

            if(System.currentTimeMillis() < cooldown.get(key, PersistentDataType.LONG) + 604800000){

                player.sendMessage(tag + ChatColor.RED + "Your job is currently on cooldown. Please try again later");
                return true;

            }

        }

        cooldown.set(key, PersistentDataType.LONG, System.currentTimeMillis());
        payMoney.payOut(player);

        player.sendMessage(tag + ChatColor.GREEN + "Successfully paid!");

        return true;
    }
}
