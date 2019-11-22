package io.github.jsonSnapshot;

import java.lang.reflect.Method;
import java.util.Optional;
import java.util.function.Function;

public class Snapshot {

  private SnapshotFile snapshotFile;

  private Class clazz;

  private Method method;

  private Function<Object, String> serializeFunction;

  private SnapshotMatchingStrategy snapshotMatchingStrategy;

  private Object[] current;

  Snapshot(
      SnapshotFile snapshotFile,
      Class clazz,
      Method method,
      Function<Object, String> serializeFunction,
      SnapshotMatchingStrategy snapshotMatchingStrategy,
      Object... current) {
    this.current = current;
    this.snapshotFile = snapshotFile;
    this.clazz = clazz;
    this.method = method;
    this.serializeFunction = serializeFunction;
    this.snapshotMatchingStrategy = snapshotMatchingStrategy;
  }

  public void toMatchSnapshot() {

    final SnapshotData snapshots = snapshotFile.getStoredSnapshots();
    final Optional<SnapshotDataItem> snapshot = snapshots.getItemByName(getSnapshotName());

    final SnapshotDataItem currentObject = takeSnapshot();

    // Match Snapshot
    if (snapshot.isPresent()) {
      snapshotMatchingStrategy.match(snapshot.get(), currentObject.getData());
    }
    // Create New Snapshot
    else {
      snapshotFile.push(currentObject);
    }
  }

  private SnapshotDataItem takeSnapshot() {
    return new SnapshotDataItem(getSnapshotName(), serializeFunction.apply(current));
  }

  public String getSnapshotName() {
    return clazz.getName() + "." + method.getName();
  }
}
