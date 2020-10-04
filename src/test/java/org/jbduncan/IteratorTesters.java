package org.jbduncan;

import com.google.common.collect.testing.IteratorFeature;
import com.google.common.collect.testing.IteratorTester;
import java.util.Iterator;
import java.util.List;

final class IteratorTesters {
  static IteratorTester<String> iteratorTester(
      Graphemes actualGraphemes, List<String> expectedGraphemes) {
    return new IteratorTester<>(
        expectedGraphemes.size(),
        IteratorFeature.UNMODIFIABLE,
        expectedGraphemes,
        IteratorTester.KnownOrder.KNOWN_ORDER) {
      @Override
      protected Iterator<String> newTargetIterator() {
        return actualGraphemes.iterator();
      }
    };
  }

  private IteratorTesters() {}
}
