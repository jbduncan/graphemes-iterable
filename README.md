# Graphemes

This project implements a simple `Iterable<String>` wrapper around ICU4J's
`BreakIterator` for splitting strings into human-readable characters.

However, this project is really just an excuse for me to get to grips with
[jqwik](https://jqwik.net/) for Property-based Testing and
[ArchUnit](https://github.com/TNG/ArchUnit) for writing tests about the code in
the project. See the tests in `src/test/java/org/jbduncan` for some examples.

I used the [jqwik User Guide](https://jqwik.net/docs/current/user-guide.html),
the
[ArchUnit User Guide](https://www.archunit.org/userguide/html/000_Index.html),
and the jqwik maintainer's
[introduction to Property-based Testing](https://blog.johanneslink.net/2018/03/24/property-based-testing-in-java-introduction/) -
especially their article on
["Patterns to Find Good Properties"](https://blog.johanneslink.net/2018/07/16/patterns-to-find-properties/) -
to write these tests.
