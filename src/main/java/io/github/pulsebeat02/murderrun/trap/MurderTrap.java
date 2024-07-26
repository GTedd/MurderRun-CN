package io.github.pulsebeat02.murderrun.trap;

import io.github.pulsebeat02.murderrun.game.MurderGame;
import io.github.pulsebeat02.murderrun.player.Murderer;
import org.bukkit.Bukkit;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginManager;
import org.bukkit.scheduler.BukkitScheduler;
import org.checkerframework.checker.initialization.qual.UnderInitialization;

public abstract sealed class MurderTrap implements Listener permits SurvivorTrap, KillerTrap {

  private final ItemStack stack;
  private final String name;

  public MurderTrap(final String name) {
    this.name = name;
    this.stack = this.constructItemStack();
  }

  public abstract ItemStack constructItemStack(@UnderInitialization MurderTrap this);

  public ItemStack getStack() {
    return this.stack;
  }

  public String getName() {
    return this.name;
  }

  public void scheduleTask(final Runnable runnable, final long delay) {
    final PluginManager manager = Bukkit.getPluginManager();
    final Plugin plugin = manager.getPlugin("MurderRun");
    final BukkitScheduler scheduler = Bukkit.getScheduler();
    if (plugin == null) {
      throw new AssertionError("Unable to retrieve plugin class!");
    }
    scheduler.runTaskLater(plugin, runnable, delay);
  }

  public abstract void onDropEvent(final PlayerDropItemEvent event);

  public abstract void activate(final MurderGame game, final Murderer murderer);
}
