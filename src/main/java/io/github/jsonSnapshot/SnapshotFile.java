package io.github.jsonSnapshot;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

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

    try (BufferedReader br = new BufferedReader(new FileReader(this.fileName))) {

      String sCurrentLine;

      while ((sCurrentLine = br.readLine()) != null) {
        fileContent.append(sCurrentLine + "\n");
      }

      final String fileText = fileContent.toString();
      if (StringUtils.isNotBlank(fileText)) {
        storedSnapshots = new SnapshotData();
        final String[] split = fileContent.toString().split(SPLIT_STRING);
        for (final String rawData : split) {
          storedSnapshots.add(SnapshotDataItem.ofRawData(rawData));
        }
      } else {
        storedSnapshots = new SnapshotData();
      }
    } catch (IOException e) {
      createFile(this.fileName);
      storedSnapshots = new SnapshotData();
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

    File file = null;

    try {
      file = createFile(fileName);
    } catch (IOException e) {
      e.printStackTrace();
    }

    final ArrayList<String> rawItems = new ArrayList<>();
    for (final SnapshotDataItem snapshotDataItem : storedSnapshots.getItems()) {
      rawItems.add(snapshotDataItem.asRawData());
    }

    try (final FileOutputStream fileStream = new FileOutputStream(file, false)) {
      byte[] myBytes = StringUtils.join(rawItems, SPLIT_STRING).getBytes();
      fileStream.write(myBytes);
    } catch (IOException e) {
      e.printStackTrace();
    }
  }
}
