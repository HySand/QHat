package org.circube.qhat;

import net.coreprotect.CoreProtect;
import net.coreprotect.CoreProtectAPI;
import org.bukkit.Location;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.bukkit.Bukkit.getServer;

public class CoreProtectHandler {
    private static final QHat plugin = QHat.getPlugin(QHat.class);

    private static CoreProtectAPI getCoreProtect() {
        Plugin plugin = getServer().getPluginManager().getPlugin("CoreProtect");

        // Check that CoreProtect is loaded
        if (!(plugin instanceof CoreProtect)) {
            return null;
        }

        // Check that the API is enabled
        CoreProtectAPI CoreProtect = ((CoreProtect) plugin).getAPI();
        if (!CoreProtect.isEnabled()) {
            return null;
        }

        // Check that a compatible version of the API is loaded
        if (CoreProtect.APIVersion() < 10) {
            return null;
        }

        return CoreProtect;
    }

    public static void rollBackPlacedBlocks(Location location) {
        new BukkitRunnable() {
            @Override
            public void run() {
                try {
                    CoreProtectAPI api = getCoreProtect();
                    if (api == null) return;
                    List<Integer> actionList = new ArrayList<>(Arrays.asList(1, 0));
                    api.performRollback(200, null, null, null, null, actionList, 200, location);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }.runTaskAsynchronously(plugin);
    }
}
