package io.github.ezsk.cavern_detector;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EntityEquipment;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;


public class CavernDetector extends JavaPlugin {
    private static Material check_tool = Material.STICK;
    private static Sound knock_sound = Sound.BLOCK_BAMBOO_BREAK;
    public static Set<UUID> delaydlist = new HashSet<>();

    public static CavernDetector instance;


    public CavernDetector(){
        instance = this;
    }
    public static CavernDetector getInstance(){
        return instance;
    }

    @Override
    public void onEnable() {
        getServer().getPluginManager().registerEvents(new PlayerHandler(), this);
    }

    private static class PlayerHandler implements Listener {
        @EventHandler
        public void onPlayerUse (PlayerInteractEvent event){
            if(event.getAction().equals(Action.LEFT_CLICK_BLOCK)){
                Player p = event.getPlayer();
                EntityEquipment eq = p.getEquipment();
                if(delaydlist.contains(p.getUniqueId())){
                    return;
                } else {
                    delaydlist.add(p.getUniqueId());
                }
                Bukkit.getScheduler().scheduleSyncDelayedTask(CavernDetector.getInstance(), () -> delaydlist.remove(p.getUniqueId()), 10);
                if(eq != null && eq.getItemInMainHand().getType().equals(check_tool)){
                    Block block = event.getClickedBlock();
                    BlockFace face = event.getBlockFace();
                    if(block != null) {
                        int distance = 10;
                        for (int i = -1; i >= distance * -1; i--) {
                            Block tb = block.getRelative(face, i);
                            if (tb.getType().equals(Material.CAVE_AIR) || tb.getType().equals(Material.AIR)) {
                                block.getWorld().playSound(block.getLocation(), knock_sound, (float) (1 + ((i + 1) * 0.1)), i);
                                break;
                            }
                        }
                    }
                }
            }
        }
    }
}

