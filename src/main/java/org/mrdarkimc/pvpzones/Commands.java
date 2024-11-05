package org.mrdarkimc.pvpzones;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.io.IOException;
import java.util.*;

public class Commands implements CommandExecutor, TabExecutor {
    public PvPZones getPlugin() {
        return PvPZones.getInstance();
    }

    public Commands() {
        this.utils = PvPZones.getInstance().getUtils();
    }
    List<CommandSender> allowedCommandSender = new ArrayList<>();

    Utils utils;

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (sender.hasPermission("pvz.admin")) {
            if (args.length == 0) {
                sender.sendMessage(Utils.translateHex("&#35A581[PvPZones] version 1.3 by MrDarkiMC"));
                sender.sendMessage(Utils.translateHex("&#35A581/pvz reload"));
                sender.sendMessage(Utils.translateHex("&#35A581/pvz backInventory <player>"));
                sender.sendMessage(Utils.translateHex("&#35A581/pvz TeleportToRespawn <player>"));
                sender.sendMessage(Utils.translateHex("&#35A581/pvz reloadKillStreakFile"));
                sender.sendMessage(Utils.translateHex("&#35A581/pvz equip <player> force/normal"));
                sender.sendMessage(Utils.translateHex("&#35A581/pvz equip <player> <group> force/normal"));
                sender.sendMessage(Utils.translateHex("&#35A581/pvz setitem <player> <itemgroup> <item>"));
                sender.sendMessage(Utils.translateHex("&#35A581/pvz setgroup <player> <group>"));
                sender.sendMessage(Utils.translateHex("&#35A581/pvz setspawn"));
            } else {
            switch (args[0]) {
                case "reload":
                    if (args.length == 1) {
                        getPlugin().reloadConfig();
                        sender.sendMessage(Utils.translateHex("&#35A581[PvPZones] config reloaded"));
                        getPlugin().getServer().getLogger().info(Utils.translateHex("&#35A581[PvPZones] config reloaded by " + sender.getName()));
                    } else sender.sendMessage(Utils.translateHex("&#35A581Usage: /pvz reload"));
                    break;
                case "setspawn":
                    if (args.length == 1) {
                        utils.setSpawnPoint((Player) sender);
                        sender.sendMessage(Utils.translateHex("&#35A581[PvPZones] spawnpoint set"));
                    } else sender.sendMessage(Utils.translateHex("&#35A581Usage: /pvz reload"));
                    break;
                case "equip":
                    //pvz equip MrDarkiMC normal
                    if (args.length == 3) {
                        if (args[2].equalsIgnoreCase("normal")) {
                            Player player = getPlugin().getServer().getPlayer(args[1]);
                            if (player.getInventory().isEmpty()) {
                                getPlugin().setEquipment(player);
                                sender.sendMessage(Utils.translateHex("&#35A581[PvPZones] Command done."));
                            } else
                                sender.sendMessage(Utils.translateHex("&#35A581[PvPZones] Player has something in his inventory"));
                        }
                        if (args[2].equalsIgnoreCase("force")) {
                            Player player = PvPZones.getInstance().getServer().getPlayer(args[1]);
                            getPlugin().setEquipment(player);
                            sender.sendMessage(Utils.translateHex("&#35A581[PvPZones] Command done."));
                        }
                    } else
                        sender.sendMessage(Utils.translateHex("&#35A581Usage: /pvz equip <player> <group> force/normal"));
                    break;
                case "setitem":
                    //pvz set mrdarkimc MyCustomSwordSkins DefaultSword
                    if (getPlugin().isLuckPermsEnable) {
                        Player player = getPlugin().getServer().getPlayer(args[1]);
                        String group = args[2];
                        String item = args[3];
                        PvPZones.getInstance().LpHook.setPermission(sender, player, group, item);
                    } else {
                        sender.sendMessage(Utils.translateHex("&#35A581[PvPZones] LuckPerms plugin is not found. Feature is disabled"));
                    }
                    break;
                case "setgroup":
                    if (getPlugin().isLuckPermsEnable) {
                        //pvz setgroup mrdarkimc pvpOnIronArmor
                        Player player = getPlugin().getServer().getPlayer(args[1]);
                        String group = args[2];
                        PvPZones.getInstance().LpHook.setPlayerGroup(sender, player, group);
                    } else {
                        sender.sendMessage(Utils.translateHex("&#35A581[PvPZones] LuckPerms plugin is not found. Feature is disabled"));
                    }
                    break;
                case "TeleportToRespawn":
                    Player player = getPlugin().getServer().getPlayer(args[1]);
                    utils.teleportPlayerToArena(player);
                    break;
                case "backInventory":
                        if (!allowedCommandSender.contains(sender)){
                            sender.sendMessage(Utils.translateHex("&#35A581[PvPZones] &#ffba42This command will try to return players items if server was crashed"));
                            sender.sendMessage(Utils.translateHex("&#35A581[PvPZones] &#ffba42and items were lost. if you want check saveditems.yml to see if players have items"));
                            sender.sendMessage(Utils.translateHex("&#35A581[PvPZones] &#ffba42WARNING. Make sure:"));
                            sender.sendMessage(Utils.translateHex("&#35A581[PvPZones] &#ffba421)Player name is typed correctly and hes online"));
                            sender.sendMessage(Utils.translateHex("&#35A581[PvPZones] &#ffba422)Player not in the arena right now"));
                            sender.sendMessage(Utils.translateHex("&#35A581[PvPZones] &#ffba42Playername is case sensitive"));
                            sender.sendMessage(Utils.translateHex(""));
                            sender.sendMessage(Utils.translateHex("&#35A581[PvPZones] &#ffba42Enter this command again if you sure you done it right"));
                            allowedCommandSender.add(sender);
                            new BukkitRunnable(){
                                @Override
                                public void run() {
                                    allowedCommandSender.remove(sender);
                                }
                            }.runTaskLaterAsynchronously(PvPZones.getInstance(),800);
                    }
                    Player player2 = getPlugin().getServer().getPlayer(args[1]);
                    PvPZones.getInstance().switcher.giveInvBack(player2);
                    allowedCommandSender.remove(sender);
                    break;
                case "reloadKillStreakFile":
                    try {
                        PvPZones.killstreak.reloadKillstreakFile();
                        sender.sendMessage(Utils.translateHex("&#35A581[PvPZones] Kill streaks reloaded"));
                    } catch (IOException | InvalidConfigurationException e) {
                        throw new RuntimeException(e);
                    }
                    break;
                case "help":
                    sender.sendMessage(Utils.translateHex("&#35A581[PvPZones] version 1.3 by MrDarkiMC"));
                    sender.sendMessage(Utils.translateHex("&#35A581/pvz reload"));
                    sender.sendMessage(Utils.translateHex("&#35A581/pvz backInventory <player>"));
                    sender.sendMessage(Utils.translateHex("&#35A581/pvz TeleportToRespawn <player>"));
                    sender.sendMessage(Utils.translateHex("&#35A581/pvz reloadKillStreakFile"));
                    sender.sendMessage(Utils.translateHex("&#35A581/pvz equip <player> force/normal"));
                    sender.sendMessage(Utils.translateHex("&#35A581/pvz equip <player> <group> force/normal"));
                    sender.sendMessage(Utils.translateHex("&#35A581/pvz setitem <player> <itemgroup> <item>"));
                    sender.sendMessage(Utils.translateHex("&#35A581/pvz setgroup <player> <group>"));
                    sender.sendMessage(Utils.translateHex("&#35A581/pvz setspawn"));
                    break;
                default:
                    sender.sendMessage(Utils.translateHex("&#35A581[PvPZones] Command did not found. Use /pvz help"));
                    sender.sendMessage(Utils.translateHex("&#35A581[PvPZones] Commands are case sensitive"));
                    break;
            }
            }
//            if (args.length == 1 && args[0].equalsIgnoreCase("reload")) {
//
//            }
//            if (args.length == 1 && args[0].equalsIgnoreCase("setspawn") & sender instanceof Player) {
//                utils.setSpawnPoint((Player) sender);
//                sender.sendMessage(ChatColor.GREEN + "[PvPZones] spawnpoint set");
//            }
//            if ( args.length == 3 && args[0].equalsIgnoreCase("equip") && args[2].equalsIgnoreCase("normal")) {
//                Player player = getPlugin().getServer().getPlayer(args[1]);
//                if (player.getInventory().isEmpty()) {
//                    pvPZones.setEquipment(player);
//                    sender.sendMessage(ChatColor.GREEN + "Command done.");
//                } else sender.sendMessage(ChatColor.RED + "Player has something in inventory");
//
//            }
//            if (args.length == 3 && args[0].equalsIgnoreCase("equip") && args[2].equalsIgnoreCase("force")) {
//                Player player = getPlugin().getServer().getPlayer(args[1]);
//                pvPZones.setEquipment(player);
//                sender.sendMessage(ChatColor.GREEN + "Command done.");
//            }
//            if (args.length == 4 && args[0].equalsIgnoreCase("setitem")) {
//                //pvz set mrdarkimc MyCustomSwordSkins DefaultSword
//                if (pvPZones.isLuckPermsEnable) {
//                    Player player = getPlugin().getServer().getPlayer(args[1]);
//                    String group = args[2];
//                    String item = args[3];
//                    PvPZones.getInstance().LpHook.setPermission(sender, player, group, item);
//                }else {sender.sendMessage("LuckPerms plugin is not found. Feature is disabled");}
//            }
//            if (args.length == 3 && args[0].equalsIgnoreCase("setgroup") ) {
//                 if (pvPZones.isLuckPermsEnable) {
//                     //pvz setgroup mrdarkimc pvpOnIronArmor
//                     Player player = getPlugin().getServer().getPlayer(args[1]);
//                     String group = args[2];
//                     PvPZones.getInstance().LpHook.setPlayerGroup(sender, player, group);
//                 } else {sender.sendMessage("LuckPerms plugin is not found. Feature is disabled");}
//            }
//            if (args.length == 2 && args[1].equalsIgnoreCase("TeleportToRespawn")) {
//                Player player = getPlugin().getServer().getPlayer(args[1]);
//                utils.teleportPlayerToArena(player);
//            }
//            if (args.length == 1 && args[0].equalsIgnoreCase("reloadKillStreakFile")) {
//                try {
//                    PvPZones.killstreak.reloadKillstreakFile();
//                } catch (IOException e) {
//                    throw new RuntimeException(e);
//                } catch (InvalidConfigurationException e) {
//                    throw new RuntimeException(e);
//                }
//            }

        }
        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        if (args.length == 1) {
        return Arrays.asList("reload", "equip", "setitem", "setgroup", "setspawn", "TeleportToRespawn", "reloadKillStreakFile","backInventory");
        }
        if (args.length == 2 && args[0].equalsIgnoreCase("equip")) {
            return null;
        }
        if (args.length == 2 && args[0].equalsIgnoreCase("backInventory")) {
            return null;
        }
        if (args.length == 3 && args[0].equalsIgnoreCase("equip")) {
            return Arrays.asList("force", "normal");
        }
        if (args.length == 2 && args[0].equalsIgnoreCase("setitem")) {
            return null;
        }
        if (args.length == 3 && args[0].equalsIgnoreCase("setitem")) {
            Set<String> SingleItemSet = getPlugin().getConfig().getConfigurationSection("Single-Item-Groups").getKeys(false);
            String[] list = SingleItemSet.toArray(new String[SingleItemSet.size()]);
            return Arrays.asList(list);
        }
        if (args.length == 4 && args[0].equalsIgnoreCase("setitem")) {///pvz setitem <player> <itemgroup> <item>
            Set<String> SingleItemSet = getPlugin().getConfig().getConfigurationSection("Single-Item-Groups." + args[2]).getKeys(false);
            String[] list = SingleItemSet.toArray(new String[SingleItemSet.size()]);
            return Arrays.asList(list);
        }
        if (args.length == 2 && args[0].equalsIgnoreCase("setgroup")) {
            return null;
        }
        if (args.length == 3 && args[0].equalsIgnoreCase("setgroup")) {
            Set<String> SingleItemSet = getPlugin().getConfig().getConfigurationSection("Set-of-items-Groups").getKeys(false);
            String[] list = SingleItemSet.toArray(new String[SingleItemSet.size()]);
            return Arrays.asList(list);
        }
        return new ArrayList<>();
    }
}
