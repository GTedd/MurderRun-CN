package io.github.pulsebeat02.murderrun.resourcepack;

import io.github.pulsebeat02.murderrun.resourcepack.model.ModelGeneratorManager;
import io.github.pulsebeat02.murderrun.resourcepack.sound.SoundKeys;
import io.github.pulsebeat02.murderrun.resourcepack.texture.ItemTextureKeys;
import io.github.pulsebeat02.murderrun.utils.IOUtils;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Path;
import team.unnamed.creative.ResourcePack;
import team.unnamed.creative.base.Writable;
import team.unnamed.creative.serialize.minecraft.MinecraftResourcePackWriter;
import team.unnamed.creative.sound.Sound;

public final class ServerResourcepack {

  private static final String PACK_FILE_NAME = "murder-run-pack";
  private static final String PACK_FILE_SUFFIX = ".zip";
  private static final String PACK_PNG_PATH = "textures/pack.png";
  private static final String PACK_MCMETA_DESCRIPTION = "Murder Run Plugin Resources";

  private Path path;
  private ResourcePack pack;

  public void build() throws IOException {
    this.initializeFields();
    this.customizeMetaData();
    this.addTextures();
    this.addModels();
    this.addSounds();
    this.zipPack();
  }

  private void initializeFields() throws IOException {
    this.path = IOUtils.createTemporaryPath(PACK_FILE_NAME, PACK_FILE_SUFFIX);
    this.pack = ResourcePack.resourcePack();
  }

  private void customizeMetaData() throws IOException {
    final InputStream stream = IOUtils.getResourceAsStream(PACK_PNG_PATH);
    this.pack.packMeta(34, PACK_MCMETA_DESCRIPTION);
    this.pack.icon(Writable.copyInputStream(stream));
  }

  private void addTextures() {
    final ItemTextureKeys[] textures = ItemTextureKeys.values();
    for (final ItemTextureKeys texture : textures) {
      this.pack.texture(texture.getTexture());
    }
  }

  private void addModels() {
    final ModelGeneratorManager handler = new ModelGeneratorManager();
    this.pack.texture(handler.customJumpScareGenerator());
    this.pack.model(handler.customItemModelGenerator());
    this.pack.model(handler.customSwordGenerator());
  }

  private void addSounds() {
    final SoundKeys[] sounds = SoundKeys.values();
    for (final SoundKeys sound : sounds) {
      final Sound internal = sound.getSound();
      this.pack.sound(internal);
    }
  }

  private void zipPack() {
    final MinecraftResourcePackWriter writer = MinecraftResourcePackWriter.minecraft();
    writer.writeToZipFile(this.path, this.pack);
  }

  public Path getPath() {
    return this.path;
  }
}
