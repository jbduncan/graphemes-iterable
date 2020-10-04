package org.jbduncan;

import static com.tngtech.archunit.base.DescribedPredicate.not;
import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.classes;
import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.noClasses;

import com.tngtech.archunit.base.DescribedPredicate;
import com.tngtech.archunit.core.domain.JavaClass;
import com.tngtech.archunit.core.domain.JavaModifier;
import com.tngtech.archunit.core.domain.properties.HasAnnotations;
import com.tngtech.archunit.junit.AnalyzeClasses;
import com.tngtech.archunit.junit.ArchTest;
import com.tngtech.archunit.lang.ArchRule;

@AnalyzeClasses(packages = "..")
class ArchitectureTests {
  private static final String TOP_LEVEL_PACKAGE = "org.jbduncan..";

  @ArchTest
  static final ArchRule allJunit5TestsMatchTestNameConventions =
      classes()
          .that()
          .resideInAPackage(TOP_LEVEL_PACKAGE)
          .and(areJunit5Tests())
          .should()
          .haveSimpleNameEndingWith("Tests");

  private static DescribedPredicate<JavaClass> areJunit5Tests() {
    return new DescribedPredicate<>("are JUnit 5 tests") {
      @Override
      public boolean apply(JavaClass input) {
        return isAnnotatedWithJunit5TestAnnotation(input)
            || input.getAllMembers().stream().anyMatch(this::isAnnotatedWithJunit5TestAnnotation);
      }

      private boolean isAnnotatedWithJunit5TestAnnotation(HasAnnotations<?> javaElement) {
        return javaElement.isAnnotatedWith("org.junit.jupiter.api.Test")
            || javaElement.isAnnotatedWith("org.junit.jupiter.params.ParameterizedTest");
      }
    };
  }

  @ArchTest
  public static final ArchRule allJqwikTestsMatchPropertyTestNameConventions =
      classes()
          .that()
          .resideInAPackage(TOP_LEVEL_PACKAGE)
          .and(areJqwikTests())
          .should()
          .haveSimpleNameEndingWith("Properties");

  private static DescribedPredicate<JavaClass> areJqwikTests() {
    return new DescribedPredicate<>("are jqwik tests") {
      @Override
      public boolean apply(JavaClass input) {
        return isAnnotatedWithJqwikTestAnnotation(input)
            || input.getAllMembers().stream().anyMatch(this::isAnnotatedWithJqwikTestAnnotation);
      }

      private boolean isAnnotatedWithJqwikTestAnnotation(HasAnnotations<?> javaElement) {
        return javaElement.isAnnotatedWith("net.jqwik.api.Property")
            || javaElement.isAnnotatedWith("net.jqwik.api.Example");
      }
    };
  }

  @ArchTest
  public static final ArchRule allArchUnitTestsShouldMatchTestNameConventions =
      classes()
          .that()
          .resideInAPackage(TOP_LEVEL_PACKAGE)
          .and(areArchUnitTests())
          .should()
          .haveSimpleNameEndingWith("Tests");

  private static DescribedPredicate<JavaClass> areArchUnitTests() {
    return new DescribedPredicate<>("are ArchUnit tests") {
      @Override
      public boolean apply(JavaClass input) {
        return isAnnotatedWithJqwikTestAnnotation(input)
            || input.getAllMembers().stream().anyMatch(this::isAnnotatedWithJqwikTestAnnotation);
      }

      private boolean isAnnotatedWithJqwikTestAnnotation(HasAnnotations<?> javaElement) {
        return javaElement.isAnnotatedWith("com.tngtech.archunit.junit.AnalyzeClasses")
            || javaElement.isAnnotatedWith("com.tngtech.archunit.junit.ArchTest");
      }
    };
  }

  @ArchTest
  public static final ArchRule disallowUsagesOfJunit3 =
      noClasses()
          .that()
          .resideInAPackage(TOP_LEVEL_PACKAGE)
          .should()
          .dependOnClassesThat(areFromJunit3());

  private static DescribedPredicate<? super JavaClass> areFromJunit3() {
    return new DescribedPredicate<>("are from JUnit 3") {
      @Override
      public boolean apply(JavaClass input) {
        return input.getPackageName().startsWith("junit");
      }
    };
  }

  @ArchTest
  public static final ArchRule disallowUsagesOfJunit4 =
      noClasses()
          .that()
          .resideInAPackage(TOP_LEVEL_PACKAGE)
          .should()
          .dependOnClassesThat(areFromJunit4());

  private static DescribedPredicate<JavaClass> areFromJunit4() {
    return new DescribedPredicate<>("are from JUnit 4") {
      @Override
      public boolean apply(JavaClass input) {
        return input.getPackageName().startsWith("org.junit")
            && !input.getPackageName().startsWith("org.junit.jupiter")
            && !input.getPackageName().startsWith("org.junit.platform");
      }
    };
  }

  @ArchTest
  public static ArchRule
      allNonTestClassesWithPurelyStaticMethodsShouldNotBeInstantiableOrExtendable =
          classes()
              .that()
              .resideInAPackage(TOP_LEVEL_PACKAGE)
              .and()
              .areNotInterfaces()
              .and(haveOnlyStaticMethods())
              .and(not(areJunit5Tests()))
              .and(not(areJqwikTests()))
              .and(not(areArchUnitTests()))
              .should()
              .haveOnlyPrivateConstructors()
              .andShould()
              .haveModifier(JavaModifier.FINAL)
              .because("these classes are not intended to be instantiated or extended");

  private static DescribedPredicate<JavaClass> haveOnlyStaticMethods() {
    return new DescribedPredicate<>("have only static methods") {
      @Override
      public boolean apply(JavaClass input) {
        return input.getMethods().stream()
            .allMatch(m -> m.getModifiers().contains(JavaModifier.STATIC));
      }
    };
  }
}
