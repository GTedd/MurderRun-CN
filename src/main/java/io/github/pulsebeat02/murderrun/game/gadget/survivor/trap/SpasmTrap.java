package io.github.pulsebeat02.murderrun.game.gadget.survivor.trap;

import io.github.pulsebeat02.murderrun.game.Game;
import io.github.pulsebeat02.murderrun.game.player.GamePlayer;
import io.github.pulsebeat02.murderrun.game.player.PlayerManager;
import io.github.pulsebeat02.murderrun.game.scheduler.GameScheduler;
import io.github.pulsebeat02.murderrun.locale.Message;
import java.awt.Color;
import java.util.Map;
import java.util.WeakHashMap;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Function;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Item;
import org.bukkit.util.Vector;

public final class SpasmTrap extends SurvivorTrap {

  private static final int SPASM_TRAP_DURATION = 7 * 20;
  private static final String SPASM_TRAP_SOUND = "entity.elder_guardian.curse";

  private static final Vector UP = new Vector(0, 1, 0);
  private static final Vector DOWN = new Vector(0, -1, 0);

  private final Map<GamePlayer, AtomicBoolean> states;

  public SpasmTrap() {
    super(
        "spasm",
        Material.SEA_LANTERN,
        Message.SPASM_NAME.build(),
        Message.SPASM_LORE.build(),
        Message.SPASM_ACTIVATE.build(),
        32,
        Color.RED);
    this.states = new WeakHashMap<>();
  }

  @Override
  public void onTrapActivate(final Game game, final GamePlayer murderer, final Item item) {

    final GameScheduler scheduler = game.getScheduler();
    scheduler.scheduleRepeatedTask(() -> this.alternateHead(murderer), 0, 5, SPASM_TRAP_DURATION);

    final PlayerManager manager = game.getPlayerManager();
    manager.playSoundForAllParticipants(SPASM_TRAP_SOUND);
  }

  private void alternateHead(final GamePlayer murderer) {
    final Function<GamePlayer, AtomicBoolean> function = ignored -> new AtomicBoolean(false);
    final AtomicBoolean atomic = this.states.computeIfAbsent(murderer, function);
    final boolean up = atomic.get();
    final Location location = this.getProperLocation(murderer, up);
    murderer.teleport(location);
    atomic.set(!up);
  }

  private Location getProperLocation(final GamePlayer murderer, final boolean up) {
    final Location location = murderer.getLocation();
    location.setDirection(up ? UP : DOWN);
    return location;
  }
}
