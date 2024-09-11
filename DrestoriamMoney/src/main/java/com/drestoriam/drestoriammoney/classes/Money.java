package com.drestoriam.drestoriammoney.classes;

import com.drestoriam.drestoriammoney.util.MoneyUtil;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

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

    public Money(double balance){

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

        ItemStack[] moneyArray = MoneyUtil.getMoneyItems(this);

        player.getInventory().addItem(moneyArray);

    }

    public double getBalance(){

        double balance = 0;

        balance = balance + (denom1 * 0.01);
        balance = balance + denom2;
        balance = balance + (denom3 * 100);
        balance = balance + (denom4 * 1000);

        return balance;

    }

    public void parseBalance(double balance){

        double[] deno = {0.01, 1, 100, 1000};
        Map<Double, Integer> ans = new HashMap<>();

        for(double d: deno){

            ans.put(d, 0);

        }

        for(int i = deno.length - 1; i >= 0; i--){

            while(balance >= deno[i]){

                balance -= deno[i];


                ans.put(deno[i], ans.get(deno[i]) + 1);

            }

        }

        this.setDenom1(ans.get(deno[0]));
        this.setDenom2(ans.get(deno[1]));
        this.setDenom3(ans.get(deno[2]));
        this.setDenom4(ans.get(deno[3]));

    }

}
