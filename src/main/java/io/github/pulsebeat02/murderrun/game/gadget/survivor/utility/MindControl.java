package io.github.pulsebeat02.murderrun.game.gadget.survivor.utility;

import io.github.pulsebeat02.murderrun.game.Game;
import io.github.pulsebeat02.murderrun.game.gadget.survivor.SurvivorGadget;
import io.github.pulsebeat02.murderrun.game.player.GamePlayer;
import io.github.pulsebeat02.murderrun.game.player.PlayerAudience;
import io.github.pulsebeat02.murderrun.game.player.PlayerManager;
import io.github.pulsebeat02.murderrun.game.player.Survivor;
import io.github.pulsebeat02.murderrun.game.scheduler.GameScheduler;
import io.github.pulsebeat02.murderrun.locale.Message;
import net.kyori.adventure.text.Component;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Item;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.util.Vector;

public final class MindControl extends SurvivorGadget {

  private static final int MIND_CONTROL_DURATION = 10 * 20;
  private static final String MIND_CONTROL_SOUND = "entity.enderman.scream";

  public MindControl() {
    super(
        "mind_control",
        Material.STRUCTURE_VOID,
        Message.MIND_CONTROL_NAME.build(),
        Message.MIND_CONTROL_LORE.build(),
        64);
  }

  @Override
  public boolean onGadgetDrop(
      final Game game, final GamePlayer player, final Item item, final boolean remove) {

    super.onGadgetDrop(game, player, item, true);

    final PlayerManager manager = game.getPlayerManager();
    final Location originLoc = player.getLocation();
    final GamePlayer nearest = manager.getNearestKiller(originLoc);
    if (nearest == null) {
      return true;
    }

    if (!(player instanceof final Survivor survivor)) {
      return true;
    }
    survivor.setCanPickupCarPart(false);

    final Location location = nearest.getLocation();
    player.addPotionEffects(
        new PotionEffect(PotionEffectType.INVISIBILITY, MIND_CONTROL_DURATION, 1));
    player.setInvulnerable(true);
    player.teleport(location);

    final Location origin = player.getLocation();
    final GameScheduler scheduler = game.getScheduler();
    scheduler.scheduleRepeatedTask(
        () -> this.applyMindControlEffects(player, nearest), 0L, 1L, MIND_CONTROL_DURATION);
    scheduler.scheduleTask(() -> this.resetPlayer(survivor, origin), MIND_CONTROL_DURATION);

    final String targetName = nearest.getDisplayName();
    final Component targetMsg = Message.MIND_CONTROL_ACTIVATE_SURVIVOR.build(targetName);
    final PlayerAudience audience1 = player.getAudience();
    audience1.sendMessage(targetMsg);
    audience1.playSound(MIND_CONTROL_SOUND);

    final String name = player.getDisplayName();
    final Component msg = Message.MIND_CONTROL_ACTIVATE_KILLER.build(name);
    final PlayerAudience audience = nearest.getAudience();
    audience.sendMessage(msg);

    return false;
  }

  private void resetPlayer(final Survivor player, final Location location) {
    player.teleport(location);
    player.setInvulnerable(false);
    player.setCanPickupCarPart(true);
  }

  private void applyMindControlEffects(final GamePlayer player, final GamePlayer killer) {
    final Location location = player.getLocation();
    final Vector velocity = player.getVelocity();
    killer.teleport(location);
    killer.setVelocity(velocity);
  }
}
