package io.github.pulsebeat02.murderrun.game.gadget.survivor.utility;

import io.github.pulsebeat02.murderrun.game.Game;
import io.github.pulsebeat02.murderrun.game.gadget.Gadget;
import io.github.pulsebeat02.murderrun.game.gadget.GadgetLoadingMechanism;
import io.github.pulsebeat02.murderrun.game.gadget.GadgetManager;
import io.github.pulsebeat02.murderrun.game.gadget.survivor.SurvivorGadget;
import io.github.pulsebeat02.murderrun.locale.Message;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;

public final class RandomTrap extends SurvivorGadget {

  public RandomTrap() {
    super(
        "random",
        Material.END_PORTAL_FRAME,
        Message.RANDOM_NAME.build(),
        Message.RANDOM_LORE.build(),
        16);
  }

  @Override
  public void onGadgetDrop(final Game game, final PlayerDropItemEvent event, final boolean remove) {
    super.onGadgetDrop(game, event, true);
    final Player player = event.getPlayer();
    final GadgetManager manager = game.getGadgetManager();
    final GadgetLoadingMechanism mechanism = manager.getMechanism();
    final Gadget gadget = mechanism.getRandomInnocentGadget();
    final ItemStack stack = gadget.getGadget();
    final PlayerInventory inventory = player.getInventory();
    inventory.addItem(stack);
  }
}
