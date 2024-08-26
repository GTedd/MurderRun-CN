package io.github.pulsebeat02.murderrun.game.gadget.survivor.utility;

import static java.util.Objects.requireNonNull;

import io.github.pulsebeat02.murderrun.game.Game;
import io.github.pulsebeat02.murderrun.game.gadget.survivor.SurvivorGadget;
import io.github.pulsebeat02.murderrun.game.player.PlayerManager;
import io.github.pulsebeat02.murderrun.game.scheduler.GameScheduler;
import io.github.pulsebeat02.murderrun.immutable.Keys;
import io.github.pulsebeat02.murderrun.locale.Message;
import io.github.pulsebeat02.murderrun.resourcepack.sound.Sounds;
import io.github.pulsebeat02.murderrun.utils.PDCUtils;
import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.Particle.DustOptions;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Snowball;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.ProjectileHitEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.potion.PotionEffectType;

public final class SmokeGrenade extends SurvivorGadget implements Listener {

  private final Game game;

  public SmokeGrenade(final Game game) {
    super(
        "smoke_grenade",
        Material.SNOWBALL,
        Message.SMOKE_BOMB_NAME.build(),
        Message.SMOKE_BOMB_LORE.build(),
        16,
        stack -> PDCUtils.setPersistentDataAttribute(
            stack, Keys.SMOKE_GRENADE, PersistentDataType.BOOLEAN, true));
    this.game = game;
  }

  @Override
  public void onGadgetRightClick(
      final Game game, final PlayerInteractEvent event, final boolean remove) {
    // ignore impl
  }

  @EventHandler
  public void onProjectileHitEvent(final ProjectileHitEvent event) {

    final Entity entity = event.getEntity();
    if (!(entity instanceof final Snowball snowball)) {
      return;
    }

    final ItemStack stack = snowball.getItem();
    if (!PDCUtils.isSmokeGrenade(stack)) {
      return;
    }

    final Block block = event.getHitBlock();
    final Entity hitEntity = event.getHitEntity();
    final Location location;
    if (block == null) {
      if (hitEntity == null) {
        return;
      }
      location = hitEntity.getLocation();
    } else {
      location = block.getLocation();
    }

    final World world = requireNonNull(location.getWorld());
    final GameScheduler scheduler = this.game.getScheduler();
    scheduler.scheduleRepeatedTask(
        () -> world.spawnParticle(
            Particle.DUST, location, 10, 1, 1, 1, new DustOptions(Color.GRAY, 4)),
        0,
        1,
        5 * 20L);

    final PlayerManager manager = this.game.getPlayerManager();
    manager.playSoundForAllParticipantsAtLocation(location, Sounds.FLASHBANG);

    manager.applyToAllMurderers(player -> {
      final Location playerLocation = player.getLocation();
      final double distance = playerLocation.distanceSquared(location);
      if (distance < 4) {
        player.addPotionEffects(PotionEffectType.BLINDNESS.createEffect(5 * 20, Integer.MAX_VALUE));
      }
    });
  }
}
