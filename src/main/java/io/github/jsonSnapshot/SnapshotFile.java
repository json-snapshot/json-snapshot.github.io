package io.github.jsonSnapshot;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Files;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import lombok.Getter;
import lombok.NonNull;

import org.apache.commons.lang3.StringUtils;

public class SnapshotFile {

  private static final String SPLIT_STRING = "\n\n\n";

  private String fileName;

  @Getter private SnapshotData storedSnapshots;

  SnapshotFile(String filePath, String fileName) throws IOException {

    this.fileName = filePath + fileName;

    StringBuilder fileContent = new StringBuilder();

    storedSnapshots = new SnapshotData();
    try (BufferedReader br = new BufferedReader(new FileReader(this.fileName))) {

      String sCurrentLine;

      while ((sCurrentLine = br.readLine()) != null) {
        fileContent.append(sCurrentLine + "\n");
      }

      final String fileText = fileContent.toString();
      if (StringUtils.isNotBlank(fileText)) {

        final String[] split = fileContent.toString().split(SPLIT_STRING);
        Stream.of(split).map(SnapshotDataItem::ofRawData).forEach(storedSnapshots::add);
      }
    } catch (IOException e) {
      createFile(this.fileName);
    }
  }

  private File createFile(String fileName) throws IOException {
    File file = new File(fileName);
    file.getParentFile().mkdirs();
    file.createNewFile();
    return file;
  }

  public void push(@NonNull final SnapshotDataItem snapshot) {
    storedSnapshots.add(snapshot);

    final File file;
    try {
      file = createFile(fileName);
    } catch (IOException e) {
      throw new RuntimeException("Unable to create snapshot items file " + fileName, e);
    }

    final byte[] myBytes =
        storedSnapshots
            .getItems()
            .stream()
            .map(SnapshotDataItem::asRawData)
            .collect(Collectors.joining(SPLIT_STRING))
            .getBytes();

    try {
      Files.write(file.toPath(), myBytes);
    } catch (IOException e) {
      throw new RuntimeException(
          "Unable to write snapshot items to file " + file.getAbsolutePath(), e);
    }
  }
}
