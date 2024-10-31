package io.github.pulsebeat02.murderrun.game.gadget.survivor.tool;

import static java.util.Objects.requireNonNull;

import io.github.pulsebeat02.murderrun.game.Game;
import io.github.pulsebeat02.murderrun.game.GameProperties;
import io.github.pulsebeat02.murderrun.game.gadget.packet.GadgetDropPacket;
import io.github.pulsebeat02.murderrun.game.gadget.packet.GadgetRightClickPacket;
import io.github.pulsebeat02.murderrun.game.gadget.survivor.SurvivorGadget;
import io.github.pulsebeat02.murderrun.game.player.GamePlayer;
import io.github.pulsebeat02.murderrun.game.player.PlayerAudience;
import io.github.pulsebeat02.murderrun.game.player.PlayerManager;
import io.github.pulsebeat02.murderrun.immutable.Keys;
import io.github.pulsebeat02.murderrun.locale.Message;
import io.github.pulsebeat02.murderrun.resourcepack.sound.Sounds;
import io.github.pulsebeat02.murderrun.utils.PDCUtils;
import io.github.pulsebeat02.murderrun.utils.item.ItemFactory;
import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.Particle.DustOptions;
import org.bukkit.World;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.util.Vector;

public final class Flashlight extends SurvivorGadget {

  public Flashlight() {
    super(
      "flashlight",
      Material.GOLDEN_SHOVEL,
      Message.FLASHLIGHT_NAME.build(),
      Message.FLASHLIGHT_LORE.build(),
      GameProperties.FLASHLIGHT_COST,
      ItemFactory::createFlashlight
    );
  }

  @Override
  public boolean onGadgetDrop(final GadgetDropPacket packet) {
    return true;
  }

  @Override
  public boolean onGadgetRightClick(final GadgetRightClickPacket packet) {
    final ItemStack stack = packet.getItemStack();
    if (stack == null) {
      return true;
    }

    final Long last = PDCUtils.getPersistentDataAttribute(stack, Keys.FLASHLIGHT_USE, PersistentDataType.LONG);
    if (last == null) {
      return true;
    }

    final long current = System.currentTimeMillis();
    final long difference = current - last;
    if (difference < 5000) {
      return true;
    }

    PDCUtils.setPersistentDataAttribute(stack, Keys.FLASHLIGHT_USE, PersistentDataType.LONG, current);

    final Game game = packet.getGame();
    final GamePlayer player = packet.getPlayer();
    this.sprayParticlesInCone(game, player);

    final PlayerAudience audience = player.getAudience();
    audience.playSound(Sounds.FLASHLIGHT);

    return false;
  }

  private void sprayParticlesInCone(final Game game, final GamePlayer player) {
    final PlayerManager manager = game.getPlayerManager();
    final Location handLocation = player.getEyeLocation();
    final World world = requireNonNull(handLocation.getWorld());
    final Vector direction = handLocation.getDirection();
    final double increment = Math.toRadians(5);
    final double maxAngle = Math.toRadians(GameProperties.FLASHLIGHT_CONE_ANGLE);
    for (double t = 0; t < GameProperties.FLASHLIGHT_CONE_LENGTH; t += 0.5) {
      for (double angle = -maxAngle; angle <= maxAngle; angle += increment) {
        final Location particleLocation = this.getParticleLocation(direction, handLocation, t, angle);
        world.spawnParticle(Particle.DUST, particleLocation, 1, 0, 0, 0, 0, new DustOptions(Color.YELLOW, 3));
        manager.applyToAllMurderers(killer -> this.applyPotionEffects(killer, particleLocation));
      }
    }
  }

  private Location getParticleLocation(final Vector direction, final Location handLocation, final double t, final double angle) {
    final Vector copy = direction.clone();
    final Vector offset = copy.multiply(t);
    offset.rotateAroundY(angle);

    final Location hand = handLocation.clone();
    return hand.add(offset);
  }

  private void applyPotionEffects(final GamePlayer killer, final Location particleLocation) {
    final Location killerLocation = killer.getLocation();
    final double distance = killerLocation.distanceSquared(particleLocation);
    final double radius = GameProperties.FLASHLIGHT_RADIUS;
    if (distance < radius * radius) {
      killer.addPotionEffects(new PotionEffect(PotionEffectType.BLINDNESS, 5 * 20, Integer.MAX_VALUE));
    }
  }
}