package com.drestoriam.drestoriammoney.events;

import com.drestoriam.drestoriammoney.classes.Money;
import com.drestoriam.drestoriammoney.classes.PlayerBank;
import com.drestoriam.drestoriammoney.util.MoneyUtil;
import com.mordonia.mcore.MCoreAPI;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Objects;

public class BankInteract implements Listener {

    private HashMap<String, PlayerBank> bankSheet;
    private final MCoreAPI mCoreAPI;
    private Inventory gui;
    private String guiTitle;

    public BankInteract(HashMap<String, PlayerBank> bankSheet, MCoreAPI mCoreAPI){

        this.bankSheet = bankSheet;
        this.mCoreAPI = mCoreAPI;

    }

    public void openInventory(Player player){

        String playerName = mCoreAPI.getmPlayerManager().getPlayerMap().get(player.getUniqueId().toString()).getmName().getName();
        guiTitle = playerName + "'s Bank";
        gui = Bukkit.createInventory(null, 54, ChatColor.DARK_PURPLE + guiTitle);

        PlayerBank playerBank = bankSheet.get(player.getUniqueId().toString());

        Money playerMoney = new Money(playerBank.getBankBalance());
        ArrayList<ItemStack> moneyList = MoneyUtil.getMoneyItems(playerMoney);

        gui.setContents(moneyList.toArray(new ItemStack[moneyList.size()]));

        player.openInventory(gui);

    }

    @EventHandler
    public void closeInventory(InventoryCloseEvent event){

        if(!event.getView().getTitle().contains("'s Bank")) return;
        Player player = (Player) event.getPlayer();

        Inventory bank = event.getInventory();
        Money bankMoney = MoneyUtil.inventoryCoins(bank);

        PlayerBank playerBank = bankSheet.get(player.getUniqueId().toString());
        playerBank.setBalance(bankMoney.getInventoryBalance());

    }

    @EventHandler
    public void bankInventory(InventoryClickEvent event){

        if(!event.getView().getTitle().contains("'s Bank")) return;

        ItemStack item = event.getCurrentItem();
        Money money = new Money(1, 1, 1, 1);
        ArrayList<ItemStack> moneyList = MoneyUtil.getMoneyItems(money);

        for(ItemStack coin: moneyList){

            if(item.getType() == null || item.getType() == Material.AIR) return;
            if(item.getType() == coin.getType() && Objects.equals(item.getItemMeta().getLore().get(0), coin.getItemMeta().getLore().get(0)) && item.getItemMeta().isUnbreakable()){

                return;

            }

        }

        event.setCancelled(true);

    }

}
