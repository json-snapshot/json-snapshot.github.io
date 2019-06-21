package io.github.jsonSnapshot;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

class SnapshotDataTest {

  @Test
  void SnapshotDataItem_ofRawData() {
    final String expectedName =
        "io.github.jsonSnapshot.SnapshotIntegrationTest.shouldMatchSnapshotInsidePrivateMethod";
    final String extravagantNameDateSeparator = " ==  ";
    final String expectedData =
        "[{\r\n"
            + "    \"id\": \"anyPrivate\",\r\n"
            + "    \"value\": 5,\r\n"
            + "    \"name\": \"anyPrivate\"\r\n"
            + "  }]";

    final String rawDataString = expectedName + extravagantNameDateSeparator + expectedData;

    performTest(rawDataString, expectedName, expectedData);
  }

  @Test
  void SnapshotDataItem_ofRawData2() {
    final String expectedName =
        "io.github.jsonSnapshot.SnapshotIntegrationTest.shouldThrowSnapshotMatchException";
    final String expectedData =
        "[\r\n"
            + "	  {\r\n"
            + "	    \"id\": \"anyId5\",\r\n"
            + "	    \"value\": 6,\r\n"
            + "	    \"name\": \"anyName5\"\r\n"
            + "	  }\r\n"
            + "	]";

    final String rawDataString =
        "io.github.jsonSnapshot.SnapshotIntegrationTest.shouldThrowSnapshotMatchException==[\r\n"
            + "	  {\r\n"
            + "	    \"id\": \"anyId5\",\r\n"
            + "	    \"value\": 6,\r\n"
            + "	    \"name\": \"anyName5\"\r\n"
            + "	  }\r\n"
            + "	]";

    performTest(rawDataString, expectedName, expectedData);
  }

  private void performTest(
      final String rawDataString, final String expectedName, final String expectedData) {

    final SnapshotDataItem result = new SnapshotDataItem(rawDataString);

    assertThat(result.getName()).as("name").isEqualTo(expectedName);
    assertThat(result.getData()).as("data").isEqualToIgnoringNewLines(expectedData);
  }
}
