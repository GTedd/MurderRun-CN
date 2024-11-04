/*

MIT License

Copyright (c) 2024 Brandon Li

Permission is hereby granted, free of charge, to any person obtaining a copy
of this software and associated documentation files (the "Software"), to deal
in the Software without restriction, including without limitation the rights
to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
copies of the Software, and to permit persons to whom the Software is
furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in all
copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
SOFTWARE.

*/
package io.github.pulsebeat02.murderrun.game.gadget.survivor.trap;

import io.github.pulsebeat02.murderrun.game.Game;
import io.github.pulsebeat02.murderrun.game.GameProperties;
import io.github.pulsebeat02.murderrun.game.player.GamePlayer;
import io.github.pulsebeat02.murderrun.game.player.PlayerManager;
import io.github.pulsebeat02.murderrun.game.scheduler.GameScheduler;
import io.github.pulsebeat02.murderrun.locale.Message;
import java.awt.Color;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Function;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Item;
import org.bukkit.util.Vector;

public final class SpasmTrap extends SurvivorTrap {

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
      GameProperties.SPASM_COST,
      Color.RED
    );
    this.states = new HashMap<>();
  }

  @Override
  public void onTrapActivate(final Game game, final GamePlayer murderer, final Item item) {
    final GameScheduler scheduler = game.getScheduler();
    scheduler.scheduleRepeatedTask(() -> this.alternateHead(murderer), 0, 5, GameProperties.SPASM_DURATION);

    final PlayerManager manager = game.getPlayerManager();
    manager.playSoundForAllParticipants(GameProperties.SPASM_SOUND);
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
