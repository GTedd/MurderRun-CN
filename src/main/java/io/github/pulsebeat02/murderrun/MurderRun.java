package io.github.pulsebeat02.murderrun;

import io.github.pulsebeat02.murderrun.config.PluginConfiguration;
import io.github.pulsebeat02.murderrun.locale.AudienceHandler;
import io.github.pulsebeat02.murderrun.resourcepack.server.PackHostingDaemon;
import org.bukkit.NamespacedKey;
import org.bukkit.plugin.java.JavaPlugin;

public final class MurderRun extends JavaPlugin {

  private static NamespacedKey KEY;

  private PluginConfiguration configuration;
  private AudienceHandler audience;
  private PackHostingDaemon daemon;

  @Override
  public void onEnable() {
    KEY = new NamespacedKey(this, "data");
    this.configuration = new PluginConfiguration(this);
    this.audience = new AudienceHandler(this);
    this.daemon = new PackHostingDaemon(this.configuration.getPort());
  }

  @Override
  public void onDisable() {}

  public static NamespacedKey getKey() {
    return KEY;
  }

  public PluginConfiguration getConfiguration() {
    return this.configuration;
  }

  public AudienceHandler getAudience() {
    return this.audience;
  }

  public PackHostingDaemon getDaemon() {
    return this.daemon;
  }
}
