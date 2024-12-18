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

import com.google.gson.Gson;
import io.github.pulsebeat02.murderrun.gson.GsonProvider;
import io.github.pulsebeat02.murderrun.utils.IOUtils;
import it.unimi.dsi.fastutil.io.FastBufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import java.io.Writer;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.jsoup.Connection.Method;
import org.jsoup.Connection.Response;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

public final class MCPackHosting extends ResourcePackProvider {

  private static final String WEBSITE_URL = "https://mc-packs.net/";
  private static final String DOWNLOAD_REGEX = "input[readonly][value^=https]";

  public MCPackHosting() {
    super(ProviderMethod.MC_PACK_HOSTING);
  }

  @Override
  public String getRawUrl(final Path zip) {
    final ReentrantReadWriteLock lock = new ReentrantReadWriteLock();
    final PackInfo info = this.checkFileUrl(lock);
    return this.updateAndRetrievePackJSON(lock, info == null ? this.createNewPackInfo(zip) : info);
  }

  private PackInfo createNewPackInfo(final Path zip) {
    final String name = IOUtils.getName(zip);
    try (final InputStream stream = Files.newInputStream(zip); final InputStream fast = new FastBufferedInputStream(stream)) {
      final Response uploadResponse = this.getResponse(name, fast);
      final Document document = uploadResponse.parse();
      final String url = this.getDownloadUrl(document);
      return new PackInfo(url, 0);
    } catch (final IOException e) {
      throw new AssertionError(e);
    }
  }

  private String updateAndRetrievePackJSON(final ReentrantReadWriteLock lock, final PackInfo info) {
    final Lock write = lock.writeLock();
    final Path path = this.getCachedFilePath();
    try (final Writer writer = Files.newBufferedWriter(path)) {
      final int loads = info.loads + 1;
      final PackInfo updated = new PackInfo(info.url, loads);
      final Gson gson = GsonProvider.getGson();
      write.lock();
      gson.toJson(updated, writer);
      return updated.url;
    } catch (final IOException e) {
      throw new AssertionError(e);
    } finally {
      write.unlock();
    }
  }

  private @Nullable PackInfo checkFileUrl(final ReentrantReadWriteLock lock) {
    final Path path = this.getCachedFilePath();
    if (IOUtils.createFile(path)) {
      return null;
    }

    final Lock read = lock.readLock();
    read.lock();

    try (final Reader reader = Files.newBufferedReader(path)) {
      final Gson gson = GsonProvider.getGson();
      final PackInfo info = gson.fromJson(reader, PackInfo.class);
      return info == null ? null : (info.loads > 10 ? null : info);
    } catch (final IOException e) {
      throw new AssertionError(e);
    } finally {
      read.unlock();
    }
  }

  private Path getCachedFilePath() {
    final Path data = IOUtils.getPluginDataFolderPath();
    return data.resolve("cached-packs.json");
  }

  private String getDownloadUrl(final Document document) {
    final Elements elements = document.select(DOWNLOAD_REGEX);
    final Element element = elements.first();
    if (element == null) {
      throw new IllegalStateException("Download URL not found!");
    }
    return element.val();
  }

  private Response getResponse(final String name, final InputStream fast) throws IOException {
    return Jsoup.connect(WEBSITE_URL).data("file", name, fast).method(Method.POST).execute();
  }

  record PackInfo(String url, int loads) {}
}
