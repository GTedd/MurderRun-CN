package io.github.pulsebeat02.murderrun.data;

import io.github.pulsebeat02.murderrun.MurderRun;
import io.github.pulsebeat02.murderrun.game.arena.ArenaManager;

public final class ArenaDataConfigurationMapper extends AbstractConfigurationManager<ArenaManager> {

  public ArenaDataConfigurationMapper(final MurderRun run) {
    super(ArenaManager.class, "arenas.json");
  }
}
