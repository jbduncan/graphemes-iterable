package org.jbduncan;

import static java.util.Collections.emptyList;
import static org.jbduncan.IteratorTesters.iteratorTester;
import static org.junit.jupiter.api.Assertions.assertIterableEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import com.google.common.base.Joiner;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.testing.SpliteratorTester;
import com.ibm.icu.text.UnicodeSet;
import java.util.List;
import org.junit.jupiter.api.Test;

class GraphemesTests {

  private static final String SMILEY = "\uD83D\uDE00";
  private static final String UK_FLAG = "\uD83C\uDDEC\uD83C\uDDE7";

  @Test
  void throwsNullPointerExceptionWhenGivenNullText() {
    assertThrows(NullPointerException.class, () -> Graphemes.of(null));
  }

  @Test
  void singleMultibyteGrapheme() {
    // when
    var graphemes = Graphemes.of(SMILEY);

    // then
    assertIterableEquals(List.of(SMILEY), graphemes);
  }

  @Test
  void singleMultibyteGraphemeReversed() {
    // when
    var graphemes = Graphemes.of(SMILEY).reversed();

    // then
    assertIterableEquals(List.of(SMILEY), graphemes);
  }

  @Test
  void noGraphemes() {
    // when
    var graphemes = Graphemes.of("");

    // then
    assertIterableEquals(emptyList(), graphemes);
  }

  @Test
  void noGraphemesReversed() {
    // when
    var graphemes = Graphemes.of("").reversed();

    // then
    assertIterableEquals(emptyList(), graphemes);
  }

  @Test
  void twoAsciiGraphemes() {
    // when
    var graphemes = Graphemes.of("ab");

    // then
    assertIterableEquals(List.of("a", "b"), graphemes);
  }

  @Test
  void twoAsciiGraphemesReversed() {
    // when
    var graphemes = Graphemes.of("ab").reversed();

    // then
    assertIterableEquals(List.of("b", "a"), graphemes);
  }

  @Test
  void twoMultibyteGraphemes() {
    // when
    var graphemes = Graphemes.of(SMILEY + UK_FLAG);

    // then
    assertIterableEquals(List.of(SMILEY, UK_FLAG), graphemes);
  }

  @Test
  void twoMultibyteGraphemesReversed() {
    // when
    var graphemes = Graphemes.of(SMILEY + UK_FLAG).reversed();

    // then
    assertIterableEquals(List.of(UK_FLAG, SMILEY), graphemes);
  }

  @Test
  void allAsciiAlphanumericGraphemes() {
    // given
    var expectedGraphemes = ImmutableList.copyOf(new UnicodeSet("[a-zA-Z0-9]"));

    // when
    var actualGraphemes = Graphemes.of(Joiner.on("").join(new UnicodeSet("[a-zA-Z0-9]")));

    // then
    assertIterableEquals(expectedGraphemes, actualGraphemes);
  }

  @Test
  void graphemesFulfillsIteratorContract() {
    // given
    var expectedGraphemes = List.of("a", "b", "c", "d", "e");

    // when
    var actualGraphemes = Graphemes.of("abcde");

    // then
    iteratorTester(actualGraphemes, expectedGraphemes).test();
  }

  @Test
  void graphemesReversedFulfillsIteratorContract() {
    // given
    var expectedGraphemes = List.of("e", "d", "c", "b", "a");

    // when
    var actualGraphemes = Graphemes.of("abcde").reversed();

    // then
    iteratorTester(actualGraphemes, expectedGraphemes).test();
  }

  @Test
  void graphemesFulfillsIteratorForEachRemainingContract() {
    // given
    var expectedGraphemes = List.of("a", "b", "c", "d", "e");

    // when
    var actualGraphemes = Graphemes.of("abcde");

    // then
    iteratorTester(actualGraphemes, expectedGraphemes).testForEachRemaining();
  }

  @Test
  void graphemesReversedFulfillsIteratorForEachRemainingContract() {
    // given
    var expectedGraphemes = List.of("e", "d", "c", "b", "a");

    // when
    var actualGraphemes = Graphemes.of("abcde").reversed();

    // then
    iteratorTester(actualGraphemes, expectedGraphemes).testForEachRemaining();
  }

  @Test
  void graphemesFulfillsSpliteratorContract() {
    // given
    var expectedGraphemes = List.of("a", "b", "c", "d", "e");

    // when
    var actualGraphemes = Graphemes.of("abcde");

    // then
    SpliteratorTester.of(actualGraphemes::spliterator).expect(expectedGraphemes);
  }

  @Test
  void graphemesReversedFulfillsSpliteratorContract() {
    // given
    var expectedGraphemes = List.of("e", "d", "c", "b", "a");

    // when
    var actualGraphemes = Graphemes.of("abcde").reversed();

    // then
    SpliteratorTester.of(actualGraphemes::spliterator).expect(expectedGraphemes);
  }
}
