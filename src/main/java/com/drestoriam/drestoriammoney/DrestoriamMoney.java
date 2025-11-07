package com.drestoriam.drestoriammoney;

import com.drestoriam.drestoriammoney.classes.PlayerBank;
import com.drestoriam.drestoriammoney.commands.*;
import com.drestoriam.drestoriammoney.events.BankInteract;
import com.drestoriam.drestoriammoney.events.ConnectionEvents;
import com.drestoriam.drestoriammoney.events.MoneyInteract;
import com.drestoriam.drestoriammoney.events.TaxEvent;
import com.mordonia.mcore.MCore;
import com.mordonia.mcore.MCoreAPI;
import org.bukkit.ChatColor;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.ArrayList;
import java.util.HashMap;

public final class DrestoriamMoney extends JavaPlugin {

    private static DrestoriamMoney plugin;
    public static String tag = ChatColor.GOLD + "[" + ChatColor.DARK_GREEN + "DrestoriamMoney" + ChatColor.GOLD + "] ";
    private final HashMap<String, PlayerBank> bankSheet = new HashMap<>();
    private ArrayList<String> permissionList;

    @Override
    public void onEnable() {
        // Plugin startup logic

        plugin = this;
        MCoreAPI mCoreAPI = MCore.getPlugin(MCore.class).getmCoreAPI();

        getConfig().options().copyDefaults();
        saveDefaultConfig();

        permissionList = loadJobPermissions();

        getCommand("adminmoney").setExecutor(new SpawnMoney());
        getCommand("jobpay").setExecutor(new JobPay(permissionList));
        getCommand("pay").setExecutor(new PlayerPay(bankSheet));
        getCommand("adminpay").setExecutor(new AdminPay());
        getCommand("moneyreset").setExecutor(new MoneyReset(bankSheet));
        getCommand("citybank").setExecutor(new CityBank(mCoreAPI, bankSheet));
        getCommand("bank").setExecutor(new BankCommand(bankSheet, mCoreAPI));
        getCommand("taxes").setExecutor(new TaxPay(mCoreAPI, bankSheet));

        getServer().getPluginManager().registerEvents(new TaxEvent(mCoreAPI, bankSheet), this);
        getServer().getPluginManager().registerEvents(new BankInteract(bankSheet, mCoreAPI), this);
        getServer().getPluginManager().registerEvents(new MoneyInteract(), this);
        getServer().getPluginManager().registerEvents(new ConnectionEvents(bankSheet), this);

    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }

    public static DrestoriamMoney getPlugin(){

        return plugin;

    }

    private ArrayList<String> loadJobPermissions() {

        ArrayList<String> permissionList = new ArrayList<>();
        Plugin plugin = DrestoriamMoney.getPlugin();

        for (String key : plugin.getConfig().getConfigurationSection("jobs").getKeys(false)) {

            permissionList.add(plugin.getConfig().getString("jobs." + key + ".permission"));

        }

        return permissionList;

    }



}
