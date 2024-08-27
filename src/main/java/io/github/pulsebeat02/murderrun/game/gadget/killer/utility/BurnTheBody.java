package io.github.pulsebeat02.murderrun.game.gadget.killer.utility;

import static java.util.Objects.requireNonNull;

import io.github.pulsebeat02.murderrun.game.Game;
import io.github.pulsebeat02.murderrun.game.gadget.killer.KillerGadget;
import io.github.pulsebeat02.murderrun.game.player.GamePlayer;
import io.github.pulsebeat02.murderrun.game.player.PlayerManager;
import io.github.pulsebeat02.murderrun.game.player.death.DeathManager;
import io.github.pulsebeat02.murderrun.game.scheduler.GameScheduler;
import io.github.pulsebeat02.murderrun.locale.Message;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.World;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerDropItemEvent;

public final class BurnTheBody extends KillerGadget {

  public BurnTheBody() {
    super(
        "burn_the_body",
        Material.RED_STAINED_GLASS,
        Message.BURN_THE_BODY_NAME.build(),
        Message.BURN_THE_BODY_LORE.build(),
        48);
  }

  @Override
  public void onGadgetDrop(final Game game, final PlayerDropItemEvent event, final boolean remove) {

    super.onGadgetDrop(game, event, true);

    final Player player = event.getPlayer();
    final Location location = player.getLocation();
    final PlayerManager manager = game.getPlayerManager();
    final GamePlayer closest = manager.getNearestDeadSurvivor(location);
    if (closest == null) {
      return;
    }

    final Location deathLocation = requireNonNull(closest.getDeathLocation());
    final double distance = location.distanceSquared(deathLocation);
    if (distance <= 4) {
      final GameScheduler scheduler = game.getScheduler();
      manager.playSoundForAllParticipants("block.fire.ambient");
      this.destroyBody(scheduler, closest, deathLocation);
    }
  }

  private void destroyBody(
      final GameScheduler scheduler, final GamePlayer victim, final Location deathLocation) {
    final World world = requireNonNull(deathLocation.getWorld());
    scheduler.scheduleRepeatedTask(() -> this.summonEffects(deathLocation, world), 0, 20L, 5 * 20L);
    scheduler.scheduleTask(() -> this.handleBurnTasks(victim), 100);
  }

  private void summonEffects(final Location deathLocation, final World world) {
    world.spawnParticle(Particle.LAVA, deathLocation, 15, 1, 1, 1);
    world.strikeLightningEffect(deathLocation);
  }

  private void handleBurnTasks(final GamePlayer victim) {
    final DeathManager manager = victim.getDeathManager();
    victim.apply(player -> {
      final ArmorStand stand = manager.getCorpse();
      if (stand != null) {
        stand.remove();
      }
      player.setLastDeathLocation(null);
    });
  }
}
