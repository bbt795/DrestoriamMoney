package com.drestoriam.drestoriammoney.commands;

import com.drestoriam.drestoriammoney.DrestoriamMoney;
import com.mordonia.mcore.MCoreAPI;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

import java.math.BigDecimal;

import static com.drestoriam.drestoriammoney.DrestoriamMoney.tag;

public class CityBank implements CommandExecutor {

    private final MCoreAPI mCoreAPI;

    public CityBank(MCoreAPI mCoreAPI){

        this.mCoreAPI = mCoreAPI;

    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {

        if(!(sender instanceof Player)) {
            System.out.println("[DrestoriamMoney] Must send command via player");
            return true;
        }

        Player player = (Player) sender;

        if(!player.hasPermission("dremoney.cityleader.*")){

            player.sendMessage(tag + ChatColor.RED + "You need to be a city leader to execute this command");
            return true;

        }

        if(args.length != 2){

            player.sendMessage(tag + ChatColor.RED + "Please use /citybank add/taxes (amount)");
            return true;

        }

        try{

            String playerKingdom = mCoreAPI.getKingdomManager().getKingdomMap().get(player.getUniqueId().toString()).getName();

            Plugin plugin = DrestoriamMoney.getPlugin(DrestoriamMoney.class);
            FileConfiguration config = plugin.getConfig();

            switch(args[0]){

                case "add": {

                    BigDecimal amount = new BigDecimal(args[1]);
                    BigDecimal cityBalance = new BigDecimal(config.getString("citybanks." + playerKingdom + ".balance"));

                    if (amount.compareTo(new BigDecimal("0")) > 0) {
                        config.set("citybanks." + playerKingdom + ".balance", amount.add(cityBalance).toString());
                    } else {
                        player.sendMessage(tag + ChatColor.RED + "Please enter a positive or non-0 number");
                        break;
                    }

                    player.sendMessage(tag + ChatColor.GREEN + "City bank balance successfully updated");

                    break;
                }

                case "taxes": {

                    BigDecimal amount = new BigDecimal(args[1]);

                    if (amount.compareTo(new BigDecimal("0")) >= 0) {
                        config.set("citybanks." + playerKingdom + ".taxes", amount.toString());
                    } else {
                        player.sendMessage(tag + ChatColor.RED + "Please enter a positive number or 0");
                        break;
                    }

                    player.sendMessage(tag + ChatColor.GREEN + "Taxes successfully updated");

                    break;
                }

                case "create":

                    if(!player.hasPermission("dremoney.*")){

                        player.sendMessage(tag + ChatColor.RED + "You do not have permission to run this command");
                        break;

                    }

                    String cityName = args[1];

                    config.set("citybanks." + cityName, null);

                    config.set("citybanks." + cityName + ".balance", "0.0");
                    config.set("citybanks." + cityName + ".taxes", "0.0");

                    player.sendMessage(tag + ChatColor.GREEN + "City bank successfully created");

                    break;

                default:
                    player.sendMessage(tag + ChatColor.RED + "Please use /citybank add/taxes (amount)");
                    break;

            }

        } catch (NumberFormatException e) {

            player.sendMessage(tag + ChatColor.RED + "Please input a number");
            return true;

        } catch (NullPointerException e){

            player.sendMessage(tag + ChatColor.RED + "You need to be a part of a kingdom to run this command");
            return true;

        }

        return true;

    }

}
