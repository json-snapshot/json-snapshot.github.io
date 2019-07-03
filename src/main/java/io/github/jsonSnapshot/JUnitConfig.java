package io.github.jsonSnapshot;

import java.lang.reflect.Method;
import java.util.stream.Stream;

import lombok.Getter;

public class JUnitConfig implements SnapshotConfig {
  @Getter private String filePath = "src/test/java/";

  @Override
  public StackTraceElement findStacktraceElement() {
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
                    SnapshotMatcher.getMethod(
                        getClassForName(stackTraceElement.getClassName()),
                        stackTraceElement.getMethodName())))
        .findFirst()
        .orElseThrow(
            () ->
                new SnapshotMatchException(
                    "Could not locate a method with one of supported test annotations"));
  }

  private boolean hasTestAnnotation(Method method) {
    return
    // Junit 4
    method.isAnnotationPresent(org.junit.Test.class)
        || method.isAnnotationPresent(org.junit.BeforeClass.class)
        // JUnit 5
        || method.isAnnotationPresent(org.junit.jupiter.params.ParameterizedTest.class)
        || method.isAnnotationPresent(org.junit.jupiter.api.Test.class)
        || method.isAnnotationPresent(org.junit.jupiter.api.BeforeAll.class);
  }

  private Class<?> getClassForName(String className) {
    try {
      return Class.forName(className);
    } catch (ClassNotFoundException e) {
      throw new IllegalArgumentException(e);
    }
  }
}
