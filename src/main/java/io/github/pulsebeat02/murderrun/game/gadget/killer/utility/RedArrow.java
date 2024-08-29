package io.github.pulsebeat02.murderrun.game.gadget.killer.utility;

import io.github.pulsebeat02.murderrun.game.Game;
import io.github.pulsebeat02.murderrun.game.gadget.killer.KillerGadget;
import io.github.pulsebeat02.murderrun.game.player.GamePlayer;
import io.github.pulsebeat02.murderrun.game.player.PlayerAudience;
import io.github.pulsebeat02.murderrun.game.player.PlayerManager;
import io.github.pulsebeat02.murderrun.game.scheduler.GameScheduler;
import io.github.pulsebeat02.murderrun.locale.Message;
import io.github.pulsebeat02.murderrun.utils.item.ItemFactory;
import net.kyori.adventure.text.Component;
import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.Particle.DustOptions;
import org.bukkit.World;
import org.bukkit.entity.Item;

public final class RedArrow extends KillerGadget {

  private static final String RED_ARROW_SOUND = "entity.arrow.hit";
  private static final int RED_ARROW_DURATION = 10 * 20;

  public RedArrow() {
    super(
        "red_arrow",
        Material.TIPPED_ARROW,
        Message.RED_ARROW_NAME.build(),
        Message.RED_ARROW_LORE.build(),
        64,
        ItemFactory::createRedArrow);
  }

  @Override
  public boolean onGadgetDrop(
      final Game game, final GamePlayer player, final Item item, final boolean remove) {

    super.onGadgetDrop(game, player, item, true);

    final PlayerManager manager = game.getPlayerManager();
    final GameScheduler scheduler = game.getScheduler();
    scheduler.scheduleRepeatedTask(() -> this.handleSurvivors(manager), 0, RED_ARROW_DURATION);

    final PlayerAudience audience = player.getAudience();
    final Component message = Message.RED_ARROW_ACTIVATE.build();
    audience.sendMessage(message);
    audience.playSound(RED_ARROW_SOUND);

    return false;
  }

  private void handleSurvivors(final PlayerManager manager) {
    manager.applyToAllLivingInnocents(this::spawnParticleBeam);
  }

  private void spawnParticleBeam(final GamePlayer player) {

    final Location location = player.getLocation();
    final World world = location.getWorld();
    if (world == null) {
      return;
    }

    final double startY = location.getY();
    final double skyLimit = world.getMaxHeight();
    final double x = location.getX();
    final double z = location.getZ();

    for (double y = startY; y <= skyLimit; y += 1.0) {
      final Location particleLocation = new Location(world, x, y, z);
      world.spawnParticle(Particle.DUST, particleLocation, 1, new DustOptions(Color.RED, 4));
    }
  }
}
