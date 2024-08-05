package io.github.pulsebeat02.murderrun.resourcepack.texture;

import static net.kyori.adventure.key.Key.key;

import io.github.pulsebeat02.murderrun.utils.ResourceUtils;
import java.io.IOException;
import java.io.InputStream;
import net.kyori.adventure.key.Key;
import org.checkerframework.checker.initialization.qual.UnderInitialization;
import team.unnamed.creative.base.Writable;
import team.unnamed.creative.texture.Texture;

public final class CustomTexture {

  private final Key key;
  private final Writable data;

  public CustomTexture(final String namespace) {
    this(key("murder_run", namespace), namespace);
  }

  public CustomTexture(final Key key, final String namespace) {
    this.key = key;
    this.data = this.getTextureStream(namespace);
  }

  private Writable getTextureStream(
      @UnderInitialization CustomTexture this, final String namespace) {
    final String path = String.format("assets/textures/%s", namespace);
    try (final InputStream stream = ResourceUtils.getResourceAsStream(path)) {
      return Writable.copyInputStream(stream);
    } catch (final IOException e) {
      throw new AssertionError(e);
    }
  }

  public Key getKey() {
    return this.key;
  }

  public Writable getData() {
    return this.data;
  }

  public Texture build() {
    return Texture.texture().key(this.key).data(this.data).build();
  }
}
