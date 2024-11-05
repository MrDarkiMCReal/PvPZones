package org.mrdarkimc.pvpzones.hooks;

import com.sk89q.worldedit.bukkit.BukkitAdapter;
import com.sk89q.worldguard.WorldGuard;
import com.sk89q.worldguard.protection.ApplicableRegionSet;
import com.sk89q.worldguard.protection.managers.RegionManager;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import com.sk89q.worldguard.protection.regions.RegionContainer;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.mrdarkimc.pvpzones.Equipment;
import org.mrdarkimc.pvpzones.ItemsSwitcher;
import org.mrdarkimc.pvpzones.PvPZones;
import org.mrdarkimc.pvpzones.Utils;

import java.util.*;

public class WorldGuardHook implements Listener {
    public WorldGuardHook() {
        this.switcher = PvPZones.getInstance().switcher;
        this.equipment = PvPZones.getInstance().equipment;
    }
    Equipment equipment;
    ItemsSwitcher switcher;
    public static final Set<String> equippedPlayers = new HashSet<>();
    @EventHandler
    public void JoinEvent(PlayerJoinEvent e){
        Player player = e.getPlayer();
        if (!equippedPlayers.contains(player.getName())) {
            if (switcher.getFromConfig(player) != null) {
                player.teleport(Utils.arenaLocation());
                switcher.giveInvBack(player);
            }
        }
    }

    @EventHandler
    public void playerEntersRegion(PlayerMoveEvent event) {
        Player player = event.getPlayer();
        if (player.getWorld().getName().equalsIgnoreCase(PvPZones.getInstance().getConfig().getString("location.world"))) {
            String pvpLocation = PvPZones.getInstance().getConfig().getString("Worldguard.regionToEquip");
            if (isInRegion(event.getFrom(), pvpLocation) && !equippedPlayers.contains(player.getName())) {
            equipment.engagePlayer(player);
            }
            if (!isInRegion(event.getFrom(), pvpLocation) && equippedPlayers.contains(player.getName())) {
                equipment.disEngage(player);
            }

        }
    }

    public boolean isInRegion(Location fromLocation, String regionToEquip) {
        //todo сделать в асинке?
        //а если этот метод асинхронный, будет ли зависать лисенер ожидая результат?
        //можно не метод сделать асинком, а когда вызываешь isInRg сделать асинк
        World world = fromLocation.getWorld();
        RegionContainer container = WorldGuard.getInstance().getPlatform().getRegionContainer();
        com.sk89q.worldedit.world.World adaptedworld = BukkitAdapter.adapt(world);
        RegionManager regionManager = container.get(adaptedworld);
        boolean isInPvpRegion = false;
        if (!(regionManager == null)) {
            ApplicableRegionSet playerInRegions = regionManager.getApplicableRegions(BukkitAdapter.asBlockVector(fromLocation));
            for (ProtectedRegion region : playerInRegions) {
                if (region.getId().equalsIgnoreCase(regionToEquip)) {
                    isInPvpRegion = true;
                    break;
                }
            }
        } else
            PvPZones.getInstance().getServer().getLogger().info(ChatColor.RED + "[PvPZones] ERROR: invalid world name (Regions with that world name is not found) in config: location.world: " + PvPZones.getInstance().getConfig().getString("location.world"));
        return isInPvpRegion;
    }

    @EventHandler
    public void onTeleportEvent(PlayerTeleportEvent event) {
        if (event.getFrom().getWorld().getName().equalsIgnoreCase(PvPZones.getInstance().getConfig().getString("location.world")))
        //&& event.getFrom().getWorld().getName().equalsIgnoreCase(event.getTo().getWorld().getName()))
        {
            if (isInRegion(event.getFrom(), PvPZones.getInstance().getConfig().getString("Worldguard.regionToEquip"))) {
            } else if (equippedPlayers.contains(event.getPlayer().getName())) {
                Location tplocation = event.getTo();
                event.setCancelled(true);
                event.getPlayer().getInventory().clear();
                event.getPlayer().teleport(tplocation);
                equippedPlayers.remove(event.getPlayer().getName());
                if (PvPZones.getInstance().getConfig().getBoolean("messages.playerLeavePvPArea.enable")) {
                    event.getPlayer().sendMessage(org.mrdarkimc.Utils.translateHex(PvPZones.getInstance().getConfig().getString("messages.playerLeavePvPArea.message")));
                }
            }
        }
    }
}
