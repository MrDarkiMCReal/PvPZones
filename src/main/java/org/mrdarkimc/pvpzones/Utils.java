package org.mrdarkimc.pvpzones;


import org.bukkit.*;
import org.bukkit.attribute.Attribute;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.Plugin;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class Utils {

    public static Plugin getPlugin() {
    return PvPZones.getInstance();
    }
    Equipment equipment = PvPZones.getInstance().equipment;

    public Utils() {
    }

    public static Location arenaLocation() {
        String worldString = getPlugin().getConfig().getString("location.world");
        World world = getPlugin().getServer().getWorld(worldString);
        float x = Float.parseFloat(getPlugin().getConfig().getString("location.x"));
        float y = Float.parseFloat(getPlugin().getConfig().getString("location.y"));
        float z = Float.parseFloat(getPlugin().getConfig().getString("location.z"));
        float yaw = getPlugin().getConfig().getInt("location.yaw");
        float pitch = getPlugin().getConfig().getInt("location.pitch");
        return new Location(world,x,y,z,yaw,pitch);
    }

    public void respawnPlayer(Player player){
        player.teleport(arenaLocation());
        player.getInventory().clear();
        healPlayer(player);
    }
    public void healPlayer(Player player) {
        player.setFireTicks(0);
        player.setHealth(player.getAttribute(Attribute.GENERIC_MAX_HEALTH).getValue());
        player.setFoodLevel(20);
    }
    public void setSpawnPoint(Player player) {
        String world = player.getWorld().getName();
        double x = (double) Math.round(((float) player.getLocation().getX()) * 2) / 2;
        double y = (float) player.getLocation().getY();
        double z = (double) Math.round(((float) player.getLocation().getZ()) * 2) / 2;
        float yaw = player.getLocation().getYaw();
        float pitch = player.getLocation().getPitch();
        getPlugin().getConfig().set("location.world",world);
        getPlugin().getConfig().set("location.x",x);
        getPlugin().getConfig().set("location.y",y);
        getPlugin().getConfig().set("location.z",z);
        getPlugin().getConfig().set("location.pitch",pitch);
        getPlugin().getConfig().set("location.yaw",yaw);
        getPlugin().saveConfig();
        player.sendMessage(ChatColor.GREEN + "Spawn point for instant respawn set in world " + world);
    }

//    public void getList (){
//        Set<String> SingleItemSet = getPlugin().getConfig().getConfigurationSection("Single-Item-Groups").getKeys(false);
//    }
//    private void setItems(Player player) {
//        Set<String> SingleItemSet = getPlugin().getConfig().getConfigurationSection("Single-Item-Groups").getKeys(false);
//    for (String key : SingleItemSet) {
//        Set<String> SingleItemSetItems = getPlugin().getConfig().getConfigurationSection("Single-Item-Groups." + key).getKeys(false);
//        for (String key2 : SingleItemSetItems) {
//            //pvpzones.wear.MyCustomSwordSkins.DefaultSword
//            String stringItem = getPlugin().getConfig().getString("Single-Item-Groups." + key + "." + key2);
//            String perms = "pvpzones.wear." + key + "." + key2;
//            if (player.hasPermission(perms.toLowerCase())) {
//                equipment.iaItemSetter(player, getItemNameOfItemString(stringItem), getAmountOfItemString(stringItem), getTypeOfItemString(stringItem), getModelDataOfItemString(stringItem));
//            }
//        }
//    }
//    }
    public void setGroups(Player player) //определяем какая группа есть у игрока
    {
        Set<String> groups = getPlugin().getConfig().getConfigurationSection("Set-of-items-Groups").getKeys(false);
        if (!(groups == null)) {
            for (String key : groups) {
                String perms = "pvpzones.group." + key;
                if (player.hasPermission(perms)) {
                    List<String> list = getPlugin().getConfig().getStringList("Set-of-items-Groups." + key);
                    for (String key2 : list) {
                        setItemsByGroups(player, key2);

                    }
                }
            }
        }
        else player.sendMessage("setGroups: Set<String> groups == null");
    }
    private void setItemsByGroups(Player player, String item) //выдаем каждый предмет который находится в группе
    {
        Set<String> SingleItemSet = getPlugin().getConfig().getConfigurationSection("Single-Item-Groups." + item).getKeys(false);
        if (!(SingleItemSet == null)) {
            for (String key : SingleItemSet) {
                //pvpzones.wear.MyCustomSwordSkins.DefaultSword
                String stringItem = getPlugin().getConfig().getString("Single-Item-Groups." + item + "." + key);
                String perms = "pvpzones.wear." + item + "." + key;
                if (player.hasPermission(perms)) {
                    if (stringItem.startsWith("itemsadder")){
                        equipment.iaItemSetter(player, getItemNameOfItemString(stringItem), getAmountOfItemString(stringItem), getTypeOfItemString(stringItem), getModelDataOfItemString(stringItem));
                    }else {
                        equipment.iaItemSetter(player, createItem(stringItem), getTypeOfItemString(stringItem));
                    }
                }
            }
        }
        else player.sendMessage("setItemsByGroups: Set<String> SingleItemSet == null");
    }
    private int getTypeOfItemString(String args) {
        Pattern pattern = Pattern.compile("type:(\\d+)");
        Matcher matcher = pattern.matcher(args);
        if (matcher.find()){
            int type = Integer.parseInt(matcher.group(1));
            return type;
        }
        else {
            getPlugin().getLogger().info(ChatColor.RED + "[PVPZones] ERROR: Some item missing TYPE value. (Type = slot)");
            throw new IllegalArgumentException("Error with item type. Specify slot");

        }
    }
    public ItemStack createItem(String stringitem){
        String[] array = stringitem.split(" ");
        Material material = Material.valueOf(array[0]);
        int amount = Integer.parseInt(array[1]);
        ItemStack stack = new ItemStack(material,amount);
        ItemMeta meta = stack.getItemMeta();
        for (int i = 2; i < array.length; i++) {
            String[] param = array[i].split(":");
            if (param.length != 2) continue;
            Enchantment enchantment = Enchantment.getByKey(NamespacedKey.fromString(param[0]));
            //Enchantment enchantments = Registry.ENCHANTMENT.get(NamespacedKey.fromString(param[0]));
            if (enchantment !=null){
                meta.addEnchant(enchantment,Integer.parseInt(param[1]),true);
            }
            stack.setItemMeta(meta);
        }
        return stack;
    }
    private int getModelDataOfItemString(String args) {
        Pattern pattern = Pattern.compile("modeldata:(\\d+)");
        Matcher matcher = pattern.matcher(args);
        if (matcher.find()){
            int modeldata = Integer.parseInt(matcher.group(1));
            return modeldata;
        }
        else {
            return 0;
        }
    }
    private String getItemNameOfItemString(String args) {
        String[] arr = args.split(" ");
        return arr[0];
    }
    private int getAmountOfItemString(String args) {
        String[] arr = args.split(" ");
        return Integer.parseInt(arr[1]);
    }
//    public void removeServerPermission(){
//        for (String key : singleItemSet) {
//            try {getPlugin().getServer().getPluginManager().removePermission(new Permission("pvpzones.wear" + key));}
//            catch (IllegalArgumentException e) {}
//        }
//    }
    public void playParticle(Player killer, int streak) {
        String key;
        if (getPlugin().getConfig().getBoolean("particles.onKillStreakParticle.enable") && streak >= getPlugin().getConfig().getInt("particles.onKillStreakParticle.killsToKillStreak")) {
            key = "onKillStreakParticle";
        }
        else {key = "onKillParticle";}
        String particleType = getPlugin().getConfig().getString("particles." + key + ".particle_type");
        getPlugin().getLogger().info("[PvPZones] playParticle key is: " + key);
        String world = getPlugin().getConfig().getString("location.world");
        int amount = getPlugin().getConfig().getInt("particles." + key + ".amount");
        float speed = Float.valueOf(getPlugin().getConfig().getString("particles." + key + ".speed"));
        float offsetX = Float.valueOf(getPlugin().getConfig().getString("particles." + key + ".offsetX"));
        float offsetY = Float.valueOf(getPlugin().getConfig().getString("particles." + key + ".offsetY"));
        float offsetZ = Float.valueOf(getPlugin().getConfig().getString("particles." + key + ".offsetZ"));
        double x = killer.getLocation().getX();
        double y = killer.getLocation().getY()+1;
        double z = killer.getLocation().getZ();
        try {
            if (getPlugin().getConfig().getBoolean("particles." + key + ".enable")) {
                getPlugin().getServer().getWorld(world).spawnParticle(Particle.valueOf(particleType), x, y, z, amount, offsetX, offsetY, offsetZ, speed);
            }
        }
        catch (NullPointerException e) {
            e.printStackTrace();
            getPlugin().getServer().getLogger().info(ChatColor.RED + "[PvPZones] Error in config: particles: some value is null!");
        }
    }
    public void soundEffect (Player killer) {
        String sound = getPlugin().getConfig().getString("sound_effect.sound_type");
        float volume = Float.valueOf(getPlugin().getConfig().getString("sound_effect.volume"));
        float pitch = Float.valueOf(getPlugin().getConfig().getString("sound_effect.pitch"));
        if (getPlugin().getConfig().getBoolean("sound_effect.enable")) {
            killer.playSound(killer.getLocation(), Sound.valueOf(sound), volume, pitch);
        }
    }
    public void teleportPlayerToArena (Player player) {
        player.teleport(arenaLocation());
        if (getPlugin().getConfig().getBoolean("messages.playerLeavePvPArea.enable")) {
            player.sendMessage(translateHex(getPlugin().getConfig().getString("messages.playerLeavePvPArea.message")));
        }
    }
    public void deathBroadcast (Player killer, Player victim) {
        if (getPlugin().getConfig().getBoolean("messages.broadcast.enable")) {
            try {
                String broadcast = getPlugin().getConfig().getString("messages.broadcast.message").replaceAll("%killer%", killer.getName()).replaceAll("%victim%", victim.getName());
                getPlugin().getServer().broadcastMessage(translateHex(broadcast));
            }
            catch (NullPointerException e) {e.printStackTrace();}
        }

    }
    public void messages(Player diedPlayer, Player killer) {
        String messageToKiller = getPlugin().getConfig().getString("messages.toKiller.message");
        if (getPlugin().getConfig().getBoolean("messages.toKiller.enable")) {
            killer.sendMessage(translateHex(messageToKiller.replaceAll("%victim%",diedPlayer.getName()).replaceAll("%killer%",killer.getName())));
        }
        String messageToDiedPlayer = getPlugin().getConfig().getString("messages.toDiedPlayer.message");
        if (getPlugin().getConfig().getBoolean("messages.toDiedPlayer.enable")) {
            diedPlayer.sendMessage(translateHex(messageToDiedPlayer.replaceAll("%victim%",diedPlayer.getName()).replaceAll("%killer%",killer.getName())));
        }
    }
    public static String translateHex (String message) {
        Pattern pattern = Pattern.compile("&#[0-9A-Fa-f]{6}");
        Matcher matcher = pattern.matcher(message);
        while (matcher.find()) {
            String color = message.substring(matcher.start(), matcher.end());
            message = message.replace(color, net.md_5.bungee.api.ChatColor.of(color.replaceAll("&","")) + "");
            matcher = pattern.matcher(message);
        }
        return ChatColor.translateAlternateColorCodes('&', message);
    }
}
