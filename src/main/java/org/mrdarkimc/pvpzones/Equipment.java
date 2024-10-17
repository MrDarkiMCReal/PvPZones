package org.mrdarkimc.pvpzones;

import dev.lone.itemsadder.api.CustomStack;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;





public class Equipment {
//    private PvPZones getPvPZones (){
//        return PvPZones.getInstance();
//    }
//    public void setWeapon (Player player) {
//        if (player.hasPermission("pvpzones.weapon.katana-red")) {
//            iaItemSetter(player, "dmc:katana-red2",0);
//        }
//        if (player.hasPermission("pvpzones.weapon.demonsword")) {
//            iaItemSetter(player, "dmc:demonsword2",0);
//        }
//        if (player.hasPermission("pvpzones.weapon.demoniac_hammer")) {
//            iaItemSetter(player, "dmc:demoniac_hammer2",0);
//        }
//        if (player.hasPermission("pvpzones.weapon.axe")) {
//            iaItemSetter(player, "dmc:axe2",0);
//        }
//        if (player.hasPermission("pvpzones.weapon.serp")) {
//            iaItemSetter(player, "dmc:left_serp2",0);
//            iaItemSetter(player, "dmc:right_serp2",40);
//        }
//        if (player.hasPermission("pvpzones.weapon.katana-sharp")) {
//            iaItemSetter(player, "dmc:katana-sharp2",0);
//        }
//        if (player.hasPermission("pvpzones.weapon.iron_sword")) {
//            player.getInventory().setItem(0, new ItemStack(Material.IRON_SWORD));
//        }
//    }
//    public void setHelmet (Player player) {
//        if (player.hasPermission("pvpzones.wear.deadskull-mask")) {
//            iaItemSetter(player, "dmc:deadskull-mask2",39);
//        }
//        if (player.hasPermission("pvpzones.wear.samurai-hat-demon")) {
//            iaItemSetter(player, "dmc:samurai-hat-demon2",39);
//        }
//        if (player.hasPermission("pvpzones.wear.samurai-hat")) {
//            iaItemSetter(player, "dmc:samurai-hat2",39);
//        }
//        if (player.hasPermission("pvpzones.wear.crown-purple")) {
//            iaItemSetter(player, "dmc:crown-purple2",39);
//        }
//        if (player.hasPermission("pvpzones.wear.boba-hat")) {
//            iaItemSetter(player, "dmc:boba-hat2",39);
//        }
//        if (player.hasPermission("pvpzones.wear.air")) {
//            player.getInventory().setHelmet(new ItemStack(Material.AIR));
//        }
//
//
//
//    }
//    public void equipItemSetByPerms(Player player){
//        if (player.hasPermission("pvpzones.wear.steel")) {
//            equipSteelItemSet(player);
//        }
//        if (player.hasPermission("pvpzones.wear.chrome")) {
//            equipChromeItemSet(player);
//        }
//        if (player.hasPermission("pvpzones.wear.demon")) {
//            equipDemonItemSet(player);
//        }
//        if (player.hasPermission("pvpzones.wear.iron")) {
//            equipIronItemSet(player);
//        }
//    }
//    public void equipIronItemSet (Player player) {
//        player.getInventory().setChestplate(new ItemStack(Material.IRON_CHESTPLATE));
//        player.getInventory().setLeggings(new ItemStack(Material.IRON_LEGGINGS));
//        player.getInventory().setBoots(new ItemStack(Material.IRON_BOOTS));
//        iaItemSetter(player, "dmc:leave-item",8);
//        player.getInventory().setHeldItemSlot(0);
//    }
//    public void equipSteelItemSet (Player player) {
//        iaItemSetter(player,"dmc:steel_chest2",38);
//        iaItemSetter(player,"dmc:steel_legs2",37);
//        iaItemSetter(player,"dmc:steel_boots2",36);
//        iaItemSetter(player, "dmc:leave-item",8);
//        player.getInventory().setHeldItemSlot(0);
//    }
//    public void equipChromeItemSet (Player player) {
//        iaItemSetter(player,"dmc:chrome_chest2",38);
//        iaItemSetter(player,"dmc:chrome_legs2",37);
//        iaItemSetter(player,"dmc:chrome_boots2",36);
//        iaItemSetter(player, "dmc:leave-item",8);
//        player.getInventory().setHeldItemSlot(0);
//    }
//    public void equipDemonItemSet (Player player) {
//        iaItemSetter(player,"dmc:demon-samurai_chestplate2",38);
//        iaItemSetter(player,"dmc:demon-samurai_leggings2",37);
//        iaItemSetter(player,"dmc:demon-samurai_boots2",36);
//        iaItemSetter(player, "dmc:leave-item",8);
//        player.getInventory().setHeldItemSlot(0);
//    }
public void iaItemSetter(Player player, ItemStack itemStack, int slot) {
    player.getInventory().setItem(slot, itemStack);
}

    public void iaItemSetter(Player player, String iaItem, int amount, int slot, int modeldata) {
        if (PvPZones.getInstance().isItemsAdderEnabled)

        {
            String iaItem2 = iaItem.replaceAll("itemsadder:", "");

            CustomStack stack = CustomStack.getInstance(iaItem2);

            if (stack != null) {
                ItemStack itemStack = stack.getItemStack();

                itemStack.setAmount(amount);
                player.getInventory().setItem(slot, itemStack);

            }
            else player.sendMessage(ChatColor.RED + "Item " + iaItem2 + " is not valid for ItemsAdder. Contact an admin.");
        }
        else {
            Bukkit.getLogger().info(ChatColor.RED +  "PVPZONES cannot equip ItemsAdder item.(Maybe ItemsAdder is disabled in config?) Trying to convent it to minecraft item.");
            if (Material.matchMaterial(iaItem) != null) {
                ItemStack defaultStack = new ItemStack(Material.valueOf(iaItem), amount);
                if (!(modeldata == 0)) {
                    ItemMeta meta = defaultStack.getItemMeta();
                    meta.setCustomModelData(modeldata); 
                    defaultStack.setItemMeta(meta);

                }

                player.getInventory().setItem(slot, defaultStack);
            } else {PvPZones.getInstance().getServer().getLogger().info(ChatColor.RED +  "[PvPZones]Error obtaining item: " + iaItem);}
        }
    }
}
