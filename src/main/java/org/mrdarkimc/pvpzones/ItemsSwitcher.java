package org.mrdarkimc.pvpzones;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.mrdarkimc.Configs;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

public class ItemsSwitcher {
    public ItemsSwitcher() {
        this.playerInventories = new HashMap<>();
    }

    Map<Player, List<ItemStack>> playerInventories;
    public void saveInv(Player player) {
        List<ItemStack> stackList = new ArrayList<>();
        Inventory inv = player.getInventory();
        for (int i = 0; i < inv.getSize(); i++) {
            stackList.add(inv.getItem(i));
            playerInventories.put(player, stackList);
        }
        serialize(player, stackList); //double check
    }

    public void giveInvBack(Player player) {
        //String pvpLocation = PvPZones.getInstance().getConfig().getString("Worldguard.regionToEquip");

        //if (!isInRegion(player.getLocation(), pvpLocation)) {
            List<ItemStack> stackList = playerInventories.get(player);

            if (stackList == null) {
                stackList = getFromConfig(player);
            }

            if (stackList != null) {
                give(player, stackList);
                removeFromConfig(player,stackList);
            } else {
                Logger logger = Bukkit.getLogger();
                logger.info(ChatColor.RED + "Player " + player.getName() + " trying to get his items back after leaving the PvP region. No saved items found.");
                logger.info(ChatColor.YELLOW + "Please contact a developer.");
                logger.info(ChatColor.YELLOW + "https://www.spigotmc.org/resources/pvpzones-autokit%E2%9C%85-fast-respawn%E2%9C%85-killstreaks%E2%9C%85-skins-%E2%9C%85-particles-sounds%E2%9C%85.117952/");
                logger.info(ChatColor.YELLOW + "https://discord.gg/t5dU27WKMG");
            }
        //}
    }

    public void give(Player player, List<ItemStack> stackList){
        player.getInventory().clear();
        for (int i = stackList.size() - 1; i >= 0; i--) {
            player.getInventory().setItem(i, stackList.get(i));
        }
        playerInventories.remove(player);
    }
    public void serialize(Player player, List<ItemStack> stackList) {
        Configs items = PvPZones.savedItems;
        if (items.get().contains("savedItems." + player)) {
            Logger logger = Bukkit.getLogger();
            logger.info(ChatColor.RED + "Player " + player + " trying to save his items entering pvp region. He already have saved items.");
            logger.info(ChatColor.RED + "If you think its an error you can delete his items manually in config.");
            logger.info(ChatColor.YELLOW  + "Please contact an developer");
            logger.info("https://www.spigotmc.org/resources/pvpzones-autokit%E2%9C%85-fast-respawn%E2%9C%85-killstreaks%E2%9C%85-skins-%E2%9C%85-particles-sounds%E2%9C%85.117952/");
            logger.info("https://discord.gg/t5dU27WKMG");
            return;
        }
        items.get().set("savedItems." + player.getName(), stackList);
        items.saveConfig();
    }
    //null or stackList
    public List<ItemStack> getFromConfig(Player player) {
        List<ItemStack> stacklist = (List<ItemStack>) PvPZones.savedItems.get().get("savedItems." + player.getName());
        return stacklist;
    }
    public void removeFromConfig(Player player, List<ItemStack> stackList) {
        if (stackList !=null) {
            PvPZones.savedItems.get().set("savedItems." + player, null);
            PvPZones.savedItems.saveConfig();
        }
    }

}
