package io.github.pulsebeat02.murderrun.game.gadget.killer.utility;

import io.github.pulsebeat02.murderrun.game.Game;
import io.github.pulsebeat02.murderrun.game.gadget.killer.KillerGadget;
import io.github.pulsebeat02.murderrun.game.player.GamePlayer;
import io.github.pulsebeat02.murderrun.game.player.Killer;
import io.github.pulsebeat02.murderrun.game.player.PlayerAudience;
import io.github.pulsebeat02.murderrun.game.scheduler.GameScheduler;
import io.github.pulsebeat02.murderrun.locale.Message;
import java.util.function.Consumer;
import net.kyori.adventure.text.Component;
import org.bukkit.Material;
import org.bukkit.entity.Item;

public final class TrapWrecker extends KillerGadget {

  private static final String TRAP_WRECKER_SOUND = "block.bone_block.break";
  private static final int TRAP_WRECKER_TIME = 30;

  public TrapWrecker() {
    super(
        "trap_wrecker",
        Material.BARRIER,
        Message.TRAP_WRECKER_NAME.build(),
        Message.TRAP_WRECKER_LORE.build(),
        48);
  }

  @Override
  public boolean onGadgetDrop(
      final Game game, final GamePlayer player, final Item item, final boolean remove) {

    super.onGadgetDrop(game, player, item, true);

    if (!(player instanceof final Killer killer)) {
      return true;
    }

    killer.setIgnoreTraps(true);

    final GameScheduler scheduler = game.getScheduler();
    final Consumer<Integer> consumer = (time) -> {
      if (time == 0) {
        killer.setIgnoreTraps(false);
      }
      killer.setLevel(time);
    };
    scheduler.scheduleCountdownTask(consumer, TRAP_WRECKER_TIME);

    final PlayerAudience audience = killer.getAudience();
    final Component msg = Message.TRAP_WRECKER_ACTIVATE.build();
    audience.sendMessage(msg);
    audience.playSound(TRAP_WRECKER_SOUND);

    return false;
  }
}
