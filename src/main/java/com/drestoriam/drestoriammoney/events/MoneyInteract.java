package com.drestoriam.drestoriammoney.events;

import com.drestoriam.drestoriammoney.classes.Money;
import com.drestoriam.drestoriammoney.util.MoneyUtil;
import org.bukkit.entity.GlowItemFrame;
import org.bukkit.entity.ItemFrame;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.Objects;


public class MoneyInteract implements Listener {

    @EventHandler
    public void onMoneyMove(InventoryClickEvent e){

        Player player = (Player) e.getWhoClicked();

        if(player.hasPermission("dremoney.*")) return;

        ItemStack item = e.getCurrentItem();
        Money money = new Money(1, 1, 1, 1);
        ArrayList<ItemStack> moneyList = MoneyUtil.getMoneyItems(money);

        if(item == null || item.getType().isAir()) return;

        if(!item.hasItemMeta() || item.getItemMeta() == null) return;

        if(!item.getItemMeta().hasLore()) return;

        for(ItemStack coin: moneyList){

            if (item.getType() == coin.getType() && Objects.equals(item.getItemMeta().getLore().get(0), coin.getItemMeta().getLore().get(0)) && item.getItemMeta().isUnbreakable()){

                if(player.getOpenInventory().getType() == InventoryType.CRAFTING || e.getView().getTitle().contains("'s Bank")) return;

                e.setCancelled(true);

            }

        }

    }

    @EventHandler
    public void dropMoney(PlayerDropItemEvent e){

        Player player = e.getPlayer();

        if(player.hasPermission("dremoney.*")) return;

        ItemStack item = e.getItemDrop().getItemStack();
        Money money = new Money(1, 1, 1, 1);
        ArrayList<ItemStack> moneyList = MoneyUtil.getMoneyItems(money);

        if(!item.hasItemMeta() || item.getItemMeta() == null) return;

        if(!item.getItemMeta().hasLore()) return;

        for(ItemStack coin: moneyList){

            if (item.getType() == coin.getType() && Objects.equals(item.getItemMeta().getLore().get(0), coin.getItemMeta().getLore().get(0)) && item.getItemMeta().isUnbreakable()){

                e.setCancelled(true);

            }

        }

    }

    @EventHandler
    public void frameMoney(PlayerInteractEntityEvent e){

        Player player = e.getPlayer();

        if(player.hasPermission("dremoney.*")) return;

        ItemStack item = e.getPlayer().getInventory().getItemInMainHand();
        Money money = new Money(1, 1, 1, 1);
        ArrayList<ItemStack> moneyList = MoneyUtil.getMoneyItems(money);

        if(item.getType().isAir()) item = e.getPlayer().getInventory().getItemInOffHand();

        if(item.getType().isAir()) return;

        if(!item.hasItemMeta() || item.getItemMeta() == null) return;

        if(!item.getItemMeta().hasLore()) return;

        if(!(e.getRightClicked() instanceof ItemFrame) && !(e.getRightClicked() instanceof GlowItemFrame)) return;

        for(ItemStack coin: moneyList){

            if (item.getType() == coin.getType() && Objects.equals(item.getItemMeta().getLore().get(0), coin.getItemMeta().getLore().get(0)) && item.getItemMeta().isUnbreakable()){

                e.setCancelled(true);

            }

        }

    }

}
