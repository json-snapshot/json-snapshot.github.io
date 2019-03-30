package io.github.jsonSnapshot.junit5;

import java.util.Optional;

import org.junit.jupiter.api.extension.ExtensionContext;

public class ExtensionContextRetriever {

  private static ExtensionContextRetriever sInstance;
  private ThreadLocal<ExtensionContext> extensionContextThreadLocal = new ThreadLocal<>();

  private ExtensionContextRetriever() {}

  public static ExtensionContextRetriever getInstance() {
    if (sInstance == null) {
      sInstance = new ExtensionContextRetriever();
    }

    return sInstance;
  }

  public void set(ExtensionContext context) {
    extensionContextThreadLocal.set(context);
  }

  public Optional<ExtensionContext> get() {
    return Optional.ofNullable(extensionContextThreadLocal.get());
  }

  public void remove() {
    extensionContextThreadLocal.remove();
  }
}
