package io.github.pulsebeat02.murderrun.game.gadget.survivor.trap;

import io.github.pulsebeat02.murderrun.game.Game;
import io.github.pulsebeat02.murderrun.game.player.GamePlayer;
import io.github.pulsebeat02.murderrun.game.scheduler.GameScheduler;
import io.github.pulsebeat02.murderrun.locale.Message;
import java.awt.Color;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

public final class HauntTrap extends SurvivorTrap {

  public HauntTrap() {
    super(
        "haunt",
        Material.WITHER_SKELETON_SKULL,
        Message.HAUNT_NAME.build(),
        Message.HAUNT_LORE.build(),
        Message.HAUNT_ACTIVATE.build(),
        32,
        Color.GRAY);
  }

  @Override
  public void onTrapActivate(final Game game, final GamePlayer murderer) {
    final GameScheduler scheduler = game.getScheduler();
    scheduler.scheduleRepeatedTask(() -> this.createSpookyEffect(game, murderer), 0, 20, 10 * 20L);
    murderer.addPotionEffects(
        new PotionEffect(PotionEffectType.NAUSEA, 10 * 20, 10),
        new PotionEffect(PotionEffectType.BLINDNESS, 10 * 20, 1),
        new PotionEffect(PotionEffectType.SLOWNESS, 10 * 20, 4));
  }

  private void createSpookyEffect(final Game game, final GamePlayer gamePlayer) {
    final Location location = gamePlayer.getLocation();
    final GameScheduler scheduler = game.getScheduler();
    gamePlayer.addPotionEffects(new PotionEffect(PotionEffectType.DARKNESS, 20, 10));
    gamePlayer.spawnParticle(Particle.ELDER_GUARDIAN, location, 1, 0, 0, 0);
    gamePlayer.addFakeWorldBorderEffect();
    scheduler.scheduleTask(() -> this.removeSpecialEffects(gamePlayer), 19);
  }

  private void removeSpecialEffects(final GamePlayer gamePlayer) {
    gamePlayer.apply(player -> player.removePotionEffect(PotionEffectType.DARKNESS));
    gamePlayer.removeFakeWorldBorderEffect();
  }
}
