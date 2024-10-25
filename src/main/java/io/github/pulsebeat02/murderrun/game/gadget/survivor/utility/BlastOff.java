package io.github.pulsebeat02.murderrun.game.gadget.survivor.utility;

import static java.util.Objects.requireNonNull;

import io.github.pulsebeat02.murderrun.game.Game;
import io.github.pulsebeat02.murderrun.game.GameProperties;
import io.github.pulsebeat02.murderrun.game.gadget.packet.GadgetDropPacket;
import io.github.pulsebeat02.murderrun.game.gadget.survivor.SurvivorGadget;
import io.github.pulsebeat02.murderrun.game.player.GamePlayer;
import io.github.pulsebeat02.murderrun.game.player.PlayerAudience;
import io.github.pulsebeat02.murderrun.game.player.PlayerManager;
import io.github.pulsebeat02.murderrun.game.scheduler.GameScheduler;
import io.github.pulsebeat02.murderrun.locale.Message;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.entity.Firework;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.inventory.meta.FireworkMeta;

public final class BlastOff extends SurvivorGadget {

  public BlastOff() {
    super(
      "blast_off",
      Material.FIREWORK_ROCKET,
      Message.BLAST_OFF_NAME.build(),
      Message.BLAST_OFF_LORE.build(),
      GameProperties.BLAST_OFF_COST
    );
  }

  @Override
  public boolean onGadgetDrop(final GadgetDropPacket packet) {
    final Game game = packet.getGame();
    final GamePlayer player = packet.getPlayer();
    final Item item = packet.getItem();

    final Location location = player.getLocation();
    final PlayerManager manager = game.getPlayerManager();
    final GamePlayer killer = manager.getNearestKiller(location);
    if (killer == null) {
      return true;
    }
    item.remove();

    final Location before = killer.getLocation();
    final Firework firework = this.spawnRocket(killer);
    final GameScheduler scheduler = game.getScheduler();
    scheduler.scheduleAfterDeath(() -> this.resetPlayer(killer, before), firework);

    final PlayerAudience audience = player.getAudience();
    audience.playSound(GameProperties.BLASTOFF_SOUND);

    return false;
  }

  private void resetPlayer(final GamePlayer killer, final Location before) {
    killer.teleport(before);
    killer.setFallDistance(0.0f);
    killer.setCanDismount(true);
  }

  private Firework spawnRocket(final GamePlayer player) {
    final Location location = player.getLocation();
    final World world = requireNonNull(location.getWorld());
    return world.spawn(location, Firework.class, firework -> {
      this.customizeMeta(firework);
      this.customizeProperties(player, firework);
      player.setCanDismount(false);
    });
  }

  private void customizeProperties(final GamePlayer player, final Firework firework) {
    final Player internal = player.getInternalPlayer();
    firework.setShotAtAngle(false);
    firework.addPassenger(internal);
  }

  private void customizeMeta(final Firework firework) {
    final FireworkMeta meta = firework.getFireworkMeta();
    meta.setPower(5);
    firework.setFireworkMeta(meta);
  }
}
