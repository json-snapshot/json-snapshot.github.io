# json-snapshot
Snapshot Testing for Java. 

#### Purpose of Snapshot Testing
Snapshots help figuring out whether the output of the modules covered by tests is changed, without doing tons of asserts!

#### When is it usefull?

It's usefull for deterministic tests. That is, running the same tests multiple times on a component that has not changed 
should produce the same results every time. You're responsible for making sure your generated snapshots do not include 
platform specific or other non-deterministic data. 

A Json Snapshot test does not assert Java types. You can continue doing that with any other testing framework.


Based on [facebook's Jest framework](https://facebook.github.io/jest/docs/en/snapshot-testing.html)

#### How to install using Maven

Add to your pom.xml dependencies section:

```xml
<dependency>
    <groupId>io.github.json-snapshot</groupId>
    <artifactId>json-snapshot</artifactId>
    <version>0.0.1</version>
</dependency>
```


#### Usage

```java
package com.example;

import com.github.andrebonna.jsonSnapshot.*;

public class ExampleTest {
    @BeforeClass
    public static void beforeAll() {
        SnapshotMatcher.start();
    }
    
    @AfterClass
    public static void afterAll() {
        SnapshotMatcher.validateSnapshots();
    }
    
    @Test
    public void shouldShowSnapshotExample() {
        SnapshotMatcher.expect("<any type of object>").toMatchSnapshot();
    }
}
```

When the test runs for the first time, the framework will create a snapshot file named `ExampleTest.snap` alongside with your test class. It should look like this:
```text
com.example.ExampleTest| with |shouldShowSnapshotExample=[
    "<any type of object>"
]
```

Whenever it runs again, the `expect` method argument will be automatically validated with the `.snap` file. That is why you should commit every `.snap` file created.


