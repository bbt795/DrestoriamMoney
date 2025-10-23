package com.drestoriam.drestoriammoney.util;

import com.drestoriam.drestoriammoney.classes.PlayerBank;
import org.bukkit.Bukkit;

import java.util.UUID;

public class BankWrapper {

    private PlayerBank playerBank;

    public BankWrapper(String uuid) {

        playerBank = new PlayerBank(Bukkit.getPlayer(UUID.fromString(uuid)));

    }

    public PlayerBank getPlayerBank() {
        return playerBank;
    }

    public void setPlayerBank(PlayerBank playerBank) {
        this.playerBank = playerBank;
    }

}
