package io.github.pulsebeat02.murderrun.resourcepack.provider;

import static java.util.Objects.requireNonNull;

import io.github.pulsebeat02.murderrun.utils.IOUtils;
import it.unimi.dsi.fastutil.io.FastBufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.security.NoSuchAlgorithmException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import team.unnamed.creative.BuiltResourcePack;
import team.unnamed.creative.base.Writable;
import team.unnamed.creative.server.ResourcePackServer;

public final class ServerPackHosting extends ResourcePackProvider {

  private static final String HOST_URL = "%s:%s";

  private final String hostName;
  private final int port;

  private ResourcePackServer server;

  public ServerPackHosting(final String hostName, final int port) {
    super(ProviderMethod.LOCALLY_HOSTED_DAEMON);
    this.hostName = requireNonNull(hostName);
    this.port = port;
  }

  @Override
  String getRawUrl(final Path zip) {
    try (final InputStream stream = Files.newInputStream(zip);
        final InputStream fast = new FastBufferedInputStream(stream)) {
      final String hash = IOUtils.generateFileHash(zip);
      final Writable writable = Writable.copyInputStream(fast);
      final BuiltResourcePack pack = BuiltResourcePack.of(writable, hash);
      final ExecutorService service = Executors.newVirtualThreadPerTaskExecutor();
      this.server = ResourcePackServer.server()
          .address(this.hostName, this.port)
          .pack(pack)
          .executor(service)
          .build();
      return HOST_URL.formatted(this.hostName, this.port);
    } catch (final IOException | NoSuchAlgorithmException e) {
      throw new AssertionError(e);
    }
  }

  @Override
  public void start() {
    this.server.start();
  }

  @Override
  public void shutdown() {
    this.server.stop(0);
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
}