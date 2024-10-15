package org.mrdarkimc.pvpzones;

import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.stream.Stream;
import java.lang.Integer;
import java.util.Set;
import java.util.TreeSet;
import java.util.Comparator;

public class Killstreak {
    Utils utils = new Utils();
    private FileConfiguration killstreakFile;
    private File killstreakFilePath;

    public static Plugin getPlugin() {
        return PvPZones.getInstance();
    }

    public Killstreak() throws IOException {
        killstreakFilePath = new File(getPlugin().getDataFolder(), "killstreak.yml");
        killstreakFile = YamlConfiguration.loadConfiguration(killstreakFilePath);
        if (!killstreakFilePath.exists()) {
            killstreakFile.save(killstreakFilePath);
        }
    }

    private void saveKillstreakFile() throws IOException {
        killstreakFile.save(killstreakFilePath);
    }
    public void reloadKillstreakFile () throws IOException, InvalidConfigurationException {
        if (!killstreakFilePath.exists()) {
            killstreakFile.save(killstreakFilePath);
        }
        killstreakFile = YamlConfiguration.loadConfiguration(killstreakFilePath);
    }

    protected void addPoint(Player player) throws IOException {
        String uuid = player.getUniqueId().toString();
        if (!killstreakFile.contains(uuid)) {
            killstreakFile.set(uuid, 1);
        } else {
            int value = killstreakFile.getInt(uuid) + 1;
            killstreakFile.set(uuid, value);
        }
        saveKillstreakFile();
    }

    public int getStreak(Player player) {
        String uuid = player.getUniqueId().toString();
        return killstreakFile.getInt(uuid);
    }

    protected void removePoint(Player player) throws IOException {
        killstreakFile.set(player.getUniqueId().toString(), 0);
        saveKillstreakFile();
    }
    public void selectStreakType (Player killer, Player victim) {
        if (getPlugin().getConfig().getBoolean("killstreak.enable")) {
        if (getPlugin().getConfig().getBoolean("killstreak.send_killstreakMessage_every_time_player_kills_a_victim")){
            killStreakEachTime(killer,victim);
        }
        else {
            killstreakWhenReach(killer,victim);
        }
        }

    }
    public void killstreakWhenReach (Player killer, Player victim) {
        try {
            addPoint(killer);
            removePoint(victim);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    int streak = getStreak(killer);
    Set<String> set = getPlugin().getConfig().getConfigurationSection("messages.killstreak").getKeys(false);
    for (String key : set) {
        if (Integer.parseInt(key) == streak){
            String message = getPlugin().getConfig().getString("messages.killstreak." + key).replaceAll("%killer%",killer.getName()).replaceAll("%victim%",victim.getName()).replaceAll("%killstreak%",String.valueOf(streak));
            getPlugin().getServer().broadcastMessage(Utils.translateHex(message));
        }
    }
}
    protected void killStreakEachTime(Player killer, Player victim) {
            try {
                addPoint(killer);
                removePoint(victim);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
                int streak = getStreak(killer);
                Set<String> set = getPlugin().getConfig().getConfigurationSection("messages.killstreak").getKeys(false);
                int[] intArray = new int[set.size()];

                int x = 0;
                for (String key : set) {
                    intArray[x++] = Integer.parseInt(key);
                }
                Arrays.sort(intArray);
                int id = 0;
                for (int i = set.size() - 1; i >= 0; i--) {
                    if (streak >= intArray[i]) {
                        id = intArray[i];
                        break;
                    }
                }
                    if (set.contains(String.valueOf(id))) {
                        String message = getPlugin().getConfig().getString("messages.killstreak." + id).replaceAll("%killer%", killer.getName()).replaceAll("%victim%", victim.getName()).replaceAll("%killstreak%", String.valueOf(streak));
                        getPlugin().getServer().broadcastMessage(Utils.translateHex(message));
                    }
    }
    public void endKillstreakMessageEvent (Player killer, Player victim) {
        if (getPlugin().getConfig().getBoolean("endKillStreakEnable")) {
            int streak = getStreak(victim); //3
            Set<String> set = getPlugin().getConfig().getConfigurationSection("messages.endKillStreak").getKeys(false);
            //set 3 5 10
            int[] intArray = new int[set.size()];

            int x = 0;
            for (String key : set) {
                intArray[x] = Integer.parseInt(key);
                x++;
            }
            Arrays.sort(intArray); //3 5 10
            int id = 0;
            for (int i = set.size()-1; i >= 0; i--) {
                if (streak >= intArray[i]) {
                    id = intArray[i];
                    break;
                }
            }
            if (set.contains(String.valueOf(id))){
                String message = getPlugin().getConfig().getString("messages.endKillStreak." + id).replaceAll("%killer%", killer.getName()).replaceAll("%victim%", victim.getName()).replaceAll("%killstreak%", String.valueOf(streak));
                getPlugin().getServer().broadcastMessage(Utils.translateHex(message));
            }


        }
    }
}
