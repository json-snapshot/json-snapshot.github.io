package io.github.jsonSnapshot;

import lombok.Getter;
import org.assertj.core.util.diff.DiffUtils;
import org.assertj.core.util.diff.Patch;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Collection;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;

public class Snapshot {

    private SnapshotFile snapshotFile;

    private Class clazz;

    private Method method;

    private Function<Object, String> jsonFunction;

    @Getter
    private Object[] current;

    Snapshot(SnapshotFile snapshotFile, Class clazz, Method method, Function<Object, String> jsonFunction, Object... current) {
        this.current = current;
        this.snapshotFile = snapshotFile;
        this.clazz = clazz;
        this.method = method;
        this.jsonFunction = jsonFunction;
    }

    public void toMatchSnapshot() {

        Set<String> rawSnapshots = snapshotFile.getRawSnapshots();

        String rawSnapshot = getRawSnapshot(rawSnapshots);

        String currentObject = takeSnapshot();

        // Match Snapshot
        if (rawSnapshot != null) {
            if (!rawSnapshot.trim().equals(currentObject.trim())) {
                throw generateDiffError(rawSnapshot, currentObject);
            }
        }
        // Create New Snapshot
        else {
            snapshotFile.push(currentObject);
        }
    }

    private SnapshotMatchException generateDiffError(String rawSnapshot, String currentObject) {
        //compute the patch: this is the diffutils part
        Patch<String> patch =
                DiffUtils.diff(
                        Arrays.asList(rawSnapshot.trim().split("\n")),
                        Arrays.asList(currentObject.trim().split("\n")));
        String error = "Error on: \n" + currentObject.trim() + "\n\n" + patch.getDeltas().stream().map(delta -> delta.toString() + "\n").reduce(String::concat).get();
        return new SnapshotMatchException(error);
    }

    private String getRawSnapshot(Collection<String> rawSnapshots) {
        for (String rawSnapshot : rawSnapshots) {

            if (rawSnapshot.contains(getSnapshotName())) {
                return rawSnapshot;
            }
        }
        return null;
    }

    private String takeSnapshot() {
        return getSnapshotName() + jsonFunction.apply(current);
    }

    public String getSnapshotName() {
        return clazz.getName() + "." + method.getName() + "=";
    }

    /**
     * Ignore nested fields in the {@link #current} objects.
     *
     * @param pathsToIgnore Dot delimited paths to ignore within the {@link #current} objects. For example {@code nested.field}.
     * @return self
     * @throws SnapshotMatchException if the {@link #current} objects are not nested {@link Map}s.
     */
    public Snapshot ignoring(String... pathsToIgnore) throws SnapshotMatchException {
        for (String path : pathsToIgnore) {
            for (Object object : current) {
                SnapshotUtils.deleteNestedFieldFromMap(object, path);
            }
        }

        return this;
    }
}
