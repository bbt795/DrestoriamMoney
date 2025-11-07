package com.drestoriam.drestoriammoney.commands;

import com.drestoriam.drestoriammoney.DrestoriamMoney;
import com.drestoriam.drestoriammoney.classes.Money;
import org.bukkit.ChatColor;
import org.bukkit.NamespacedKey;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.permissions.PermissionAttachmentInfo;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.plugin.Plugin;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import static com.drestoriam.drestoriammoney.DrestoriamMoney.tag;

public class JobPay implements CommandExecutor {

    private ArrayList<String> permissionList;

    public JobPay(ArrayList<String> permissionList) {

        this.permissionList = permissionList;

    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {

        if(!(sender instanceof Player)) {
            System.out.println("[DrestoriamMoney] Must send command via player");
            return true;
        }

        Player player = (Player) sender;

        String playerJob = "";

        if(player.hasPermission("dremoney.*")){

            for(PermissionAttachmentInfo permission: player.getEffectivePermissions()){

                String stringPermission = permission.getPermission();

                if(stringPermission.contains("dremoney.jobpay.")){

                    playerJob = stringPermission.substring(stringPermission.lastIndexOf('.') + 1);

                }

            }

        } else {

            for (String permission : permissionList) {

                if (player.hasPermission(permission)) {

                    playerJob = permission.substring(permission.lastIndexOf('.') + 1);

                }

            }

        }

        if(playerJob.isEmpty()){

            player.sendMessage(tag + ChatColor.RED + "Your job isn't properly registered or you don't have one!");
            return true;

        }

        Plugin plugin = DrestoriamMoney.getPlugin();

        PersistentDataContainer cooldown = player.getPersistentDataContainer();
        NamespacedKey key = new NamespacedKey(plugin, "jobcooldown");

        if(cooldown.has(key, PersistentDataType.LONG)){

            if(System.currentTimeMillis() < cooldown.get(key, PersistentDataType.LONG) + 604800000){

                player.sendMessage(tag + ChatColor.RED + "Your job is currently on cooldown. Please try again later");
                return true;

            }

        }

        try {

            List<Integer> pay = plugin.getConfig().getIntegerList("jobs." + playerJob + ".pay");
            Money payMoney = new Money(pay.get(0), pay.get(1), pay.get(2), pay.get(3));

            cooldown.set(key, PersistentDataType.LONG, System.currentTimeMillis());
            payMoney.payOut(player);

            player.sendMessage(tag + ChatColor.GREEN + "Successfully paid!");

        } catch (IndexOutOfBoundsException e){

            player.sendMessage(tag + ChatColor.RED + "Your job isn't properly registered or you don't have one!");
            return true;

        }

        return true;
    }
}
