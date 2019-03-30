package io.github.jsonSnapshot.junit5;

import static io.github.jsonSnapshot.junit5.SnapshotMatcher.expect;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import io.github.jsonSnapshot.SnapshotMatchException;

public class SnapshotMatcherExtensionTest {

  @DisplayName("Test update events are processed")
  @ParameterizedTest(name = "test{0}")
  @ValueSource(strings = {"one", "two", "three", "four", "five", "six", "seven"})
  @ExtendWith(SnapshotMatcherExtension.class)
  public void testParameterizedTest(String param) {
    expect(param, param.toUpperCase()).toMatchSnapshot();
  }

  @DisplayName("Test update events are processed2")
  @ParameterizedTest(name = "test{0}")
  @ValueSource(strings = {"one", "two", "three", "four", "five", "six", "seven"})
  @ExtendWith(SnapshotMatcherExtension.class)
  public void testParameterizedTest2(String param) {
    expect(param, param.toUpperCase()).toMatchSnapshot();
  }

  @Test
  public void testCallsToSnapshotMatcherFailWhenExtensionIsNotPresent() {
    assertThrows(SnapshotMatchException.class, () -> expect("uh oh").toMatchSnapshot());
    assertThrows(SnapshotMatchException.class, () -> SnapshotMatcher.start());
    assertThrows(SnapshotMatchException.class, () -> SnapshotMatcher.validateSnapshots());
  }
}
