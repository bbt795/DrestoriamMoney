package com.drestoriam.drestoriammoney;

import com.drestoriam.drestoriammoney.commands.*;
import com.drestoriam.drestoriammoney.events.BankInteract;
import com.drestoriam.drestoriammoney.events.ConnectionEvent;
import com.drestoriam.drestoriammoney.events.MoneyInteract;
import com.drestoriam.drestoriammoney.events.TaxEvent;
import com.drestoriam.drestoriammoney.util.PlayerBankUtil;
import com.mordonia.mcore.MCore;
import com.mordonia.mcore.MCoreAPI;
import org.bukkit.ChatColor;
import org.bukkit.plugin.java.JavaPlugin;

public final class DrestoriamMoney extends JavaPlugin {

    private static DrestoriamMoney plugin;
    public static String tag = ChatColor.GOLD + "[" + ChatColor.DARK_GREEN + "DrestoriamMoney" + ChatColor.GOLD + "] ";

    @Override
    public void onEnable() {
        // Plugin startup logic

        plugin = this;
        MCoreAPI mCoreAPI = MCore.getPlugin(MCore.class).getmCoreAPI();

        getConfig().options().copyDefaults();
        saveDefaultConfig();

        PlayerBankUtil bankUtil = new PlayerBankUtil(this);

        getCommand("adminmoney").setExecutor(new SpawnMoney());
        getCommand("jobpay").setExecutor(new JobPay());
        getCommand("pay").setExecutor(new PlayerPay());
        getCommand("adminpay").setExecutor(new AdminPay());
        getCommand("moneyreset").setExecutor(new MoneyReset());
        getCommand("citybank").setExecutor(new CityBank(mCoreAPI));
        getCommand("bank").setExecutor(new BankCommand(bankUtil));
        getCommand("taxes").setExecutor(new TaxPay(mCoreAPI));

        getServer().getPluginManager().registerEvents(new TaxEvent(mCoreAPI), this);
        getServer().getPluginManager().registerEvents(new BankInteract(bankUtil), this);
        getServer().getPluginManager().registerEvents(new MoneyInteract(), this);
        getServer().getPluginManager().registerEvents(new ConnectionEvent(bankUtil), this);

    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }

    public static DrestoriamMoney getPlugin(){

        return plugin;

    }

}
