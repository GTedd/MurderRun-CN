package io.github.pulsebeat02.murderrun.game.gadget.killer.utility;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Multimap;
import io.github.pulsebeat02.murderrun.game.Game;
import io.github.pulsebeat02.murderrun.game.gadget.killer.KillerGadget;
import io.github.pulsebeat02.murderrun.game.player.GamePlayer;
import io.github.pulsebeat02.murderrun.game.player.Participant;
import io.github.pulsebeat02.murderrun.game.player.PlayerManager;
import io.github.pulsebeat02.murderrun.game.scheduler.GameScheduler;
import io.github.pulsebeat02.murderrun.locale.Locale;
import java.util.Collection;
import net.kyori.adventure.text.Component;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerDropItemEvent;

public final class FloorIsLava extends KillerGadget {

  private final Multimap<GamePlayer, GamePlayer> glowPlayerStates;

  public FloorIsLava() {
    super(
        "floor_is_lava",
        Material.LAVA_BUCKET,
        Locale.THE_FLOOR_IS_LAVA_TRAP_NAME.build(),
        Locale.THE_FLOOR_IS_LAVA_TRAP_LORE.build(),
        64);
    this.glowPlayerStates = ArrayListMultimap.create();
  }

  @Override
  public void onGadgetDrop(final Game game, final PlayerDropItemEvent event, final boolean remove) {

    super.onGadgetDrop(game, event, true);

    final PlayerManager manager = game.getPlayerManager();
    manager.applyToAllParticipants(this::sendFloorIsLavaMessage);

    final GameScheduler scheduler = game.getScheduler();
    final Player player = event.getPlayer();
    final GamePlayer killer = manager.getGamePlayer(player);
    this.handleSurvivors(manager, scheduler, killer);
  }

  private void handleSurvivors(
      final PlayerManager manager, final GameScheduler scheduler, final GamePlayer killer) {
    manager.applyToAllLivingInnocents(survivor -> this.handleMovement(scheduler, survivor, killer));
  }

  private void handleMovement(
      final GameScheduler scheduler, final GamePlayer player, final GamePlayer killer) {
    final Location previous = player.getLocation();
    scheduler.scheduleTask(() -> this.handleLocationChecking(previous, player, killer), 3 * 20L);
  }

  private void handleLocationChecking(
      final Location previous, final GamePlayer player, final GamePlayer killer) {
    final Location newLocation = player.getLocation();
    final Collection<GamePlayer> glowing = this.glowPlayerStates.get(killer);
    if (this.checkLocationSame(previous, newLocation)) {
      glowing.add(player);
      killer.setEntityGlowingForPlayer(player);
    } else if (glowing.contains(player)) {
      glowing.remove(player);
      killer.removeEntityGlowingForPlayer(player);
    }
  }

  private boolean checkLocationSame(final Location first, final Location second) {
    return first.getBlockX() == second.getBlockX()
        && first.getBlockY() == second.getBlockY()
        && first.getBlockZ() == second.getBlockZ();
  }

  private void sendFloorIsLavaMessage(final Participant participant) {
    final Component msg = Locale.THE_FLOOR_IS_LAVA_ACTIVATE.build();
    participant.sendMessage(msg);
  }
}