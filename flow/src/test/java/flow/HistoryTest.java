package flow;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import org.junit.Test;

import static java.util.Arrays.asList;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Fail.fail;

public class HistoryTest {
  private static final TestState ABLE = new TestState("able");
  private static final TestState BAKER = new TestState("baker");
  private static final TestState CHARLIE = new TestState("charlie");

  @Test public void builderCanPushPeekAndPopObjects() {
    History.Builder builder = History.emptyBuilder();

    List<TestState> objects = asList(ABLE, BAKER, CHARLIE);
    for (Object object : objects) {
      builder.push(object);
    }

    for (int i = objects.size() - 1; i >= 0; i--) {
      Object object = objects.get(i);
      assertThat(builder.peek()).isSameAs(object);
      assertThat(builder.pop()).isSameAs(object);
    }
  }

  @Test public void builderCanPopTo() {
    History.Builder builder = History.emptyBuilder();
    builder.push(ABLE);
    builder.push(BAKER);
    builder.push(CHARLIE);
    builder.popTo(ABLE);
    assertThat(builder.peek()).isSameAs(ABLE);
  }

  @Test public void builderPopToExplodesOnMissingState() {
    History.Builder builder = History.emptyBuilder();
    builder.push(ABLE);
    builder.push(BAKER);
    builder.push(CHARLIE);
    try {
      builder.popTo(new Object());
      fail("Missing state object, should have thrown");
    } catch (IllegalArgumentException ignored) {
      // Correct!
    }
  }

  @Test public void builderCanPopCount() {
    History.Builder builder = History.emptyBuilder();
    builder.push(ABLE);
    builder.push(BAKER);
    builder.push(CHARLIE);
    builder.pop(1);
    assertThat(builder.peek()).isSameAs(BAKER);
    builder.pop(2);
    assertThat(builder.isEmpty());
  }

  @Test public void builderPopExplodesIfCountIsTooLarge() {
    History.Builder builder = History.emptyBuilder();
    builder.push(ABLE);
    builder.push(BAKER);
    builder.push(CHARLIE);
    try {
      builder.pop(4);
      fail("Count is too large, should have thrown");
    } catch (IllegalArgumentException ignored) {
      // Success!
    }
  }


  @Test public void forwardIterator() {
    List<Object> paths = new ArrayList<>(Arrays.<Object>asList(ABLE, BAKER, CHARLIE));
    History history = History.emptyBuilder().addAll(paths).build();
    for (Object o : history) {
      assertThat(o).isSameAs(paths.remove(paths.size()-1));
    }
  }

  @Test public void reverseIterator() {
    List<Object> paths = new ArrayList<>(Arrays.<Object>asList(ABLE, BAKER, CHARLIE));
    History history = History.emptyBuilder().addAll(paths).build();
    Iterator<Object> i = history.reverseIterator();
    while (i.hasNext()) {
      assertThat(i.next()).isSameAs(paths.remove(0));
    }
  }


  @Test public void emptyBuilderPeekIsNullable() {
    assertThat(History.emptyBuilder().peek()).isNull();
  }

  @Test public void emptyBuilderPopThrows() {
    try {
      History.emptyBuilder().pop();
      fail("Should throw");
    } catch (IllegalStateException e) {
      // pass
    }
  }

  @Test public void isEmpty() {
    final History.Builder builder = History.emptyBuilder();
    assertThat(builder.isEmpty()).isTrue();
    builder.push("foo");
    assertThat(builder.isEmpty()).isFalse();
    builder.pop();
    assertThat(builder.isEmpty()).isTrue();
  }

  @Test public void historyIndexAccess() {
    History history = History.emptyBuilder().addAll(asList(ABLE, BAKER, CHARLIE)).build();
    assertThat(history.peek(0)).isEqualTo(CHARLIE);
    assertThat(history.peek(1)).isEqualTo(BAKER);
    assertThat(history.peek(2)).isEqualTo(ABLE);
  }
}
