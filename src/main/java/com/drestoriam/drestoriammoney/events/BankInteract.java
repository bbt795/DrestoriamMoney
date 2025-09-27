package com.drestoriam.drestoriammoney.events;

import com.drestoriam.drestoriammoney.DrestoriamMoney;
import com.drestoriam.drestoriammoney.classes.Money;
import com.drestoriam.drestoriammoney.util.MoneyUtil;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.plugin.Plugin;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import static com.drestoriam.drestoriammoney.DrestoriamMoney.tag;

public class BankInteract implements Listener {

    private Inventory gui;
    private String guiTitle;
    private List<Player> withdrawMap = new ArrayList<>();
    private List<Player> depositMap = new ArrayList<>();


    public BankInteract(){

        guiTitle = "Bank Account";
        gui = Bukkit.createInventory(null, 9, ChatColor.DARK_PURPLE + guiTitle);
        initializeItems();

    }

    public void initializeItems(){

        ItemStack item1 = new ItemStack(Material.RED_DYE);
        ItemStack item2 = new ItemStack(Material.BLUE_DYE);
        ItemStack item3 = new ItemStack(Material.LIME_DYE);

        ItemMeta itemMeta1 = item1.getItemMeta();
        ItemMeta itemMeta2 = item2.getItemMeta();
        ItemMeta itemMeta3 = item3.getItemMeta();

        itemMeta1.setDisplayName(ChatColor.RED + "Withdraw");
        itemMeta2.setDisplayName(ChatColor.BLUE + "Balance");
        itemMeta3.setDisplayName(ChatColor.GREEN + "Deposit");

        item1.setItemMeta(itemMeta1);
        item2.setItemMeta(itemMeta2);
        item3.setItemMeta(itemMeta3);

        gui.setItem(3, item1);
        gui.setItem(4, item2);
        gui.setItem(5, item3);

    }

    public void openInventory(HumanEntity ent){
        ent.openInventory(gui);
    }

    @EventHandler
    public void onBankInteract(InventoryClickEvent e){

        if(!ChatColor.stripColor(e.getView().getTitle()).equalsIgnoreCase(ChatColor.stripColor(guiTitle))) return;

        ItemStack clickedItem = e.getCurrentItem();

        e.setCancelled(true);

        if (clickedItem == null || clickedItem.getType().isAir()) return;

        Player p = (Player) e.getWhoClicked();
        Plugin plugin = DrestoriamMoney.getPlugin();

        PersistentDataContainer playerInfo = p.getPersistentDataContainer();
        NamespacedKey balanceKey = new NamespacedKey(plugin, "moneyBalance");
        BigDecimal playerBalance = new BigDecimal(playerInfo.get(balanceKey, PersistentDataType.STRING));

        ItemStack[] guiContents = gui.getContents();
        ItemStack withdrawItem = guiContents[3];
        ItemStack balanceItem = guiContents[4];
        ItemStack depositItem = guiContents[5];

        if(clickedItem.equals(withdrawItem)) {

            //Withdrawal functionality in AsyncPlayerChatEvent
            withdrawMap.add(p);
            p.sendMessage(tag + ChatColor.DARK_GREEN + "How much would you like to withdraw?");
            p.closeInventory();

        } else if(clickedItem.equals(balanceItem)) {

            p.sendMessage(tag + ChatColor.GREEN + "Current Balance: $" + playerBalance);
            p.closeInventory();

        } else if(clickedItem.equals(depositItem)) {

            //Deposit functionality in AsyncPlayerChatEvent
            depositMap.add(p);
            p.sendMessage(tag + ChatColor.DARK_GREEN + "How much would you like to deposit?");
            p.closeInventory();

        }

    }

    @EventHandler
    public void onBankDrag(InventoryDragEvent e){

        if(e.getInventory().equals(gui)){
            e.setCancelled(true);
        }

    }

    @EventHandler
    public void onChatInput(AsyncPlayerChatEvent e){

        try {

            if (withdrawMap.contains(e.getPlayer())) {

                Plugin plugin = DrestoriamMoney.getPlugin();
                PersistentDataContainer playerInfo = e.getPlayer().getPersistentDataContainer();
                NamespacedKey balanceKey = new NamespacedKey(plugin, "moneyBalance");

                BigDecimal withdrawAmount = new BigDecimal(e.getMessage());
                BigDecimal currentBalance = new BigDecimal(playerInfo.get(balanceKey, PersistentDataType.STRING));

                if(withdrawAmount.compareTo(new BigDecimal("0.01")) < 0){

                    e.getPlayer().sendMessage(tag + ChatColor.RED + "Please enter a number greater than 1 cent.");
                    withdrawMap.remove(e.getPlayer());
                    e.setCancelled(true);
                    return;

                }

                if(withdrawAmount.compareTo(currentBalance) > 0){

                    e.getPlayer().sendMessage(tag + ChatColor.RED + "Please enter a number less than or equal to your balance.");
                    withdrawMap.remove(e.getPlayer());
                    e.setCancelled(true);
                    return;

                }

                Money money = new Money(withdrawAmount);
                playerInfo.set(balanceKey, PersistentDataType.STRING, currentBalance.subtract(withdrawAmount).toString());
                money.payOut(e.getPlayer());
                withdrawMap.remove(e.getPlayer());
                e.getPlayer().sendMessage(tag + ChatColor.GREEN + "Withdrawal successful!");

            } else if (depositMap.contains(e.getPlayer())) {

                Plugin plugin = DrestoriamMoney.getPlugin();
                PersistentDataContainer playerInfo = e.getPlayer().getPersistentDataContainer();
                NamespacedKey balanceKey = new NamespacedKey(plugin, "moneyBalance");

                BigDecimal depositAmount = new BigDecimal(e.getMessage());
                BigDecimal currentBalance = new BigDecimal(playerInfo.get(balanceKey, PersistentDataType.STRING));

                if(depositAmount.compareTo(new BigDecimal("0.01")) < 0){

                    e.getPlayer().sendMessage(tag + ChatColor.RED + "Please enter a number greater than 1 cent.");
                    depositMap.remove(e.getPlayer());
                    e.setCancelled(true);
                    return;

                }

                Inventory pInv = e.getPlayer().getInventory();
                Money money = MoneyUtil.inventoryCoins(pInv);

                if(depositAmount.compareTo(money.getInventoryBalance()) > 0){

                    e.getPlayer().sendMessage(tag + ChatColor.RED + "You do not have the amount you're looking to deposit.");
                    depositMap.remove(e.getPlayer());
                    e.setCancelled(true);
                    return;

                }

                int[] coinCounts = MoneyUtil.makeChange(money, depositAmount);
                ArrayList<ItemStack> moneyStack = MoneyUtil.getMoneyItems(money);

                MoneyUtil.coinHelper(coinCounts[0] - money.getDenom1(), moneyStack.get(0), pInv);
                MoneyUtil.coinHelper(coinCounts[1] - money.getDenom2(), moneyStack.get(1), pInv);
                MoneyUtil.coinHelper(coinCounts[2] - money.getDenom3(), moneyStack.get(2), pInv);
                MoneyUtil.coinHelper(coinCounts[3] - money.getDenom4(), moneyStack.get(3), pInv);

                playerInfo.set(balanceKey, PersistentDataType.STRING, currentBalance.add(depositAmount).toString());
                depositMap.remove(e.getPlayer());
                e.getPlayer().sendMessage(tag + ChatColor.GREEN + "Deposit successful!");

            }

        } catch (NumberFormatException ex){

            e.getPlayer().sendMessage(tag + ChatColor.RED + "Please enter a valid number");

            withdrawMap.removeIf(n -> (n == e.getPlayer()));
            depositMap.removeIf(n -> (n == e.getPlayer()));

        }

        e.setCancelled(true);

    }

}
