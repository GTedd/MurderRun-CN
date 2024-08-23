package io.github.pulsebeat02.murderrun.game.gadget.survivor.trap;

import static net.kyori.adventure.key.Key.key;

import io.github.pulsebeat02.murderrun.game.Game;
import io.github.pulsebeat02.murderrun.game.player.GamePlayer;
import io.github.pulsebeat02.murderrun.game.scheduler.GameScheduler;
import io.github.pulsebeat02.murderrun.locale.Message;
import io.github.pulsebeat02.murderrun.utils.PDCUtils;
import java.awt.Color;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.checkerframework.checker.nullness.qual.Nullable;

public final class HackTrap extends SurvivorTrap {

  public HackTrap() {
    super(
        "hack",
        Material.EMERALD_BLOCK,
        Message.HACK_NAME.build(),
        Message.HACK_LORE.build(),
        Message.HACK_ACTIVATE.build(),
        48,
        Color.GREEN);
  }

  @Override
  public void onTrapActivate(final Game game, final GamePlayer murderer) {
    final ItemStack stack = this.removeSwordItemStack(murderer);
    final GameScheduler scheduler = game.getScheduler();
    if (stack != null) {
      scheduler.scheduleTask(() -> this.giveSwordBack(murderer, stack), 7 * 20L);
    }
    murderer.playSound(key("entity.witch.celebrate"));
  }

  private @Nullable ItemStack removeSwordItemStack(final GamePlayer player) {
    final PlayerInventory inventory = player.getInventory();
    final ItemStack[] slots = inventory.getContents();
    ItemStack find = null;
    for (final ItemStack stack : slots) {
      if (!PDCUtils.isSword(stack)) {
        continue;
      }
      inventory.remove(stack);
      find = stack;
    }
    return find;
  }

  private void giveSwordBack(final GamePlayer player, final ItemStack stack) {
    final PlayerInventory inventory = player.getInventory();
    if (stack != null) {
      inventory.addItem(stack);
    }
  }
}
