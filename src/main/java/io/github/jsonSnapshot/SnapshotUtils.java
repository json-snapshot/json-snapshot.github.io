package io.github.jsonSnapshot;

import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.verify;

import java.lang.reflect.Parameter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.function.Function;

import org.apache.commons.lang3.ArrayUtils;
import org.assertj.core.util.Arrays;
import org.mockito.ArgumentCaptor;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.PrettyPrinter;
import com.fasterxml.jackson.core.util.DefaultIndenter;
import com.fasterxml.jackson.core.util.DefaultPrettyPrinter;
import com.fasterxml.jackson.core.util.Separators;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;

public class SnapshotUtils {

  public static <T> HashMap<String, List<LinkedHashMap<String, Object>>> extractArgs(
      T object, String methodName, SnapshotCaptor... snapshotCaptors) {
    List<ArgumentCaptor> captors = new ArrayList<>();
    Class[] classes = new Class[snapshotCaptors.length];

    int i = 0;
    for (SnapshotCaptor snapshotCaptor : snapshotCaptors) {
      classes[i] = snapshotCaptor.getParameterClass();
      captors.add(ArgumentCaptor.forClass(snapshotCaptor.getParameterClass()));
      i++;
    }

    return process(object, methodName, captors, classes, snapshotCaptors);
  }

  public static <T> HashMap<String, List<LinkedHashMap<String, Object>>> extractArgs(
      T object, String methodName, Class<?>... classes) {
    List<ArgumentCaptor> captors = new ArrayList<>();

    for (Class clazz : classes) {
      captors.add(ArgumentCaptor.forClass(clazz));
    }

    return process(object, methodName, captors, classes, null);
  }

  private static <T> HashMap<String, List<LinkedHashMap<String, Object>>> process(
      T object,
      String methodName,
      List<ArgumentCaptor> captors,
      Class[] classes,
      SnapshotCaptor[] snapshotCaptors) {
    HashMap<String, List<LinkedHashMap<String, Object>>> result = new HashMap<>();
    try {
      Parameter[] parameters =
          object.getClass().getDeclaredMethod(methodName, classes).getParameters();

      object
          .getClass()
          .getDeclaredMethod(methodName, classes)
          .invoke(
              verify(object, atLeastOnce()),
              captors.stream().map(ArgumentCaptor::capture).toArray());

      List<LinkedHashMap<String, Object>> extractedObjects = new ArrayList<>();

      int numberOfCall;

      if (captors.size() > 0) {
        numberOfCall = captors.get(0).getAllValues().size();

        for (int i = 0; i < numberOfCall; i++) {
          LinkedHashMap<String, Object> objectMap = new LinkedHashMap<>();

          int j = 0;
          for (ArgumentCaptor captor : captors) {
            Object value = captor.getAllValues().get(i);
            if (snapshotCaptors != null) {
              value = snapshotCaptors[j].removeIgnored(value);
            }
            objectMap.put(parameters[j].getName(), value);
            j++;
          }
          extractedObjects.add(objectMap);
        }
      }

      result.put(
          object.getClass().getSuperclass().getSimpleName() + "." + methodName, extractedObjects);
    } catch (Exception e) {
      throw new SnapshotMatchException(e.getMessage(), e.getCause());
    }

    return result;
  }

  public static Function<Object, String> defaultJsonFunction() {

    ObjectMapper objectMapper = buildObjectMapper();

    PrettyPrinter pp = buildDefaultPrettyPrinter();

    return (object) -> {
      try {
        return objectMapper.writer(pp).writeValueAsString(object);
      } catch (Exception e) {
        throw new SnapshotMatchException(e.getMessage());
      }
    };
  }

  public static PrettyPrinter buildDefaultPrettyPrinter() {
    DefaultPrettyPrinter pp =
        new DefaultPrettyPrinter("") {
          @Override
          public DefaultPrettyPrinter withSeparators(Separators separators) {
            this._separators = separators;
            this._objectFieldValueSeparatorWithSpaces =
                separators.getObjectFieldValueSeparator() + " ";
            return this;
          }
        };
    DefaultPrettyPrinter.Indenter lfOnlyIndenter = new DefaultIndenter("  ", "\n");
    pp.indentArraysWith(lfOnlyIndenter);
    pp.indentObjectsWith(lfOnlyIndenter);
    return pp;
  }

  private static ObjectMapper buildObjectMapper() {
    ObjectMapper objectMapper = new ObjectMapper();
    objectMapper.configure(SerializationFeature.ORDER_MAP_ENTRIES_BY_KEYS, true);
    objectMapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);

    objectMapper.setVisibility(
        objectMapper
            .getSerializationConfig()
            .getDefaultVisibilityChecker()
            .withFieldVisibility(JsonAutoDetect.Visibility.ANY)
            .withGetterVisibility(JsonAutoDetect.Visibility.NONE)
            .withSetterVisibility(JsonAutoDetect.Visibility.NONE)
            .withCreatorVisibility(JsonAutoDetect.Visibility.NONE));
    return objectMapper;
  }

  public static void validateExpectCall(Snapshot snapshot, List<Snapshot> calledSnapshots) {
    for (Snapshot eachSnapshot : calledSnapshots) {
      if (eachSnapshot.getSnapshotName().equals(snapshot.getSnapshotName())) {
        throw new SnapshotMatchException(
            "You can only call 'expect' once per test method. Try using array of arguments on a single 'expect' call");
      }
    }
  }

  public static Object[] mergeObjects(Object firstObject, Object[] others) {
    Object[] objects = new Object[1];
    objects[0] = firstObject;
    if (!Arrays.isNullOrEmpty(others)) {
      objects = ArrayUtils.addAll(objects, others);
    }
    return objects;
  }
}
