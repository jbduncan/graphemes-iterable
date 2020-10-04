package org.jbduncan;

import static java.util.Objects.requireNonNull;

import com.ibm.icu.text.BreakIterator;
import java.util.Iterator;
import java.util.Locale;
import java.util.NoSuchElementException;
import java.util.Spliterator;
import java.util.Spliterators;

public abstract class Graphemes implements Iterable<String> {
  public static Graphemes of(String text) {
    return new RegularGraphemes(text);
  }

  public abstract Graphemes reversed();

  @Override
  public Spliterator<String> spliterator() {
    return Spliterators.spliteratorUnknownSize(
        iterator(), Spliterator.IMMUTABLE | Spliterator.NONNULL);
  }

  private static final class RegularGraphemes extends Graphemes {
    private final String text;

    private RegularGraphemes(String text) {
      this.text = requireNonNull(text, "'text' must be non-null");
    }

    @Override
    public Iterator<String> iterator() {
      BreakIterator characterIterator = BreakIterator.getCharacterInstance(Locale.ROOT);
      characterIterator.setText(this.text);

      return new Iterator<>() {
        int start = characterIterator.first();
        int end = characterIterator.next();

        @Override
        public boolean hasNext() {
          return end != BreakIterator.DONE;
        }

        @Override
        public String next() {
          if (!hasNext()) {
            throw new NoSuchElementException();
          }
          String result = RegularGraphemes.this.text.substring(start, end);
          start = end;
          end = characterIterator.next();
          return result;
        }
      };
    }

    @Override
    public Graphemes reversed() {
      return new ReversedGraphemes(this);
    }
  }

  private static final class ReversedGraphemes extends Graphemes {
    private final RegularGraphemes originalGraphemes;

    public ReversedGraphemes(RegularGraphemes graphemes) {
      this.originalGraphemes = requireNonNull(graphemes, "'graphemes' must be non-null");
    }

    @Override
    public Iterator<String> iterator() {
      BreakIterator characterIterator = BreakIterator.getCharacterInstance(Locale.ROOT);
      characterIterator.setText(this.originalGraphemes.text);

      return new Iterator<>() {
        int end = characterIterator.last();
        int start = characterIterator.previous();

        @Override
        public boolean hasNext() {
          return start != BreakIterator.DONE;
        }

        @Override
        public String next() {
          if (!hasNext()) {
            throw new NoSuchElementException();
          }
          String result = ReversedGraphemes.this.originalGraphemes.text.substring(start, end);
          end = start;
          start = characterIterator.previous();
          return result;
        }
      };
    }

    @Override
    public Graphemes reversed() {
      return originalGraphemes;
    }
  }
}
