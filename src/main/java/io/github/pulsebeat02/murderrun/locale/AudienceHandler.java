package io.github.pulsebeat02.murderrun.locale;

import io.github.pulsebeat02.murderrun.MurderRun;
import net.kyori.adventure.platform.bukkit.BukkitAudiences;
import net.kyori.adventure.text.Component;

public final class AudienceHandler {

  private final BukkitAudiences audience;

  public AudienceHandler(final MurderRun neon) {
    this.audience = BukkitAudiences.create(neon);
  }

  public void shutdown() {
    if (this.audience != null) {
      this.audience.close();
    }
  }

  public BukkitAudiences retrieve() {
    this.checkStatus();
    return this.audience;
  }

  public void console(final Component component) {
    this.checkStatus();
    this.audience.console().sendMessage(component);
  }

  private void checkStatus() {
    if (this.audience == null) {
      throw new AssertionError("Tried to access Adventure when the plugin was disabled!");
    }
  }
}
