package org.jbduncan;

import static com.google.common.collect.ImmutableList.toImmutableList;
import static java.util.stream.Collectors.toList;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertIterableEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Iterables;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Spliterator;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.StreamSupport;
import net.jqwik.api.Arbitraries;
import net.jqwik.api.Arbitrary;
import net.jqwik.api.ForAll;
import net.jqwik.api.Property;
import net.jqwik.api.Provide;
import net.jqwik.api.constraints.IntRange;
import net.jqwik.api.constraints.StringLength;

class GraphemesProperties {
  @Property
  void noNonNullStringCausesGraphemesOfToThrowAnException(
      @ForAll @StringLength(max = 99_999) String string) {
    assertDoesNotThrow(() -> Graphemes.of(string));
  }

  @Property
  void noNonNullStringCausesGraphemesReversedOfToThrowAnException(
      @ForAll @StringLength(max = 99_999) String string) {
    assertDoesNotThrow(() -> Graphemes.of(string).reversed());
  }

  @Property
  void noNonNullStringCausesGraphemesIteratorToThrowAnException(
      @ForAll @StringLength(max = 99_999) String string) {
    // when
    var graphemes = Graphemes.of(string);

    // then
    assertDoesNotThrow(() -> exhaust(graphemes.iterator()));
  }

  @Property
  void noNonNullStringCausesGraphemesReversedIteratorToThrowAnException(
      @ForAll @StringLength(max = 99_999) String string) {
    // when
    var graphemes = Graphemes.of(string).reversed();

    // then
    assertDoesNotThrow(() -> exhaust(graphemes.iterator()));
  }

  private void exhaust(Iterator<?> iterator) {
    iterator.forEachRemaining(unused -> {});
  }

  @Property
  void noNonNullStringCausesGraphemesSpliteratorToThrowAnException(
      @ForAll @StringLength(max = 99_999) String string) {
    // when
    var graphemes = Graphemes.of(string);

    // then
    assertDoesNotThrow(() -> exhaust(graphemes.spliterator()));
  }

  @Property
  void noNonNullStringCausesGraphemesReversedSpliteratorToThrowAnException(
      @ForAll @StringLength(max = 99_999) String string) {
    // when
    var graphemes = Graphemes.of(string).reversed();

    // then
    assertDoesNotThrow(() -> exhaust(graphemes.spliterator()));
  }

  private void exhaust(Spliterator<?> spliterator) {
    spliterator.forEachRemaining(unused -> {});
  }

  @Property
  void iteratingOverSameGraphemesMultipleTimesProducesSameResult(
      @ForAll String string, @ForAll @IntRange(min = 2, max = 25) int times) {
    // given
    var results = new HashSet<ImmutableList<String>>();
    var graphemes = Graphemes.of(string);

    // when
    for (int i = 0; i < times; i++) {
      results.add(ImmutableList.copyOf(graphemes.iterator()));
    }

    // then
    assertEquals(1, results.size());
  }

  @Property
  void iteratingOverSameGraphemesReversedMultipleTimesProducesSameResult(
      @ForAll String string, @ForAll @IntRange(min = 2, max = 25) int times) {
    // given
    var results = new HashSet<ImmutableList<String>>();
    var graphemesReversed = Graphemes.of(string).reversed();

    // when
    for (int i = 0; i < times; i++) {
      results.add(ImmutableList.copyOf(graphemesReversed.iterator()));
    }

    // then
    assertEquals(1, results.size());
  }

  @Property
  void exhaustingGraphemesIteratorMakesItEmpty(@ForAll String string) {
    // given
    var graphemes = Graphemes.of(string);

    // when
    var iterator = graphemes.iterator();
    exhaust(iterator);

    // then
    assertFalse(iterator.hasNext());
    assertThrows(NoSuchElementException.class, iterator::next);
  }

  @Property
  void exhaustingGraphemesReversedIteratorMakesItEmpty(@ForAll String string) {
    // given
    var graphemes = Graphemes.of(string).reversed();

    // when
    var iterator = graphemes.iterator();
    exhaust(iterator);

    // then
    assertFalse(iterator.hasNext());
    assertThrows(NoSuchElementException.class, iterator::next);
  }

  @Property
  void spliteratingOverSameGraphemesMultipleTimesProducesSameResult(
      @ForAll String string, @ForAll @IntRange(min = 2, max = 25) int times) {
    // given
    var uniqueResults = new HashSet<ImmutableList<String>>();

    // when
    for (int i = 0; i < times; i++) {
      var graphemesSpliterator = Graphemes.of(string).spliterator();
      uniqueResults.add(
          StreamSupport.stream(graphemesSpliterator, /* parallel= */ false)
              .collect(toImmutableList()));
    }

    // then
    assertEquals(1, uniqueResults.size());
  }

  @Property
  void spliteratingOverSameGraphemesReversedMultipleTimesProducesSameResult(
      @ForAll String string, @ForAll @IntRange(min = 2, max = 25) int times) {
    // given
    var uniqueResults = new HashSet<ImmutableList<String>>();

    // when
    for (int i = 0; i < times; i++) {
      var graphemesReversedSpliterator = Graphemes.of(string).reversed().spliterator();
      uniqueResults.add(
          StreamSupport.stream(graphemesReversedSpliterator, /* parallel= */ false)
              .collect(toImmutableList()));
    }

    // then
    assertEquals(1, uniqueResults.size());
  }

  @Property
  void exhaustingGraphemeSpliteratorMakesItEmpty(@ForAll String string) {
    // given
    var graphemes = Graphemes.of(string);

    // when
    var spliterator = graphemes.spliterator();
    exhaust(spliterator);

    // then
    assertFalse(spliterator.tryAdvance(unused -> {}));
    var counter = new AtomicInteger(0);
    spliterator.forEachRemaining(unused -> counter.incrementAndGet());
    assertEquals(0, counter.intValue());
  }

  @Property
  void exhaustingGraphemesReversedSpliteratorMakesItEmpty(@ForAll String string) {
    // given
    var graphemes = Graphemes.of(string).reversed();

    // when
    var spliterator = graphemes.spliterator();
    exhaust(spliterator);

    // then
    assertFalse(spliterator.tryAdvance(unused -> {}));
    var counter = new AtomicInteger(0);
    spliterator.forEachRemaining(unused -> counter.incrementAndGet());
    assertEquals(0, counter.intValue());
  }

  @Property
  void noNonNullStringCausesGraphemesToReturnNullCharacters(
      @ForAll @StringLength(max = 99_999) String string) {
    // when
    var graphemes = Graphemes.of(string);

    // then
    assertFalse(Iterables.contains(graphemes, null));
  }

  @Property
  void noNonNullStringCausesGraphemesReversedToReturnNullCharacters(
      @ForAll @StringLength(max = 99_999) String string) {
    // when
    var graphemes = Graphemes.of(string).reversed();

    // then
    assertFalse(Iterables.contains(graphemes, null));
  }

  @Property
  void singleCodePointGrapheme(@ForAll @StringLength(1) String codePoint) {
    // when
    var graphemes = Graphemes.of(codePoint);

    // then
    assertIterableEquals(List.of(codePoint), graphemes);
  }

  @Property
  void singleCodePointGraphemeReversed(@ForAll @StringLength(1) String codePoint) {
    // when
    var graphemes = Graphemes.of(codePoint).reversed();

    // then
    assertIterableEquals(List.of(codePoint), graphemes);
  }

  @Property
  void reversingAGraphemesReversedGivesBackTheOriginalGraphemes(@ForAll String string) {
    // given
    var expectedGraphemes = Graphemes.of(string);

    // when
    var doublyReversedGraphemes = expectedGraphemes.reversed().reversed();

    // then
    assertSame(expectedGraphemes, doublyReversedGraphemes);
    assertIterableEquals(expectedGraphemes, doublyReversedGraphemes);
  }

  @Property
  void asciiAlphanumericCharsAndGraphemesAreEqual(
      @ForAll("asciiAlphanumericStrings") String string) {
    // given
    var expectedCharacters =
        string.chars().mapToObj(c -> (char) c).map(String::valueOf).collect(toList());

    // when
    var actualGraphemes = Graphemes.of(string);

    // then
    assertIterableEquals(expectedCharacters, actualGraphemes);
  }

  @Provide
  Arbitrary<String> asciiAlphanumericStrings() {
    return Arbitraries.strings()
        .withCharRange('A', 'Z')
        .withCharRange('a', 'z')
        .withCharRange('0', '9');
  }

  @Property
  void graphemesSpliteratorIsOptimized(@ForAll String string) {
    // when
    var actualGraphemes = Graphemes.of(string);

    // then
    assertTrue(
        actualGraphemes
            .spliterator()
            .hasCharacteristics(Spliterator.IMMUTABLE | Spliterator.NONNULL));
  }

  @Property
  void graphemesReversedSpliteratorIsOptimized(@ForAll String string) {
    // when
    var actualGraphemes = Graphemes.of(string).reversed();

    // then
    assertTrue(
        actualGraphemes
            .spliterator()
            .hasCharacteristics(Spliterator.IMMUTABLE | Spliterator.NONNULL));
  }
}
