package io.github.pulsebeat02.murderrun.data;

import static java.util.Objects.requireNonNull;

import io.github.pulsebeat02.murderrun.MurderRun;
import io.github.pulsebeat02.murderrun.resourcepack.provider.ProviderMethod;
import io.github.pulsebeat02.murderrun.utils.ExecutorUtils;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import org.bukkit.configuration.file.FileConfiguration;

public final class PluginDataConfigurationMapper {

  private static final String PROVIDER_CHOICE = "pack-provider";
  private static final String SERVER_PORT_FIELD = "server.port";
  private static final String SERVER_HOST_FIELD = "server.host-name";

  private final ExecutorService service;
  private final MurderRun plugin;
  private final Lock readLock;
  private final Lock writeLock;

  private String hostName;
  private int port;
  private ProviderMethod providerMethod;

  public PluginDataConfigurationMapper(final MurderRun plugin) {
    final ReentrantReadWriteLock lock = new ReentrantReadWriteLock();
    this.plugin = plugin;
    this.readLock = lock.readLock();
    this.writeLock = lock.writeLock();
    this.service = Executors.newVirtualThreadPerTaskExecutor();
    this.plugin.saveDefaultConfig();
  }

  public synchronized void shutdown() {
    ExecutorUtils.shutdownExecutorGracefully(this.service);
  }

  public synchronized MurderRun getPlugin() {
    return this.plugin;
  }

  public synchronized void deserialize() {
    this.readLock.lock();
    final FileConfiguration config = this.plugin.getConfig();
    this.plugin.saveConfig();
    this.hostName = this.getHostName(config);
    this.port = this.getPortServerPort(config);
    this.readLock.unlock();
  }

  private int getPortServerPort(final FileConfiguration config) {
    return config.getInt(SERVER_PORT_FIELD);
  }

  private String getHostName(final FileConfiguration config) {
    return requireNonNull(config.getString(SERVER_HOST_FIELD));
  }

  private ProviderMethod getProviderMethod(final FileConfiguration config) {
    final String value = config.getString(PROVIDER_CHOICE);
    return value == null ? ProviderMethod.MC_PACK_HOSTING : ProviderMethod.valueOf(value);
  }

  public synchronized String getHostName() {
    return this.hostName;
  }

  public synchronized void serialize() {
    this.writeLock.lock();
    CompletableFuture.runAsync(
        () -> {
          final FileConfiguration config = this.plugin.getConfig();
          config.set(SERVER_HOST_FIELD, this.hostName);
          config.set(SERVER_PORT_FIELD, this.port);
          this.plugin.saveConfig();
        },
        this.service);
    this.writeLock.unlock();
  }

  public synchronized int getPort() {
    return this.port;
  }

  public synchronized ProviderMethod getProviderMethod() {
    return this.providerMethod;
  }
}
