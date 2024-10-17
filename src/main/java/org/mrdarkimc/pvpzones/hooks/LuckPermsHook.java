package org.mrdarkimc.pvpzones.hooks;

import net.luckperms.api.LuckPerms;
import net.luckperms.api.LuckPermsProvider;
import net.luckperms.api.model.user.User;
import net.luckperms.api.node.Node;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.mrdarkimc.pvpzones.PvPZones;

import java.util.Set;
import java.util.stream.Collectors;

public class LuckPermsHook {
    public static Plugin getPlugin() {
        return PvPZones.getInstance();
    }
    public void setPlayerGroup(CommandSender sender, Player player, String group)
    {//удаление прав через цикл MrDarkiMC
        LuckPerms luckPerms = LuckPermsProvider.get();
        User user = luckPerms.getUserManager().getUser(player.getUniqueId());
        if (user == null) {
            getPlugin().getLogger().info(ChatColor.RED + "User " + player + " not found in LuckPerms database");
            sender.sendMessage(ChatColor.RED + "User " + player + " not found in LuckPerms database");
            return;
        }
        Set<Node> collection = user.getNodes().stream().filter(node -> node.getKey().startsWith("pvpzones.group.")).collect(Collectors.toSet());
        collection.forEach(user.data()::remove);
        Node addNegativeNode = Node.builder("pvpzones.group.*").value(false).build();
        Node addNode = Node.builder("pvpzones.group." + group).value(true).build();
        user.data().add(addNegativeNode);
        user.data().add(addNode);
        luckPerms.getUserManager().saveUser(user);
        sender.sendMessage(ChatColor.GREEN + "[PvPZones] permissions set for user " + player.getName());
        sender.sendMessage(ChatColor.GREEN + "Now player will be getting items from group: " + group);
    }
    public void setPermission (CommandSender sender, Player player, String group, String item) {
        LuckPerms luckPerms = LuckPermsProvider.get();
        User user = luckPerms.getUserManager().getUser(player.getUniqueId());
        if (user == null) {
            getPlugin().getLogger().info(ChatColor.RED + "User " + player + " not found in LuckPerms database");
            sender.sendMessage(ChatColor.RED + "User " + player + " not found in LuckPerms database");
            return;
        }
        Set<Node> collection = user.getNodes().stream().filter(node -> node.getKey().startsWith("pvpzones.wear." + group)).collect(Collectors.toSet());
        collection.forEach(user.data()::remove);
        Node removeNode = Node.builder("pvpzones.wear." + group + ".*").value(false).build();
        user.data().add(removeNode);
        Node addNode = Node.builder("pvpzones.wear." + group + "." + item).value(true).build();
        user.data().add(addNode);
        luckPerms.getUserManager().saveUser(user);
        sender.sendMessage(ChatColor.GREEN + "[PvPZones] permissions set for user " + player.getName());
        sender.sendMessage(ChatColor.GREEN + "Now player will obtain item: " + item + " from ItemGroup: " + group);
    }
}
