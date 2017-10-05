package io.totemo.schemaccess;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.plugin.java.JavaPlugin;

// ----------------------------------------------------------------------------
/**
 * Main plugin class.
 * 
 * <ul>
 * <li>Set up player name symlink to user UUID on login</li>
 * <li>TODO: /schem-password command for per-user password(?).</li>
 * </ul>
 */
public class SchemAccess extends JavaPlugin implements Listener {
    // ------------------------------------------------------------------------
    /**
     * @see org.bukkit.plugin.java.JavaPlugin#onEnable()
     */
    @Override
    public void onEnable() {
        saveDefaultConfig();

        getServer().getPluginManager().registerEvents(this, this);
    }

    // ------------------------------------------------------------------------
    /**
     * On player join, set up playername -> uuid symlink in
     * plugins/WorldEdit/schematics/.
     */
    @EventHandler(ignoreCancelled = true)
    public void onPlayerJoin(PlayerJoinEvent event) {
        Path dataPath = getDataFolder().toPath();
        Path schemDir = dataPath.getParent().resolve("WorldEdit/schematics");

        Player player = event.getPlayer();
        Path link = schemDir.resolve(player.getName());
        if (!Files.isSymbolicLink(link)) {
            Path target = schemDir.resolve(player.getUniqueId().toString());
            try {
                getLogger().info("Creating schematics link: " + player.getName() + " -> " + player.getUniqueId());
                Files.createSymbolicLink(link, schemDir.relativize(target));
            } catch (IOException ex) {
                getLogger().info("Unable to create symlink for " + player.getName());
            }
        }
    }
} // class SchemAccess