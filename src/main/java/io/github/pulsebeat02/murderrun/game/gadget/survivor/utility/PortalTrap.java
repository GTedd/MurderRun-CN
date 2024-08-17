package io.github.pulsebeat02.murderrun.game.gadget.survivor.utility;

import static java.util.Objects.requireNonNull;

import io.github.pulsebeat02.murderrun.game.Game;
import io.github.pulsebeat02.murderrun.game.gadget.GadgetManager;
import io.github.pulsebeat02.murderrun.game.gadget.survivor.SurvivorGadget;
import io.github.pulsebeat02.murderrun.game.player.GamePlayer;
import io.github.pulsebeat02.murderrun.game.player.PlayerManager;
import io.github.pulsebeat02.murderrun.locale.Message;
import io.github.pulsebeat02.murderrun.utils.ItemUtils;
import java.util.ArrayList;
import java.util.Collection;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.inventory.ItemStack;
import org.checkerframework.checker.nullness.qual.Nullable;

public final class PortalTrap extends SurvivorGadget {

  public PortalTrap() {
    super(
        "portal",
        Material.PURPLE_WOOL,
        Message.PORTAL_NAME.build(),
        Message.PORTAL_LORE.build(),
        16);
  }

  @Override
  public void onGadgetDrop(final Game game, final PlayerDropItemEvent event, final boolean remove) {

    final GadgetManager gadgetManager = game.getGadgetManager();
    final int range = gadgetManager.getActivationRange();

    final Player player = event.getPlayer();
    final Location location = player.getLocation();
    final World world = requireNonNull(location.getWorld());
    final Collection<Item> items = this.getTrapItemStackEntities(location, world, range);
    final Item closest = this.getClosestEntity(location, items);
    if (closest == null) {
      super.onGadgetDrop(game, event, false);
      return;
    }

    final PlayerManager playerManager = game.getPlayerManager();
    final GamePlayer killer = requireNonNull(playerManager.getNearestKiller(location));
    final Location killerLocation = killer.getLocation();
    closest.teleport(killerLocation);

    super.onGadgetDrop(game, event, true);
  }

  private @Nullable Item getClosestEntity(final Location location, final Collection<Item> items) {
    Item closest = null;
    double closestDistance = Double.MAX_VALUE;
    for (final Item item : items) {
      final Location itemLocation = item.getLocation();
      final double distance = location.distance(itemLocation);
      if (distance < closestDistance) {
        closest = item;
        closestDistance = distance;
      }
    }
    return closest;
  }

  private Collection<Item> getTrapItemStackEntities(
      final Location location, final World world, final int range) {
    final Collection<Entity> entities = world.getNearbyEntities(location, range, range, range);
    final Collection<Item> trapEntities = new ArrayList<>();
    for (final Entity entity : entities) {
      if (!(entity instanceof final Item item)) {
        continue;
      }
      final ItemStack stack = item.getItemStack();
      if (ItemUtils.isGadget(stack)) {
        trapEntities.add(item);
      }
    }
    return trapEntities;
  }
}
