package org.mrdarkimc.pvpzones;


import org.bukkit.*;
import org.bukkit.attribute.Attribute;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.plugin.java.JavaPlugin;
import org.mrdarkimc.pvpzones.hooks.ItemsAdderHook;
import org.mrdarkimc.pvpzones.hooks.LuckPermsHook;
import org.mrdarkimc.pvpzones.hooks.WorldGuardHook;

import java.io.File;
import java.io.IOException;


public final class PvPZones extends JavaPlugin implements Listener {
    private static PvPZones instance;
    Utils utils = new Utils();
    static Killstreak killstreak;
    boolean isItemsAdderEnabled = false;
    boolean isWorldGuardEnable = false;
    boolean isLuckPermsEnable = false;
    LuckPermsHook LpHook;
    public Utils getUtils() {
        return utils;
    }


    @Override
    public void onEnable() {
        getServer().getLogger().info("§x§4§0§8§1§8§A[PvPZonePlus] §x§9§3§C§0§C§6is enabled and ready to run");
        getServer().getPluginManager().registerEvents(this, this);
        instance = this;
        getServer().getPluginCommand("pvz").setExecutor(new Commands());
        File file = new File(getDataFolder(), "config.yml");
        {
        }
        if (!file.exists()) {
            saveDefaultConfig();
        } else {
            saveConfig();
        }
        {
            try {
                killstreak = new Killstreak();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
        if (getServer().getPluginManager().getPlugin("ItemsAdder") != null){
            isItemsAdderEnabled=true;
            getServer().getPluginManager().registerEvents(new ItemsAdderHook(),this);
        }
        if (getServer().getPluginManager().getPlugin("WorldGuard") != null){
            isWorldGuardEnable=true;
            PvPZones.getInstance().getServer().getPluginManager().registerEvents(new WorldGuardHook(), PvPZones.getInstance());
        }
        if (getServer().getPluginManager().getPlugin("LuckPerms") != null){
            isLuckPermsEnable=true;
            LpHook = new LuckPermsHook();
        }
    }

    public static PvPZones getInstance() {
        return instance;
    }

    @Override
    public void onDisable() {
        getServer().getLogger().info("[PvPZones] is shutting down");
        instance = null;
//        utils.removeServerPermission();
    }

    //    @EventHandler
//    public void joinsEvent(PlayerJoinEvent event) {
//        Player player = event.getPlayer();
//        player.sendMessage(ChatColor.BLUE + "Привет, " + ChatColor.DARK_BLUE + player);
//
//    }
    @EventHandler
    public void onDamageCheck(EntityDamageByEntityEvent event) {
        String world = getConfig().getString("location.world");
        if (event.getEntity() instanceof Player && event.getEntity().getWorld().getName().equals(world)) {
            Player player = (Player) event.getEntity();
            double playerHP = player.getHealth();
            if (playerHP <= event.getFinalDamage()) {
                event.setCancelled(true);
                if (event.getDamager() instanceof Player) {
                    Player damager = (Player) event.getDamager();
                    handleDamager(damager, player);
                    player.playSound(damager.getLocation(),Sound.ENTITY_PLAYER_ATTACK_CRIT,1,1);
                } else if (event.getDamager() instanceof Projectile) {
                    Projectile projectile = (Projectile) event.getDamager();
                    if (projectile.getShooter() instanceof Player) {
                        Player damager = (Player) projectile.getShooter();
                        handleDamager(damager, player);
                        player.playSound(damager.getLocation(),Sound.ENTITY_ARROW_HIT,1,1);
                    }
                }
            }
        }
    }

    private void handleDamager(Player damager, Player player) {
        utils.playParticle(player, killstreak.getStreak(damager));
        utils.respawnPlayer(player);
        player.setStatistic(Statistic.DEATHS, player.getStatistic(Statistic.DEATHS) + 1);
        utils.soundEffect(damager);
        utils.messages(player, damager);
        utils.deathBroadcast(damager, player);
        killstreak.endKillstreakMessageEvent(damager,player);
        killstreak.selectStreakType(damager, player);
        afterKillHeal(damager);
        damager.setStatistic(Statistic.PLAYER_KILLS, damager.getStatistic(Statistic.PLAYER_KILLS) + 1);
    }


    public void afterKillHeal(Player player) {
        if (getConfig().getBoolean("healAfterKill.enable")) {
            double damagerHP = player.getHealth() + getConfig().getInt("healAfterKill.amount");
            double damagerMaxHP = player.getAttribute(Attribute.GENERIC_MAX_HEALTH).getValue();
            if (!(damagerHP >= damagerMaxHP)) {
                player.setHealth(damagerHP);
            } else {
                player.setHealth(player.getAttribute(Attribute.GENERIC_MAX_HEALTH).getValue());
            }
        }
    }

    public void setEquipment(Player player) {
        player.getInventory().clear();
        player.getInventory().setHeldItemSlot(0);
        utils.setGroups(player);
    }


    //    private final JavaPlugin plugin = new JavaPlugin() {};
//    public void config(){
//        String itemName = "myCustomChestPlate";
//        String itemMaterial = String.valueOf(Material.DIAMOND_CHESTPLATE);
//        String type = "chestplate";
//        String itemNamePath = String.join(".", "items");
//        String itemMaterialPath = String.join(".", "items",itemName,"itemname");
//        String typePath = String.join(".", "items",itemName,"type");
//        getConfig().createSection(itemMaterialPath);
//        getConfig().createSection(typePath);
//        getConfig().set(itemNamePath, itemName);
//        getConfig().set(itemMaterialPath, itemMaterial);
//        getConfig().set(typePath, type);
//        saveConfig();
//
//    }
//public void equipDefaultSet(Player player) {
//    itemSetter(player, "dmc:samurai-hat-demon",39);
//    itemSetter(player, "dmc:demon-samurai_chestplate",38);
//    itemSetter(player, "dmc:demon-samurai_leggings",37);
//    itemSetter(player, "dmc:demon-samurai_boots",36);
//    itemSetter(player, "dmc:katana-sharp",0);
//    itemSetter(player, "dmc:leave-item",44);
//}
//    public void equipMenuItems(Player player) {
//        itemSetter(player, "dmc:menu-item",39);
//    }
//    public void tryout (PlayerMoveEvent e) {
//        Player player = e.getPlayer();
//        player.getInventory().getChestplate().g
//
//
//    }


//    @EventHandler
//    public void onSomethingEvent(EntityDamageByEntityEvent e) {
//        Player player = (Player) e.getDamager();
//        utils.playParticle(player);
//        utils.soundEffect(player);
//        utils.deathBroadcast(player,player);
//            killstreak.selectStreakType((Player) e.getDamager(),(Player) e.getDamager());
//    }
//    public void setChestPlate (Player player) {
//        //String path = String.join(getPath() + player или вместо плеер название итемСета, например по праву dmc.myCustomItemSet); //попробовать вот так реализовать работу конфига
//            String string = getConfig().getString(getPath(getEquipmentIdByPerms(player))); //"none" or "something from list" getString("items.myCustomChestPlate.itemname: DIAMOND_CHESTPLATE")
//        //String string1 = getConfig().getString("items.myCustomChestPlate.itemname");
//            if (string != null) {
//                Material itemMaterial = Material.getMaterial(string);
//                ItemStack itemStack = new ItemStack(itemMaterial);
//                player.getInventory().setChestplate(itemStack);
//            }
//            else {
//                getServer().getLogger().info(ChatColor.RED + "[PvPZone] ERROR: There is no such item. Check your config to match permissions with itemname");
//                getServer().getLogger().info(ChatColor.RED + "[PvPZone] Setting AIR instead of your item");
//                player.getInventory().setChestplate(new ItemStack(Material.AIR));
//            }
//    }

//    public String getEquipmentIdByPerms (Player player) {
//            int index;
//            List<String> itemList = getConfig().getStringList("items");
//            ConfigurationSection section = getConfig().getConfigurationSection("items");
//            section.getList("items");
//            if (itemList !=null) {
//                String equipmentId = "none";
//                for (index = 0; index < itemList.size(); index++) {
//                    if (player.hasPermission(itemList.get(index))) {
//                        equipmentId = itemList.get(index);
//                    }
//                }
//                player.sendMessage("getEquipmentIdByPerms output: " + equipmentId);
//                return equipmentId; //"something of list" or "none"
//            }
//            else return "none";
//    }
//    public String getPath(String equipmentSetID) { // "none" or "myCustomChestPlate"
//        String path = String.join(".","items",equipmentSetID,"itemname");
//        Player player = getServer().getPlayer("MrDarkiMC");
//        player.sendMessage("getPath output: " + path);
//        return path; //"items.myCustomChestPlate.itemname"
//    }

}
