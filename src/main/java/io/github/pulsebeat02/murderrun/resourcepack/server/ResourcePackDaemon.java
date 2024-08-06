package io.github.pulsebeat02.murderrun.resourcepack.server;

import io.github.pulsebeat02.murderrun.resourcepack.ServerResourcepack;
import io.github.pulsebeat02.murderrun.utils.ResourceUtils;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.security.NoSuchAlgorithmException;
import java.util.concurrent.Executors;
import team.unnamed.creative.BuiltResourcePack;
import team.unnamed.creative.base.Writable;
import team.unnamed.creative.server.ResourcePackServer;

public final class ResourcePackDaemon {

  private static final ServerResourcepack PACK;

  static {
    PACK = new ServerResourcepack();
    PACK.build();
  }

  private final String hostName;
  private final int port;

  private ResourcePackServer server;
  private String url;
  private String hash;

  public ResourcePackDaemon(final String hostName, final int port) {
    this.hostName = hostName;
    this.port = port;
  }

  public void buildPack() {
    final Path path = PACK.getPath();
    try (final InputStream stream = Files.newInputStream(path)) {
      final Writable writable = Writable.copyInputStream(stream);
      this.url = String.format("http://%s:%s", this.hostName, this.port);
      this.hash = ResourceUtils.createPackHash(path);
      final BuiltResourcePack pack = BuiltResourcePack.of(writable, this.hash);
      this.server = ResourcePackServer.server()
          .address(this.hostName, this.port)
          .pack(pack)
          .executor(Executors.newFixedThreadPool(8))
          .build();
    } catch (final IOException | NoSuchAlgorithmException e) {
      throw new AssertionError(e);
    }
  }

  public String getHostName() {
    return this.hostName;
  }

  public int getPort() {
    return this.port;
  }

  public ResourcePackServer getServer() {
    return this.server;
  }

  public void start() {
    this.server.start();
  }

  public void stop() {
    this.server.stop(0);
  }

  public String getUrl() {
    return this.url;
  }

  public void setUrl(final String url) {
    this.url = url;
  }

  public String getHash() {
    return this.hash;
  }

  public void setHash(final String hash) {
    this.hash = hash;
  }
}