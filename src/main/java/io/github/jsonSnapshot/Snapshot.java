package io.github.jsonSnapshot;

import java.lang.reflect.Method;
import java.util.Optional;
import java.util.function.Function;

public class Snapshot {

  private SnapshotFile snapshotFile;

  private Class clazz;

  private Method method;

  private Function<Object, String> jsonFunction;

  private SnapshotMatchingStragety snapshotMatchRule;

  private Object[] current;

  Snapshot(
      SnapshotFile snapshotFile,
      Class clazz,
      Method method,
      Function<Object, String> jsonFunction,
      SnapshotMatchingStragety snapshotMatchRule,
      Object... current) {
    this.current = current;
    this.snapshotFile = snapshotFile;
    this.clazz = clazz;
    this.method = method;
    this.jsonFunction = jsonFunction;
    this.snapshotMatchRule = snapshotMatchRule;
  }

  public void toMatchSnapshot() {

    final SnapshotData snapshots = snapshotFile.getStoredSnapshots();
    final Optional<SnapshotDataItem> snapshot = snapshots.getItemByNameOrNull(getSnapshotName());

    final SnapshotDataItem currentObject = takeSnapshot();

    // Match Snapshot
    if (snapshot.isPresent()) {
      snapshotMatchRule.match(snapshot.get(), currentObject.getData());
    }
    // Create New Snapshot
    else {
      snapshotFile.push(currentObject);
    }
  }

  private SnapshotDataItem takeSnapshot() {
    return SnapshotDataItem.ofNameAndData(getSnapshotName(), jsonFunction.apply(current));
  }

  public String getSnapshotName() {
    return clazz.getName() + "." + method.getName();
  }
}
