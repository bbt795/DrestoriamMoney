package com.drestoriam.drestoriammoney.commands;

import com.drestoriam.drestoriammoney.DrestoriamMoney;
import com.drestoriam.drestoriammoney.classes.PlayerBank;
import com.mordonia.mcore.MCoreAPI;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import static com.drestoriam.drestoriammoney.DrestoriamMoney.tag;

public class CityBank implements CommandExecutor {

    private final MCoreAPI mCoreAPI;
    private HashMap<String, PlayerBank> bankSheet;

    public CityBank(MCoreAPI mCoreAPI, HashMap<String, PlayerBank> bankSheet){

        this.mCoreAPI = mCoreAPI;
        this.bankSheet = bankSheet;

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

        try{

            String playerKingdom = mCoreAPI.getmPlayerManager().getPlayerMap().get(player.getUniqueId().toString()).getKingdom();

            Plugin plugin = DrestoriamMoney.getPlugin(DrestoriamMoney.class);
            FileConfiguration config = plugin.getConfig();

            switch(args[0]){

                case "deposit": {

                    if(args.length != 2){

                        player.sendMessage(tag + ChatColor.RED + "Please use /citybank deposit (amount)");
                        return true;

                    }

                    PlayerBank playerBank = bankSheet.get(player.getUniqueId().toString());
                    BigDecimal bankBalance = playerBank.getBankBalance();

                    BigDecimal amount = new BigDecimal(args[1]);

                    if(bankBalance.compareTo(amount) < 0){

                        player.sendMessage(tag + ChatColor.RED + "You don't have enough to deposit that amount.");
                        return true;

                    }

                    BigDecimal cityBalance = new BigDecimal(config.getString("citybanks." + playerKingdom + ".balance"));

                    if (amount.compareTo(new BigDecimal("0")) > 0) {
                        config.set("citybanks." + playerKingdom + ".balance", amount.add(cityBalance).toString());
                        plugin.saveConfig();
                    } else {
                        player.sendMessage(tag + ChatColor.RED + "Please enter a positive or non-0 number");
                        break;
                    }

                    playerBank.setBalance(bankBalance.subtract(amount));

                    player.sendMessage(tag + ChatColor.GREEN + "City bank balance successfully updated");

                    break;
                }

                case "taxes": {

                    if(args.length != 2){

                        player.sendMessage(tag + ChatColor.RED + "Please use /citybank taxes (amount)");
                        return true;

                    }

                    BigDecimal amount = new BigDecimal(args[1]);

                    if (amount.compareTo(new BigDecimal("0")) >= 0) {
                        config.set("citybanks." + playerKingdom + ".taxes", amount.toString());
                        plugin.saveConfig();
                    } else {
                        player.sendMessage(tag + ChatColor.RED + "Please enter a positive number or 0");
                        break;
                    }

                    player.sendMessage(tag + ChatColor.GREEN + "Taxes successfully updated");

                    break;
                }

                case "unpaid": {

                    if(args.length != 1){

                        player.sendMessage(tag + ChatColor.RED + "Please use /citybank unpaid");
                        return true;

                    }

                    List<String> unpaidPlayers;
                    unpaidPlayers = (List<String>) config.getList("citybanks." + playerKingdom + ".unpaid");

                    if(unpaidPlayers.isEmpty()){

                        player.sendMessage(tag + ChatColor.GREEN + "All citizens have paid their taxes!");
                        break;

                    }

                    player.sendMessage(tag + "-=+ Tax Dodgers +=-");

                    for(String user : unpaidPlayers){

                        player.sendMessage(ChatColor.DARK_GREEN + "- " + user);

                    }

                    break;

                }

                case "create":

                    if(!player.hasPermission("dremoney.*")){

                        player.sendMessage(tag + ChatColor.RED + "You do not have permission to run this command");
                        break;

                    }

                    if(args.length != 2){

                        player.sendMessage(tag + ChatColor.RED + "Please use /citybank create (name)");
                        return true;

                    }

                    String cityName = args[1];

                    config.set("citybanks." + cityName, null);

                    config.set("citybanks." + cityName + ".balance", "0.0");
                    config.set("citybanks." + cityName + ".taxes", "0.0");
                    config.set("citybanks." + cityName + ".unpaid", new ArrayList<String>());

                    plugin.saveConfig();

                    player.sendMessage(tag + ChatColor.GREEN + "City bank successfully created");

                    break;

                default:
                    player.sendMessage(tag + ChatColor.RED + "Please use /citybank add/taxes/unpaid");
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
