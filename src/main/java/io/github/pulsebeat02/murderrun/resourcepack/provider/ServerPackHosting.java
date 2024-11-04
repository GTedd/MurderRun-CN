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

  private static final String HOST_URL = "http://%s:%s";

  private final String hostName;
  private final int port;

  private ResourcePackServer server;

  public ServerPackHosting(final String hostName, final int port) {
    super(ProviderMethod.LOCALLY_HOSTED_DAEMON);
    this.hostName = requireNonNull(hostName);
    this.port = port;
  }

  @Override
  public String getRawUrl(final Path zip) {
    if (this.server != null) {
      return HOST_URL.formatted(this.hostName, this.port);
    }
    return this.startInternalServer(zip);
  }

  private String startInternalServer(final Path zip) {
    try (final InputStream stream = Files.newInputStream(zip); final InputStream fast = new FastBufferedInputStream(stream)) {
      final String hash = IOUtils.generateFileHash(zip);
      final Writable writable = Writable.copyInputStream(fast);
      final BuiltResourcePack pack = BuiltResourcePack.of(writable, hash);
      final ExecutorService service = Executors.newVirtualThreadPerTaskExecutor();
      this.server = ResourcePackServer.server().address(this.hostName, this.port).pack(pack).executor(service).build();
      this.server.start();
      return HOST_URL.formatted(this.hostName, this.port);
    } catch (final IOException | NoSuchAlgorithmException e) {
      throw new AssertionError(e);
    }
  }

  @Override
  public void start() {
    super.start();
    if (this.server == null) {
      return;
    }
    this.server.start();
  }

  @Override
  public void shutdown() {
    super.shutdown();
    if (this.server == null) {
      return;
    }
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
