package io.github.pulsebeat02.murderrun.game.gadget.killer.utility;

import io.github.pulsebeat02.murderrun.game.Game;
import io.github.pulsebeat02.murderrun.game.gadget.killer.KillerGadget;
import io.github.pulsebeat02.murderrun.game.player.PlayerManager;
import io.github.pulsebeat02.murderrun.game.player.Survivor;
import io.github.pulsebeat02.murderrun.game.scheduler.GameScheduler;
import io.github.pulsebeat02.murderrun.locale.Locale;
import net.kyori.adventure.text.Component;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.event.player.PlayerDropItemEvent;

public final class BloodCurse extends KillerGadget {

  public BloodCurse() {
    super(
        "blood_curse",
        Material.REDSTONE_BLOCK,
        Locale.BLOOD_CURSE_TRAP_NAME.build(),
        Locale.BLOOD_CURSE_TRAP_LORE.build(),
        48);
  }

  @Override
  public void onGadgetDrop(final Game game, final PlayerDropItemEvent event, final boolean remove) {
    super.onGadgetDrop(game, event, true);
    final PlayerManager manager = game.getPlayerManager();
    manager.applyToAllInnocents(survivor -> this.scheduleTaskForSurvivors(game, survivor));
  }

  private void scheduleTaskForSurvivors(final Game game, final Survivor survivor) {

    final Component msg = Locale.BLOOD_CURSE_ACTIVATE.build();
    survivor.sendMessage(msg);

    final GameScheduler scheduler = game.getScheduler();
    scheduler.scheduleRepeatedTask(() -> this.setBloodBlock(survivor), 0, 20);
  }

  private void setBloodBlock(final Survivor survivor) {
    final Location location = survivor.getLocation();
    final Block block = location.getBlock();
    final Block replace = block.getRelative(BlockFace.UP);
    replace.setType(Material.REDSTONE);
  }
}
