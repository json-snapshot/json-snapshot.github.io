package io.github.jsonSnapshot;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import lombok.Getter;
import lombok.NonNull;

import org.apache.commons.lang3.StringUtils;

public class SnapshotFile {

  private static final Charset UTF_8 = StandardCharsets.UTF_8;
  private static final String SPLIT_STRING = "\n\n\n";

  private String pathAndfileName;

  @Getter private SnapshotData storedSnapshots;

  SnapshotFile(String filePath, String fileName) throws IOException {

    this.pathAndfileName = filePath + fileName;
    this.storedSnapshots = new SnapshotData();

    try {
      loadSnapshotFile();
    } catch (IOException e) {
      createFile(this.pathAndfileName);
    }
  }

  private void loadSnapshotFile() throws IOException {

    Path path = Paths.get(this.pathAndfileName);

    String lines = new String(Files.readAllBytes(path), UTF_8);
    String[] rawSnapshotItems = lines.split(SPLIT_STRING);

    Stream.of(rawSnapshotItems)
        .filter(StringUtils::isNotBlank)
        .map(SnapshotDataItem::new)
        .forEach(storedSnapshots::add);
  }

  private File createFile(String fileName) {

    File file = new File(fileName);
    try {
      file.getParentFile().mkdirs();
      file.createNewFile();
      return file;
    } catch (IOException e) {
      throw new RuntimeException("Unable to create new file " + file.getAbsolutePath(), e);
    }
  }

  public void push(@NonNull final SnapshotDataItem snapshot) {

    storedSnapshots.add(snapshot);

    final File file = createFile(pathAndfileName); // exception handling inside
    try (OutputStreamWriter fileWriter =
        new OutputStreamWriter(new FileOutputStream(file, false), UTF_8)) {

      String content =
          storedSnapshots
              .getItems()
              .stream()
              .map(SnapshotDataItem::asRawData)
              .collect(Collectors.joining(SPLIT_STRING));
      fileWriter.write(content);
    } catch (IOException e) {
      throw new RuntimeException(
          "Unable to write snapshot items to file " + file.getAbsolutePath(), e);
    }
  }
}
