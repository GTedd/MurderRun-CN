package io.github.pulsebeat02.murderrun.game.gadget.killer.utility;

import io.github.pulsebeat02.murderrun.game.Game;
import io.github.pulsebeat02.murderrun.game.GameProperties;
import io.github.pulsebeat02.murderrun.game.gadget.killer.KillerGadget;
import io.github.pulsebeat02.murderrun.game.gadget.packet.GadgetDropPacket;
import io.github.pulsebeat02.murderrun.game.player.GamePlayer;
import io.github.pulsebeat02.murderrun.game.player.PlayerAudience;
import io.github.pulsebeat02.murderrun.game.scheduler.GameScheduler;
import io.github.pulsebeat02.murderrun.locale.Message;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Item;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

public final class FireTrail extends KillerGadget {

  public FireTrail() {
    super(
      "fire_trail",
      Material.MAGMA_CREAM,
      Message.FIRE_TRAIL_NAME.build(),
      Message.FIRE_TRAIL_LORE.build(),
      GameProperties.FIRE_TRAIL_COST
    );
  }

  @Override
  public boolean onGadgetDrop(final GadgetDropPacket packet) {
    final Game game = packet.getGame();
    final GamePlayer player = packet.getPlayer();
    final Item item = packet.getItem();
    item.remove();

    player.addPotionEffects(new PotionEffect(PotionEffectType.FIRE_RESISTANCE, PotionEffect.INFINITE_DURATION, 1));

    final GameScheduler scheduler = game.getScheduler();
    scheduler.scheduleRepeatedTask(() -> this.spawnFire(player), 0, 4);

    final PlayerAudience audience = player.getAudience();
    audience.playSound(GameProperties.FIRE_TRAIL_SOUND);

    return false;
  }

  private void spawnFire(final GamePlayer player) {
    final Location location = player.getLocation();
    final Block block = location.getBlock();
    block.setType(Material.FIRE);
  }
}
