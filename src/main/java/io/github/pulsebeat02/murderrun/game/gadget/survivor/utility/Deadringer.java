package io.github.pulsebeat02.murderrun.game.gadget.survivor.utility;

import io.github.pulsebeat02.murderrun.game.Game;
import io.github.pulsebeat02.murderrun.game.gadget.survivor.SurvivorGadget;
import io.github.pulsebeat02.murderrun.game.player.GamePlayer;
import io.github.pulsebeat02.murderrun.game.player.PlayerAudience;
import io.github.pulsebeat02.murderrun.game.player.PlayerManager;
import io.github.pulsebeat02.murderrun.game.scheduler.GameScheduler;
import io.github.pulsebeat02.murderrun.locale.Message;
import net.kyori.adventure.text.Component;
import org.bukkit.Material;
import org.bukkit.entity.Item;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

public final class Deadringer extends SurvivorGadget {

  private static final int DEADRINGER_DURATION = 15 * 20;
  private static final String DEADRINGER_SOUND = "item.totem.use";

  public Deadringer() {
    super(
        "deadringer",
        Material.ZOMBIE_HEAD,
        Message.DEADRINGER_NAME.build(),
        Message.DEADRINGER_LORE.build(),
        48);
  }

  @Override
  public boolean onGadgetDrop(
      final Game game, final GamePlayer player, final Item item, final boolean remove) {

    super.onGadgetDrop(game, player, item, true);

    player.setInvulnerable(true);
    player.addPotionEffects(
        new PotionEffect(PotionEffectType.SPEED, DEADRINGER_DURATION, 1, true, false),
        new PotionEffect(PotionEffectType.INVISIBILITY, DEADRINGER_DURATION, 1, true, false));

    final GameScheduler scheduler = game.getScheduler();
    scheduler.scheduleTask(() -> player.setInvulnerable(false), DEADRINGER_DURATION);

    final PlayerManager manager = game.getPlayerManager();
    final String name = player.getDisplayName();
    final Component message = Message.PLAYER_DEATH.build(name);
    manager.sendMessageToAllParticipants(message);

    final PlayerAudience audience = player.getAudience();
    audience.playSound(DEADRINGER_SOUND);

    return false;
  }
}
