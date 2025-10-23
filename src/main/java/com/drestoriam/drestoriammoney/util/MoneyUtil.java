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
import java.util.ArrayList;
import java.util.Collections;
import java.util.Objects;

public class MoneyUtil {

    public static ArrayList<ItemStack> getMoneyItems(Money money){

        Plugin plugin = DrestoriamMoney.getPlugin(DrestoriamMoney.class);
        ArrayList<ItemStack> moneyStacks = new ArrayList<>();

        for(int i = 0; i < 4; i++){

            switch(i){

                case 0:

                    if(money.getDenom1() == 0) break;

                    ItemStack firstDenom = new ItemStack(Material.BRICK,money.getDenom1());
                    ItemMeta firstDenomItemMeta = firstDenom.getItemMeta();
                    firstDenomItemMeta.setCustomModelData(145);
                    firstDenomItemMeta.setItemName(ChatColor.of("#33463b") + plugin.getConfig().getString("money.name1"));
                    firstDenomItemMeta.setLore(Collections.singletonList("$0.01"));
                    firstDenomItemMeta.setUnbreakable(true);
                    firstDenomItemMeta.addItemFlags(ItemFlag.HIDE_UNBREAKABLE);
                    firstDenom.setItemMeta(firstDenomItemMeta);
                    moneyStacks.add(firstDenom);

                    break;

                case 1:

                    if(money.getDenom2() == 0) break;

                    ItemStack secondDenom = new ItemStack(Material.BRICK,money.getDenom2());
                    ItemMeta secondDenomItemMeta = secondDenom.getItemMeta();
                    secondDenomItemMeta.setCustomModelData(146);
                    secondDenomItemMeta.setItemName(ChatColor.of("#502B1E") + plugin.getConfig().getString("money.name2"));
                    secondDenomItemMeta.setLore(Collections.singletonList("$1.00"));
                    secondDenomItemMeta.setUnbreakable(true);
                    secondDenomItemMeta.addItemFlags(ItemFlag.HIDE_UNBREAKABLE);
                    secondDenom.setItemMeta(secondDenomItemMeta);
                    moneyStacks.add(secondDenom);

                    break;

                case 2:

                    if(money.getDenom3() == 0) break;

                    ItemStack thirdDenom = new ItemStack(Material.BRICK,money.getDenom3());
                    ItemMeta thirdDenomItemMeta = thirdDenom.getItemMeta();
                    thirdDenomItemMeta.setCustomModelData(147);
                    thirdDenomItemMeta.setItemName(ChatColor.of("#C0C0C0") + plugin.getConfig().getString("money.name3"));
                    thirdDenomItemMeta.setLore(Collections.singletonList("$100.00"));
                    thirdDenomItemMeta.setUnbreakable(true);
                    thirdDenomItemMeta.addItemFlags(ItemFlag.HIDE_UNBREAKABLE);
                    thirdDenom.setItemMeta(thirdDenomItemMeta);
                    moneyStacks.add(thirdDenom);

                    break;

                case 3:

                    if(money.getDenom4() == 0) break;

                    ItemStack fourthDenom = new ItemStack(Material.BRICK,money.getDenom4());
                    ItemMeta fourthDenomItemMeta = fourthDenom.getItemMeta();
                    fourthDenomItemMeta.setCustomModelData(148);
                    fourthDenomItemMeta.setItemName(ChatColor.of("#FFD700") + plugin.getConfig().getString("money.name4"));
                    fourthDenomItemMeta.setLore(Collections.singletonList("$1000.00"));
                    fourthDenomItemMeta.setUnbreakable(true);
                    fourthDenomItemMeta.addItemFlags(ItemFlag.HIDE_UNBREAKABLE);
                    fourthDenom.setItemMeta(fourthDenomItemMeta);
                    moneyStacks.add(fourthDenom);

                    break;

            }


        }

        return moneyStacks;

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

        Money money = new Money(1, 1, 1, 1);
        ArrayList<ItemStack> moneyStack = MoneyUtil.getMoneyItems(money);
        ItemStack[] pInvContent = pInv.getContents();

        for(ItemStack item: pInvContent) {

            if(item == null || item.getType() == Material.AIR) continue;

            for (int i = 0; i < moneyStack.size(); i++) {

                if(item.getType() != moneyStack.get(i).getType()) continue;

                if (Objects.equals(item.getItemMeta().getLore().get(0), moneyStack.get(i).getItemMeta().getLore().get(0)) && item.getItemMeta().isUnbreakable()){

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

        money.setDenom1(money.getDenom1() - 1);
        money.setDenom2(money.getDenom2() - 1);
        money.setDenom3(money.getDenom3() - 1);
        money.setDenom4(money.getDenom4() - 1);

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
