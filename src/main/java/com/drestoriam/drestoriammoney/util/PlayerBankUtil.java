package com.drestoriam.drestoriammoney.util;

import com.drestoriam.drestoriammoney.DrestoriamMoney;
import com.drestoriam.drestoriammoney.classes.PlayerBank;
import com.google.gson.Gson;

import java.io.*;
import java.util.HashMap;

public class PlayerBankUtil {

    private DrestoriamMoney plugin;
    private HashMap<String, PlayerBank> bankHashMap = new HashMap<>();

    public HashMap<String, PlayerBank> getBankHashMap() { return bankHashMap; }

    public PlayerBankUtil(DrestoriamMoney plugin) { this.plugin = plugin; }

    public void createBank(String uuid){

        BankWrapper wrapper = new BankWrapper(uuid);

        Gson gson = new Gson();
        File file = new File(plugin.getDataFolder().getAbsolutePath() + "/PlayerBanks/" + uuid + ".json");

        try{

            if(file.createNewFile()){

                Writer writer = new FileWriter(file, false);
                writer.write(gson.toJson(wrapper));
                writer.flush();
                writer.close();
                loadBank(uuid);
                System.out.println("[DrestoriamMoney] Created new player file: " + uuid);

            } else {

                System.out.println("[DrestoriamMoney] Failed to create character sheet for user: " + uuid);

            }

        } catch (IOException e){

            throw new RuntimeException(e);

        }

    }

    public void saveBank(String uuid){

        File file = new File(plugin.getDataFolder().getAbsolutePath() + "/PlayerBanks/" + uuid + ".json");
        Gson gson = new Gson();

        if(file.exists()){

            try{

                Writer writer = new FileWriter(file, false);

                BankWrapper wrapper = new BankWrapper(uuid);
                wrapper.setPlayerBank(bankHashMap.get(uuid));
                writer.write(gson.toJson(wrapper));
                writer.flush();
                writer.close();

            } catch (IOException e){

                throw new RuntimeException(e);

            }

        }

    }

    public void loadBank(String uuid){

        File file = new File(plugin.getDataFolder().getAbsolutePath() + "/PlayerBanks/" + uuid + ".json");

        if(file.exists()){

            Gson gson = new Gson();

            try{

                BufferedReader reader = new BufferedReader(new FileReader(file));
                BankWrapper wrapper = gson.fromJson(reader, BankWrapper.class);
                bankHashMap.putIfAbsent(uuid, wrapper.getPlayerBank());

            } catch (FileNotFoundException e){

                throw new RuntimeException(e);

            }

        } else {

            createBank(uuid);

        }

    }

}
