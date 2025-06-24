package com.drestoriam.drestoriammoney.util;

import com.drestoriam.drestoriammoney.DrestoriamMoney;
import com.drestoriam.drestoriammoney.classes.Money;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.Material;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.Plugin;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.Objects;

public class MoneyUtil {

    public static ItemStack[] getMoneyItems(Money money){

        Plugin plugin = DrestoriamMoney.getPlugin(DrestoriamMoney.class);

        ItemStack firstDenom = new ItemStack(Material.RED_DYE,money.getDenom1());
        ItemStack secondDenom = new ItemStack(Material.ORANGE_DYE,money.getDenom2());
        ItemStack thirdDenom = new ItemStack(Material.YELLOW_DYE,money.getDenom3());
        ItemStack fourthDenom = new ItemStack(Material.GREEN_DYE,money.getDenom4());

        ItemMeta firstDenomItemMeta = firstDenom.getItemMeta();
        ItemMeta secondDenomItemMeta = firstDenom.getItemMeta();
        ItemMeta thirdDenomItemMeta = firstDenom.getItemMeta();
        ItemMeta fourthDenomItemMeta = firstDenom.getItemMeta();

        firstDenomItemMeta.setItemName(ChatColor.of("#33463b") + plugin.getConfig().getString("money.name1"));
        secondDenomItemMeta.setItemName(ChatColor.of("#502B1E") + plugin.getConfig().getString("money.name2"));
        thirdDenomItemMeta.setItemName(ChatColor.of("#C0C0C0") + plugin.getConfig().getString("money.name3"));
        fourthDenomItemMeta.setItemName(ChatColor.of("#FFD700") + plugin.getConfig().getString("money.name4"));

        firstDenomItemMeta.setLore(Collections.singletonList("$0.01"));
        secondDenomItemMeta.setLore(Collections.singletonList("$1.00"));
        thirdDenomItemMeta.setLore(Collections.singletonList("$100.00"));
        fourthDenomItemMeta.setLore(Collections.singletonList("$1000.00"));

        firstDenomItemMeta.setUnbreakable(true);
        secondDenomItemMeta.setUnbreakable(true);
        thirdDenomItemMeta.setUnbreakable(true);
        fourthDenomItemMeta.setUnbreakable(true);

        firstDenomItemMeta.addItemFlags(ItemFlag.HIDE_UNBREAKABLE);
        secondDenomItemMeta.addItemFlags(ItemFlag.HIDE_UNBREAKABLE);
        thirdDenomItemMeta.addItemFlags(ItemFlag.HIDE_UNBREAKABLE);
        fourthDenomItemMeta.addItemFlags(ItemFlag.HIDE_UNBREAKABLE);

        firstDenom.setItemMeta(firstDenomItemMeta);
        secondDenom.setItemMeta(secondDenomItemMeta);
        thirdDenom.setItemMeta(thirdDenomItemMeta);
        fourthDenom.setItemMeta(fourthDenomItemMeta);

        return new ItemStack[]{firstDenom, secondDenom, thirdDenom, fourthDenom};

    }

    public static void coinHelper(int coinCount, ItemStack moneyStack, Inventory pInv){

        if(coinCount > 0){

            moneyStack.setAmount(coinCount);
            pInv.addItem(moneyStack);

        } else if (coinCount < 0){

            moneyStack.setAmount(coinCount * -1);
            pInv.removeItem(moneyStack);

        }

    }

    public static Money inventoryCoins(Inventory pInv){

        Money money = new Money(0,0,0,0);
        ItemStack[] moneyStack = MoneyUtil.getMoneyItems(money);
        ItemStack[] pInvContent = pInv.getContents();

        for(ItemStack item: pInvContent) {

            if(item == null || item.getType() == Material.AIR) continue;

            for (int i = 0; i < moneyStack.length; i++) {

                if(item.getType() != moneyStack[i].getType()) continue;

                if (Objects.equals(item.getItemMeta().getLore().get(0), moneyStack[i].getItemMeta().getLore().get(0)) && item.getItemMeta().isUnbreakable()){

                    switch (i) {

                        case 0:
                            money.setDenom1(money.getDenom1() + item.getAmount());
                            break;

                        case 1:
                            money.setDenom2(money.getDenom2() + item.getAmount());
                            break;

                        case 2:
                            money.setDenom3(money.getDenom3() + item.getAmount());
                            break;

                        case 3:
                            money.setDenom4(money.getDenom4() + item.getAmount());
                            break;
                    }

                }

            }
        }

        return money;

    }

    public static int[] makeChange(Money money, BigDecimal paymentAmount){

        int coin1Count = money.getDenom1();
        int coin2Count = money.getDenom2();
        int coin3Count = money.getDenom3();
        int coin4Count = money.getDenom4();

        while(paymentAmount.compareTo(new BigDecimal("0")) > 0) {

            if (new BigDecimal(coin1Count * 0.01).compareTo(paymentAmount) >= 0) {

                coin1Count -= 1;
                paymentAmount = paymentAmount.subtract(new BigDecimal("0.01"));

            } else if (new BigDecimal(coin2Count).compareTo(paymentAmount) >= 0) {

                coin2Count -= 1;
                paymentAmount = paymentAmount.subtract(new BigDecimal("1"));

            } else if (new BigDecimal(coin3Count * 100).compareTo(paymentAmount) >= 0) {

                coin3Count -= 1;
                paymentAmount = paymentAmount.subtract(new BigDecimal("100"));

            } else if (new BigDecimal(coin4Count * 1000).compareTo(paymentAmount) >= 0) {

                coin4Count -= 1;
                paymentAmount = paymentAmount.subtract(new BigDecimal("1000"));

            }

        }

        if(paymentAmount.compareTo(new BigDecimal("0")) < 0){

            BigDecimal[] bigArray = {new BigDecimal("1000"), new BigDecimal("100"), new BigDecimal("1"), new BigDecimal("0.01")};

            for(BigDecimal denom: bigArray){

                switch(denom.toString()){

                    case "0.01":

                        while(paymentAmount.compareTo(new BigDecimal("0")) < 0) {
                            coin1Count++;
                            paymentAmount = paymentAmount.add(denom);
                        }

                        break;

                    case "1":

                        while(paymentAmount.compareTo(new BigDecimal("-100")) > 0 && paymentAmount.compareTo(new BigDecimal("-1")) <= 0){

                            coin2Count++;
                            paymentAmount = paymentAmount.add(denom);

                        }

                        break;

                    case "100":

                        while(paymentAmount.compareTo(new BigDecimal("-1000")) > 0 && paymentAmount.compareTo(new BigDecimal("-100")) <= 0){

                            coin3Count++;
                            paymentAmount = paymentAmount.add(denom);

                        }

                        break;

                    case "1000":

                        while(paymentAmount.compareTo(new BigDecimal("-1000")) <= 0){

                            coin4Count++;
                            paymentAmount = paymentAmount.add(denom);

                        }

                        break;
                }

            }

        }
        return new int[] {coin1Count, coin2Count, coin3Count, coin4Count};

    }

}
