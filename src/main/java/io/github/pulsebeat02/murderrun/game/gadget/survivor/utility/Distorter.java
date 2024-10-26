package io.github.pulsebeat02.murderrun.game.gadget.survivor.utility;

import io.github.pulsebeat02.murderrun.game.Game;
import io.github.pulsebeat02.murderrun.game.GameProperties;
import io.github.pulsebeat02.murderrun.game.gadget.packet.GadgetDropPacket;
import io.github.pulsebeat02.murderrun.game.gadget.survivor.SurvivorGadget;
import io.github.pulsebeat02.murderrun.game.player.GamePlayer;
import io.github.pulsebeat02.murderrun.game.player.PlayerAudience;
import io.github.pulsebeat02.murderrun.game.player.PlayerManager;
import io.github.pulsebeat02.murderrun.game.scheduler.GameScheduler;
import io.github.pulsebeat02.murderrun.locale.Message;
import java.util.HashSet;
import java.util.Set;
import net.kyori.adventure.text.Component;
import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.entity.Item;

public final class Distorter extends SurvivorGadget {

  private final Set<Integer> removed;

  public Distorter() {
    super(
      "distorter",
      Material.CHORUS_FLOWER,
      Message.DISTORTER_NAME.build(),
      Message.DISTORTER_LORE.build(),
      GameProperties.DISTORTER_COST
    );
    this.removed = new HashSet<>();
  }

  @Override
  public boolean onGadgetDrop(final GadgetDropPacket packet) {
    final Game game = packet.getGame();
    final GamePlayer player = packet.getPlayer();
    final Item item = packet.getItem();
    final PlayerManager manager = game.getPlayerManager();
    final GameScheduler scheduler = game.getScheduler();
    scheduler.scheduleTaskUntilDeath(() -> this.handleKillers(manager, item), item);
    scheduler.scheduleParticleTaskUntilDeath(item, Color.PURPLE);

    final PlayerAudience audience = player.getAudience();
    audience.playSound(GameProperties.DISTORTER_SOUND);

    return false;
  }

  private void handleKillers(final PlayerManager manager, final Item item) {
    manager.applyToAllMurderers(killer -> this.applyDistortionEffect(manager, killer, item));
  }

  private void applyDistortionEffect(final PlayerManager manager, final GamePlayer killer, final Item item) {
    final Location location = killer.getLocation();
    final Location origin = item.getLocation();
    final double distance = location.distanceSquared(origin);
    final double destroyRadius = GameProperties.DISTORTER_DESTROY_RADIUS;
    final double effectRadius = GameProperties.DISTORTER_EFFECT_RADIUS;
    final int id = item.getEntityId();
    if (distance < destroyRadius * destroyRadius && !this.removed.contains(id)) {
      final Component message = Message.DISTORTER_DEACTIVATE.build();
      manager.sendMessageToAllSurvivors(message);
      item.remove();
      this.removed.add(id);
    } else if (distance < effectRadius * effectRadius) {
      killer.spawnPlayerSpecificParticle(Particle.ELDER_GUARDIAN);
    }
  }
}
