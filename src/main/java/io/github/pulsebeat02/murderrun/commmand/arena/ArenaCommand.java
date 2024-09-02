package io.github.pulsebeat02.murderrun.commmand.arena;

import static java.util.Objects.requireNonNull;

import io.github.pulsebeat02.murderrun.MurderRun;
import io.github.pulsebeat02.murderrun.commmand.AnnotationCommandFeature;
import io.github.pulsebeat02.murderrun.game.arena.Arena;
import io.github.pulsebeat02.murderrun.game.arena.ArenaManager;
import io.github.pulsebeat02.murderrun.locale.AudienceProvider;
import io.github.pulsebeat02.murderrun.locale.Message;
import io.github.pulsebeat02.murderrun.utils.AdventureUtils;
import io.github.pulsebeat02.murderrun.utils.item.ItemFactory;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.platform.bukkit.BukkitAudiences;
import net.kyori.adventure.text.Component;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.incendo.cloud.annotation.specifier.Quoted;
import org.incendo.cloud.annotations.AnnotationParser;
import org.incendo.cloud.annotations.Command;
import org.incendo.cloud.annotations.CommandDescription;
import org.incendo.cloud.annotations.Permission;

public final class ArenaCommand implements AnnotationCommandFeature {

  private MurderRun plugin;
  private BukkitAudiences audiences;
  private WandListener listener;

  private String name; // anvil gui
  private Location spawn;
  private Location truck;
  private Location first;
  private Location second;
  private Collection<Location> itemLocations; // get stick

  @Override
  public void registerFeature(
      final MurderRun plugin, final AnnotationParser<CommandSender> parser) {
    final AudienceProvider handler = plugin.getAudience();
    this.audiences = handler.retrieve();
    this.plugin = plugin;
    this.itemLocations = new HashSet<>();
    this.listener = new WandListener(plugin, this);
    this.listener.runScheduledTask();
  }

  @Permission("murderrun.command.arena.wand")
  @CommandDescription("murderrun.command.arena.wand.info")
  @Command(value = "murder arena wand", requiredSender = Player.class)
  public void retrieveItemWand(final Player player) {
    final PlayerInventory inventory = player.getInventory();
    final ItemStack wand = ItemFactory.createItemLocationWand();
    inventory.addItem(wand);
  }

  @Permission("murderrun.command.arena.copy")
  @CommandDescription("murderrun.command.arena.copy.info")
  @Command(value = "murder arena copy <name>", requiredSender = Player.class)
  public void copyArenaSettings(final Player sender, @Quoted final String name) {

    final Audience audience = this.audiences.player(sender);
    if (this.checkInvalidArena(audience, name)) {
      return;
    }

    final ArenaManager manager = this.plugin.getArenaManager();
    final Map<String, Arena> arenas = manager.getArenas();
    final Arena arena = requireNonNull(arenas.get(name));
    final List<Location> locations = Arrays.asList(arena.getCarPartLocations());
    this.name = arena.getName();
    this.spawn = arena.getSpawn();
    this.truck = arena.getTruck();
    this.first = arena.getFirstCorner();
    this.second = arena.getSecondCorner();
    this.itemLocations = new ArrayList<>(locations);

    final Component msg = Message.ARENA_COPY.build();
    audience.sendMessage(msg);
  }

  @Permission("murderrun.command.arena.set.item.add")
  @CommandDescription("murderrun.command.arena.set.item.add.info")
  @Command(value = "murder arena set item add", requiredSender = Player.class)
  public void addItemLocation(final Player sender) {
    final Location location = sender.getLocation();
    this.addItemLocation(sender, location);
  }

  public void addItemLocation(final Player sender, final Location location) {

    final Block block = location.getBlock();
    final Location blockLoc = block.getLocation();
    this.itemLocations.add(blockLoc);

    final Component msg = AdventureUtils.createLocationComponent(Message.ARENA_ITEM_ADD, blockLoc);
    this.sendMessage(sender, msg);
  }

  @Permission("murderrun.command.arena.set.item.remove")
  @CommandDescription("murderrun.command.arena.set.item.remove.info")
  @Command(value = "murder arena set item remove", requiredSender = Player.class)
  public void removeItemLocation(final Player sender) {
    final Location location = sender.getLocation();
    this.removeItemLocation(sender, location);
  }

  public void removeItemLocation(final Player sender, final Location location) {
    final Block block = location.getBlock();
    final Location blockLoc = block.getLocation();
    if (this.itemLocations.remove(blockLoc)) {
      final Component msg =
          AdventureUtils.createLocationComponent(Message.ARENA_ITEM_REMOVE, blockLoc);
      this.sendMessage(sender, msg);
    } else {
      final Component err = Message.ARENA_ITEM_REMOVE_ERROR.build();
      this.sendMessage(sender, err);
    }
  }

  @Permission("murderrun.command.arena.set.item.list")
  @CommandDescription("murderrun.command.arena.set.item.list.info")
  @Command(value = "murder arena set item list", requiredSender = Player.class)
  public void listItemLocations(final Player sender) {
    final List<String> msgs = new ArrayList<>();
    for (final Location location : this.itemLocations) {
      final int x = location.getBlockX();
      final int y = location.getBlockY();
      final int z = location.getBlockZ();
      final String raw = "(%s,%s,%s)".formatted(x, y, z);
      msgs.add(raw);
    }
    final Component msg = Message.ARENA_ITEM_LIST.build(msgs);
    this.sendMessage(sender, msg);
  }

  @Permission("murderrun.command.arena.list")
  @CommandDescription("murderrun.command.arena.list.info")
  @Command(value = "murder arena list", requiredSender = Player.class)
  public void listArenas(final Player sender) {
    final ArenaManager manager = this.plugin.getArenaManager();
    final Map<String, Arena> arenas = manager.getArenas();
    final List<String> keys = new ArrayList<>(arenas.keySet());
    final Component message = Message.ARENA_LIST.build(keys);
    this.sendMessage(sender, message);
  }

  @Permission("murderrun.command.arena.remove")
  @CommandDescription("murderrun.command.arena.remove.info")
  @Command(value = "murder arena remove <name>", requiredSender = Player.class)
  public void removeArena(final Player sender, final String name) {
    final Audience audience = this.audiences.player(sender);
    if (this.checkInvalidArena(audience, name)) {
      return;
    }
    final ArenaManager manager = this.plugin.getArenaManager();
    manager.removeArena(name);
    final Component message = Message.ARENA_REMOVE.build(name);
    this.sendMessage(sender, message);
  }

  private boolean checkInvalidArena(final Audience audience, final String name) {
    final ArenaManager manager = this.plugin.getArenaManager();
    final Map<String, Arena> arenas = manager.getArenas();
    final Arena arena = arenas.get(name);
    if (arena == null) {
      final Component message = Message.ARENA_REMOVE_ERROR.build();
      audience.sendMessage(message);
      return true;
    }
    return false;
  }

  private void sendMessage(final Player player, final Component component) {
    final Audience audience = this.audiences.player(player);
    audience.sendMessage(component);
  }

  @Permission("murderrun.command.arena.create")
  @CommandDescription("murderrun.command.arena.create.info")
  @Command(value = "murder arena create", requiredSender = Player.class)
  public void createArena(final Player sender) {
    final Audience audience = this.audiences.player(sender);
    if (this.handleNullCorner(audience)
        || this.handleNullSpawn(audience)
        || this.handleNullTruck(audience)
        || this.handleNullName(audience)) {
      return;
    }
    final Location[] corners = new Location[] {this.first, this.second};
    final ArenaManager manager = this.plugin.getArenaManager();
    final Location[] locations = this.itemLocations.toArray(new Location[0]);
    manager.addArena(this.name, corners, locations, this.spawn, this.truck);
    final Component message = Message.ARENA_BUILT.build();
    audience.sendMessage(message);
    this.plugin.updatePluginData();
  }

  private boolean handleNullCorner(final Audience audience) {
    if (this.first == null || this.second == null) {
      final Component message = Message.ARENA_CORNER_ERROR.build();
      audience.sendMessage(message);
      return true;
    }
    return false;
  }

  private boolean handleNullSpawn(final Audience audience) {
    if (this.spawn == null) {
      final Component message = Message.ARENA_SPAWN_ERROR.build();
      audience.sendMessage(message);
      return true;
    }
    return false;
  }

  private boolean handleNullTruck(final Audience audience) {
    if (this.truck == null) {
      final Component message = Message.ARENA_TRUCK_ERROR.build();
      audience.sendMessage(message);
      return true;
    }
    return false;
  }

  private boolean handleNullName(final Audience audience) {
    if (this.name == null) {
      final Component message = Message.ARENA_NAME_ERROR.build();
      audience.sendMessage(message);
      return true;
    }
    return false;
  }

  @Permission("murderrun.command.arena.set.name")
  @CommandDescription("murderrun.command.arena.set.name.info")
  @Command(value = "murder arena set name <name>", requiredSender = Player.class)
  public void setName(final Player sender, @Quoted final String name) {
    this.name = name;
    final Component message = Message.ARENA_NAME.build(name);
    this.sendMessage(sender, message);
  }

  @Permission("murderrun.command.arena.set.spawn")
  @CommandDescription("murderrun.command.arena.set.spawn.info")
  @Command(value = "murder arena set spawn", requiredSender = Player.class)
  public void setSpawn(final Player sender) {
    final Location location = sender.getLocation();
    this.spawn = location;
    final Component message = AdventureUtils.createLocationComponent(Message.ARENA_SPAWN, location);
    this.sendMessage(sender, message);
  }

  @Permission("murderrun.command.arena.set.truck")
  @CommandDescription("murderrun.command.arena.set.truck.info")
  @Command(value = "murder arena set truck", requiredSender = Player.class)
  public void setTruck(final Player sender) {
    final Location location = sender.getLocation();
    this.truck = location;
    final Component message = AdventureUtils.createLocationComponent(Message.ARENA_TRUCK, location);
    this.sendMessage(sender, message);
  }

  @Permission("murderrun.command.arena.set.first-corner")
  @CommandDescription("murderrun.command.arena.set.first_corner.info")
  @Command(value = "murder arena set first-corner", requiredSender = Player.class)
  public void setFirstCorner(final Player sender) {
    final Location location = sender.getLocation();
    this.first = location;
    final Component message =
        AdventureUtils.createLocationComponent(Message.ARENA_FIRST_CORNER, location);
    this.sendMessage(sender, message);
  }

  @Permission("murderrun.command.arena.set.second-corner")
  @CommandDescription("murderrun.command.arena.set.second_corner.info")
  @Command(value = "murder arena set second-corner", requiredSender = Player.class)
  public void setSecondCorner(final Player sender) {
    final Location location = sender.getLocation();
    this.second = location;
    final Component message =
        AdventureUtils.createLocationComponent(Message.ARENA_SECOND_CORNER, location);
    this.sendMessage(sender, message);
  }

  public Collection<Location> getItemLocations() {
    return this.itemLocations;
  }
}
