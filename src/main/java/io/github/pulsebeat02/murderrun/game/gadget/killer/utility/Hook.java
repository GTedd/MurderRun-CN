package io.github.pulsebeat02.murderrun.game.gadget.killer.utility;

import io.github.pulsebeat02.murderrun.game.gadget.killer.KillerGadget;
import io.github.pulsebeat02.murderrun.immutable.Keys;
import io.github.pulsebeat02.murderrun.locale.Locale;
import io.github.pulsebeat02.murderrun.utils.ItemUtils;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerFishEvent;
import org.bukkit.event.player.PlayerFishEvent.State;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.util.Vector;

public final class Hook extends KillerGadget implements Listener {

  public Hook() {
    super(
        "hook",
        Material.FISHING_ROD,
        Locale.HOOK_TRAP_NAME.build(),
        Locale.HOOK_TRAP_LORE.build(),
        32,
        stack -> ItemUtils.setPersistentDataAttribute(
            stack, Keys.HOOK, PersistentDataType.BOOLEAN, true));
  }

  @EventHandler
  public void onPlayerFish(final PlayerFishEvent event) {

    final State state = event.getState();
    if (state != State.CAUGHT_ENTITY) {
      return;
    }

    final Player killer = event.getPlayer();
    final PlayerInventory inventory = killer.getInventory();
    final ItemStack hand = inventory.getItemInMainHand();
    if (!ItemUtils.isHook(hand)) {
      return;
    }

    final Entity caught = event.getCaught();
    if (caught == null) {
      return;
    }

    final Vector multiplied = this.getMultipliedVelocity(killer, caught);
    caught.setVelocity(multiplied);
  }

  private Vector getMultipliedVelocity(final Player killer, final Entity caught) {
    final Location killerLocation = killer.getLocation();
    final Location caughtLocation = caught.getLocation();
    final Vector killerVector = killerLocation.toVector();
    final Vector caughtVector = caughtLocation.toVector();
    final Vector pullVector = killerVector.subtract(caughtVector);
    final Vector normalized = pullVector.normalize();
    return normalized.multiply(2);
  }
}