package org.jbduncan;

import static com.google.common.collect.ImmutableList.toImmutableList;
import static java.util.stream.Collectors.toList;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

import com.google.common.collect.ImmutableList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Spliterator;
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
    var uniqueResults = new HashSet<ImmutableList<String>>();
    var graphemes = Graphemes.of(string);

    // when
    for (int i = 0; i < times; i++) {
      uniqueResults.add(ImmutableList.copyOf(graphemes.iterator()));
    }

    // then
    assertThat(uniqueResults).hasSize(1);
  }

  @Property
  void iteratingOverSameGraphemesReversedMultipleTimesProducesSameResult(
      @ForAll String string, @ForAll @IntRange(min = 2, max = 25) int times) {
    // given
    var uniqueResults = new HashSet<ImmutableList<String>>();
    var graphemesReversed = Graphemes.of(string).reversed();

    // when
    for (int i = 0; i < times; i++) {
      uniqueResults.add(ImmutableList.copyOf(graphemesReversed.iterator()));
    }

    // then
    assertThat(uniqueResults).hasSize(1);
  }

  @Property
  void spliteratingOverSameGraphemesMultipleTimesProducesSameResult(
      @ForAll String string, @ForAll @IntRange(min = 2, max = 25) int times) {
    // given
    var uniqueResults = new HashSet<ImmutableList<String>>();
    var graphemes = Graphemes.of(string);

    // when
    for (int i = 0; i < times; i++) {
      var graphemesSpliterator = graphemes.spliterator();
      uniqueResults.add(
          StreamSupport.stream(graphemesSpliterator, /* parallel= */ false)
              .collect(toImmutableList()));
    }

    // then
    assertThat(uniqueResults).hasSize(1);
  }

  @Property
  void spliteratingOverSameGraphemesReversedMultipleTimesProducesSameResult(
      @ForAll String string, @ForAll @IntRange(min = 2, max = 25) int times) {
    // given
    var uniqueResults = new HashSet<ImmutableList<String>>();
    var graphemesReversed = Graphemes.of(string).reversed();

    // when
    for (int i = 0; i < times; i++) {
      var graphemesReversedSpliterator = graphemesReversed.spliterator();
      uniqueResults.add(
          StreamSupport.stream(graphemesReversedSpliterator, /* parallel= */ false)
              .collect(toImmutableList()));
    }

    // then
    assertThat(uniqueResults).hasSize(1);
  }

  @Property
  void exhaustingGraphemesSpliteratorMakesItEmpty(@ForAll String string) {
    // given
    var graphemes = Graphemes.of(string);

    // when
    var spliterator = graphemes.spliterator();
    exhaust(spliterator);

    // then
    assertThat(spliterator.tryAdvance(unused -> {})).isFalse();
  }

  @Property
  void exhaustingGraphemesReversedSpliteratorMakesItEmpty(@ForAll String string) {
    // given
    var graphemes = Graphemes.of(string).reversed();

    // when
    var spliterator = graphemes.spliterator();
    exhaust(spliterator);

    // then
    assertThat(spliterator.tryAdvance(unused -> {})).isFalse();
  }

  @Property
  void noNonNullStringCausesGraphemesToReturnNullCharacters(
      @ForAll @StringLength(max = 99_999) String string) {
    // when
    var graphemes = Graphemes.of(string);

    // then
    assertThat(graphemes).doesNotContainNull();
  }

  @Property
  void noNonNullStringCausesGraphemesReversedToReturnNullCharacters(
      @ForAll @StringLength(max = 99_999) String string) {
    // when
    var graphemes = Graphemes.of(string).reversed();

    // then
    assertThat(graphemes).doesNotContainNull();
  }

  @Property
  void singleCodePointGrapheme(@ForAll @StringLength(1) String codePoint) {
    // when
    var graphemes = Graphemes.of(codePoint);

    // then
    assertThat(graphemes).containsExactly(codePoint);
  }

  @Property
  void singleCodePointGraphemeReversed(@ForAll @StringLength(1) String codePoint) {
    // when
    var graphemes = Graphemes.of(codePoint).reversed();

    // then
    assertThat(graphemes).containsExactly(codePoint);
  }

  @Property
  void reversingAGraphemesReversedGivesBackTheOriginalGraphemes(@ForAll String string) {
    // given
    var expectedGraphemes = Graphemes.of(string);

    // when
    var doublyReversedGraphemes = expectedGraphemes.reversed().reversed();

    // then
    assertThat(doublyReversedGraphemes).isSameAs(expectedGraphemes).isEqualTo(expectedGraphemes);
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
    assertThat(actualGraphemes).containsExactlyElementsOf(expectedCharacters);
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
    assertThat(actualGraphemes.spliterator())
        .hasOnlyCharacteristics(Spliterator.IMMUTABLE, Spliterator.NONNULL);
  }

  @Property
  void graphemesReversedSpliteratorIsOptimized(@ForAll String string) {
    // when
    var actualGraphemes = Graphemes.of(string).reversed();

    // then
    assertThat(actualGraphemes.spliterator())
        .hasOnlyCharacteristics(Spliterator.IMMUTABLE, Spliterator.NONNULL);
  }
}
