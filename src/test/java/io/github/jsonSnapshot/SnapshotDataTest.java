package io.github.jsonSnapshot;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

/*
 * #%L
 * json-snapshot.github.io
 * %%
 * Copyright (C) 2019 metas GmbH
 * %%
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as
 * published by the Free Software Foundation, either version 2 of the
 * License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public
 * License along with this program. If not, see
 * <http://www.gnu.org/licenses/gpl-2.0.html>.
 * #L%
 */

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
    final SnapshotDataItem result = SnapshotDataItem.ofRawData(rawDataString);

    assertThat(result.getName()).as("name").isEqualTo(expectedName);
    assertThat(result.getData()).as("data").isEqualToIgnoringNewLines(expectedData);
  }
}
