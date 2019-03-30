package io.github.jsonSnapshot;

import java.io.IOException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.apache.commons.lang3.StringUtils;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.jupiter.api.BeforeAll;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SnapshotMatcher {

  private static Logger log = LoggerFactory.getLogger(SnapshotMatcher.class);
  private static final SnapshotConfig DEFAULT_CONFIG = SnapshotConfig.builder().build();
  private static Class clazz = null;
  private static SnapshotFile snapshotFile = null;
  private static List<Snapshot> calledSnapshots = new ArrayList<>();
  private static Function<Object, String> jsonFunction;

  public static void start() {
    start(DEFAULT_CONFIG, SnapshotUtils.defaultJsonFunction());
  }

  public static void start(SnapshotConfig config) {
    start(config, SnapshotUtils.defaultJsonFunction());
  }

  public static void start(Function<Object, String> jsonFunction) {
    start(DEFAULT_CONFIG, jsonFunction);
  }

  public static void start(SnapshotConfig config, Function<Object, String> jsonFunction) {
    SnapshotMatcher.jsonFunction = jsonFunction;
    try {
      StackTraceElement stackElement = findStackElement();
      clazz = Class.forName(stackElement.getClassName());
      snapshotFile =
          new SnapshotFile(
              config.getFilePath(), stackElement.getClassName().replaceAll("\\.", "/") + ".snap");
    } catch (ClassNotFoundException | IOException e) {
      throw new SnapshotMatchException(e.getMessage());
    }
  }

  public static void validateSnapshots() {
    Set<String> rawSnapshots = snapshotFile.getRawSnapshots();
    List<String> snapshotNames =
        calledSnapshots.stream().map(Snapshot::getSnapshotName).collect(Collectors.toList());
    List<String> unusedRawSnapshots = new ArrayList<>();

    for (String rawSnapshot : rawSnapshots) {
      boolean foundSnapshot = false;
      for (String snapshotName : snapshotNames) {
        if (rawSnapshot.contains(snapshotName)) {
          foundSnapshot = true;
        }
      }
      if (!foundSnapshot) {
        unusedRawSnapshots.add(rawSnapshot);
      }
    }
    if (unusedRawSnapshots.size() > 0) {
      log.warn(
          "All unused Snapshots: "
              + StringUtils.join(unusedRawSnapshots, "\n")
              + ". Consider deleting the snapshot file to recreate it!");
    }
  }

  public static Snapshot expect(Object firstObject, Object... others) {

    if (clazz == null) {
      throw new SnapshotMatchException(
          "SnapshotTester not yet started! Start it on @BeforeClass/@BeforeAll with SnapshotMatcher.start()");
    }
    Object[] objects = SnapshotUtils.mergeObjects(firstObject, others);
    StackTraceElement stackElement = findStackElement();
    Method method = getMethod(clazz, stackElement.getMethodName());
    Snapshot snapshot = new Snapshot(snapshotFile, clazz, method.getName(), jsonFunction, objects);
    SnapshotUtils.validateExpectCall(snapshot, calledSnapshots);
    calledSnapshots.add(snapshot);
    return snapshot;
  }

  private static Method getMethod(Class<?> clazz, String methodName) {
    try {
      return Stream.of(clazz.getDeclaredMethods())
          .filter(method -> method.getName().equals(methodName))
          .findFirst()
          .orElseThrow(() -> new NoSuchMethodException("Not Found"));
    } catch (NoSuchMethodException e) {
      return Optional.ofNullable(clazz.getSuperclass())
          .map(superclass -> getMethod(superclass, methodName))
          .orElseThrow(
              () ->
                  new SnapshotMatchException(
                      "Could not find method "
                          + methodName
                          + " on class "
                          + clazz
                          + "\nPlease annotate your test method with @Test and make it without any parameters!"));
    }
  }

  private static StackTraceElement findStackElement() {
    StackTraceElement[] stackTraceElements = Thread.currentThread().getStackTrace();
    int elementsToSkip = 1; // Start after stackTrace
    while (SnapshotMatcher.class
        .getName()
        .equals(stackTraceElements[elementsToSkip].getClassName())) {
      elementsToSkip++;
    }

    return Stream.of(stackTraceElements)
        .skip(elementsToSkip)
        .filter(
            stackTraceElement ->
                hasTestAnnotation(
                    getMethod(
                        getClass(stackTraceElement.getClassName()),
                        stackTraceElement.getMethodName())))
        .findFirst()
        .orElseThrow(
            () ->
                new SnapshotMatchException(
                    "Could not locate a method with one of supported test annotations"));
  }

  private static boolean hasTestAnnotation(Method method) {
    return method.isAnnotationPresent(Test.class)
        || method.isAnnotationPresent(BeforeClass.class)
        || method.isAnnotationPresent(org.junit.jupiter.api.Test.class)
        || method.isAnnotationPresent(BeforeAll.class);
  }

  private static Class<?> getClass(String className) {
    try {
      return Class.forName(className);
    } catch (ClassNotFoundException e) {
      throw new IllegalArgumentException(e);
    }
  }
}
