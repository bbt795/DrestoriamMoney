package com.drestoriam.drestoriammoney.classes;

import com.drestoriam.drestoriammoney.util.MoneyUtil;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;


public class Money {

    private int denom1;
    private int denom2;
    private int denom3;
    private int denom4;

    public Money(int denom1, int denom2, int denom3, int denom4){

        this.denom1 = denom1;
        this.denom2 = denom2;
        this.denom3 = denom3;
        this.denom4 = denom4;

    }

    public Money(BigDecimal balance){

        parseBalance(balance);

    }

    public int getDenom1() {
        return denom1;
    }

    public void setDenom1(int denom1) {
        this.denom1 = denom1;
    }

    public int getDenom2() {
        return denom2;
    }

    public void setDenom2(int denom2) {
        this.denom2 = denom2;
    }

    public int getDenom3() {
        return denom3;
    }

    public void setDenom3(int denom3) {
        this.denom3 = denom3;
    }

    public int getDenom4() {
        return denom4;
    }

    public void setDenom4(int denom4) {
        this.denom4 = denom4;
    }

    public void payOut(Player player){

        ArrayList<ItemStack> moneyArray = MoneyUtil.getMoneyItems(this);

        for(ItemStack item: moneyArray){

            player.getInventory().addItem(item);

        }
    }

    public BigDecimal getInventoryBalance(){

        BigDecimal balance = new BigDecimal(0);

        balance = balance.add(new BigDecimal("0.01").multiply(new BigDecimal(denom1)));
        balance = balance.add(new BigDecimal(denom2));
        balance = balance.add(new BigDecimal("100").multiply(new BigDecimal(denom3)));
        balance = balance.add(new BigDecimal("1000").multiply(new BigDecimal(denom4)));

        return balance;

    }

    public void parseBalance(BigDecimal balance){

        BigDecimal[] deno = {new BigDecimal("0.01"), new BigDecimal ("1"), new BigDecimal ("100"), new BigDecimal ("1000")};
        Map<BigDecimal, Integer> ans = new HashMap<>();

        for(BigDecimal d: deno){

            ans.put(d, 0);

        }

        //i = 3; i >= 0 (3, 2, 1, 0)
        for(int i = deno.length - 1; i >= 0; i--){

            while(balance.compareTo(deno[i]) >= 0){

                ans.put(deno[i], ans.get(deno[i]) + 1);
                balance = balance.subtract(deno[i]);

            }

        }

        this.setDenom1(ans.get(deno[0]));
        this.setDenom2(ans.get(deno[1]));
        this.setDenom3(ans.get(deno[2]));
        this.setDenom4(ans.get(deno[3]));

    }

}
