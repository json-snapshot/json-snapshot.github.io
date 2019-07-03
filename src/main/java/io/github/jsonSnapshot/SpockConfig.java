package io.github.jsonSnapshot;

import java.util.Arrays;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

/** For use by Spock http://spockframework.org/ */
@RequiredArgsConstructor
public class SpockConfig implements SnapshotConfig {

  /**
   * In order to locate the correct test method in the stacktrace the base package of the specs need
   * to be supplied.
   */
  private final String specBasePackage;

  @Getter private String filePath = "src/test/groovy/";

  @Override
  public StackTraceElement findStacktraceElement() {
    StackTraceElement[] stackTraceElements = Thread.currentThread().getStackTrace();
    return Arrays.stream(stackTraceElements)
        .filter(it -> it.getClassName().startsWith(specBasePackage))
        .findFirst()
        .orElseThrow(
            () -> new SnapshotMatchException("Could not locate a method in package 'spec'"));
  }
}
