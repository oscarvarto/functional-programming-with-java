package mx.oscarvarto;

import fj.F;
import fj.Try;
import fj.Unit;
import fj.data.*;
import fj.function.Booleans;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Arrays;
import java.util.Optional;
import java.util.function.Function;
import java.util.function.Predicate;
import lombok.extern.slf4j.Slf4j;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.testng.annotations.Test;

import static fj.Equal.stringEqual;
import static fj.Semigroup.nonEmptyListSemigroup;
import static fj.Show.charShow;
import static fj.Show.stringShow;
import static fj.data.Either.left;
import static fj.data.Either.right;
import static fj.data.List.list;
import static fj.data.Option.none;
import static fj.data.Validation.condition;
import static java.time.format.DateTimeFormatter.ISO_LOCAL_DATE;
import static mx.oscarvarto.Hero.BATMAN;
import static mx.oscarvarto.Hero.OPTIMUS_PRIME;
import static mx.oscarvarto.StringUtils.*;
import static mx.oscarvarto.ValidationConstants.*;
import static mx.oscarvarto.Villain.JOKER;
import static mx.oscarvarto.Villain.LEX_LUTHOR;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;
import static org.checkerframework.checker.nullness.util.NullnessUtil.castNonNull;

@Slf4j
public class FunctionalProgrammingTest {

    public static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    private static final F<String, LocalDateTime> str2LocalDateTime =
            ds -> LocalDateTime.parse(ds, DATE_TIME_FORMATTER);
    // A transformation function from String to LocalDate that may throw a DateTimeParseException
    // Please read the types and try to make sense of them.
    // Exercise: Test this function
    private static final F<String, Validation<DateTimeParseException, LocalDate>> tryReadDate =
            Try.<String, LocalDate, DateTimeParseException>f(str -> LocalDate.parse(str, ISO_LOCAL_DATE));
    // We could handle the exception as a value, but here we decided to simply convert the
    // Validation to an Option
    // Exercise: Test this function
    private static final F<String, Option<LocalDate>> readDate = tryReadDate.andThen(Validation::toOption);
    private static final int MAX_AGE = 130;

    // Caller needs to do null checks/defensive programming to avoid NPE
    @Nullable private static LocalDateTime unsafeParseLocalDateTime(@Nullable String dateString) {
        return dateString == null ? null : LocalDateTime.parse(dateString, DATE_TIME_FORMATTER);
    }

    // NEVER produces a NPE
    // Note: throws DateTimeParseException – if the text cannot be parsed
    private static Optional<LocalDateTime> parseLocalDateTime0(@Nullable String dateString) {
        return Optional.ofNullable(dateString).map(str2LocalDateTime);
    }

    // Alternative implementation.
    // Never throws an exception
    private static Option<LocalDateTime> parseLocalDateTime(String dateString) {
        return Try.<String, LocalDateTime, DateTimeParseException>f(str2LocalDateTime::f).andThen(Validation::toOption)
                .f(dateString);
    }

    private static String getOtherSuperHero() {
        return "Batman";
    }

    // Never throws exceptions
    // This method ignores/swallows the DateTimeParseException by converting it to a Option.none()
    private static Option<LocalDate> parseLocalDate1(@Nullable String maybeDate) {
        var parseF = Try.f((String s) -> LocalDate.parse(s, ISO_LOCAL_DATE)).andThen(Validation::toOption);
        // Write code from smaller blocks/functions!!
        return getNonNull(maybeDate).option(none(), parseF);
    }

    // Never throws exceptions
    // If the parsing fails for some reason, the returned value will contain a message explaining why
    private static Validation<String, LocalDate> parseLocalDate(@Nullable String maybeDate) {
        F<DateTimeParseException, String> formatParseExF =
                pe -> "DateTimeParseException at index: %d".formatted(pe.getErrorIndex());
        F<String, Validation<String, LocalDate>> parseF =
                Try.<String, LocalDate, DateTimeParseException>f(s -> LocalDate.parse(s, ISO_LOCAL_DATE))
                        .andThen(v -> v.f().map(formatParseExF));
        return getNonNull(maybeDate).option(Validation.fail("Null or empty string"), parseF);
    }

    private static <E, T> Validation<NonEmptyList<E>, T> check(final boolean c, final E e, final T t) {
        return condition(c, e, t).nel();
    }

    @Test
    void filterTest1() {
        java.util.List<Character> chars = Arrays.asList('a', 'b', 'A', 'B');
        java.util.List<Character> lowerCaseChars1 = chars.stream().filter(c -> Character.isLowerCase(c)).toList();

        // Same result, but using a Method reference
        java.util.List<Character> lowerCaseChars2 = chars.stream().filter(Character::isLowerCase).toList();


        log.info("Filtered: {}", prettyPrint(lowerCaseChars2));
    }

    // Using fj.data.List
    @Test
    void filterTest2() {
        var chars = list('a', 'b', 'A', 'B');
        var lowerCaseChars2 = chars.filter(Character::isLowerCase);

        log.info("Filtered: {}", pprint(lowerCaseChars2, charShow));
    }

    @Test
    void mapTest1() {
        java.util.List<Character> chars = Arrays.asList('a', 'b', 'A', 'B');
        java.util.List<Character> upperCaseChars1 = chars.stream().map(c -> Character.toUpperCase(c)).toList();

        // Same result, but using a Method reference
        java.util.List<Character> upperCaseChars2 = chars.stream().map(Character::toUpperCase).toList();

        log.info("Mapped to upper case: {}", prettyPrint(upperCaseChars2));
    }

    @Test
    void mapTest2() {
        var chars = list('a', 'b', 'A', 'B');
        var upperCaseChars2 = chars.map(Character::toUpperCase);

        log.info("Mapped to upper case: {}", pprint(upperCaseChars2, charShow));
    }

    @Test
    void flatMapTest1() {
        // Problem: get all the different chars from a sentence
        String[] arrayOfWords = {"Hello", "world"};
        java.util.List<String> uniqueCharacters =
                Arrays.stream(arrayOfWords).map(word -> word.split("")).flatMap(Arrays::stream).distinct().toList();

        log.info("Unique chars: {}", prettyPrint(uniqueCharacters));
    }

    @Test
    void flatMapTest2() {
        // Same problem. bind is the same as flatMap
        String[] arrayOfWords = {"Hello", "world"};
        var uniqueCharacters = list(arrayOfWords).map(word -> word.split("")).bind(fj.data.List::list).nub(stringEqual);

        log.info("Unique chars: {}", pprint(uniqueCharacters, stringShow));
    }

    private void logString(String s) {
        log.info(s);
    }

    private Unit logStr(String s) {
        log.info(s);
        return Unit.unit();
    }

    // Doing defensive programming with explicit null checks.
    // For more complicated scenarios, null checks can be forgotten, opening the possibility of
    // getting NPEs!!
    private <T> void maybeLog0(@Nullable T t) {
        if (t == null)
            return;
        log.info(t.toString());
    }

    // Never throws NPEs
    private <T> void maybeLog1(@Nullable T t) {
        Optional.ofNullable(t).ifPresent(elem -> log.info(elem.toString()));
    }

    // Alternative implementation with fj.data.Option
    @SuppressWarnings("nullness")
    private <T> void maybeLog(@Nullable T t) {
        Option.fromNull(t).foreachDoEffect(elem -> log.info(elem.toString()));
    }

    @Test
    void optionalTest1() {
        // Creation
        Optional<String> maybeName1 = Optional.of("BaTmAn");
        Optional<String> maybeName2 = Optional.empty();

        // map
        Optional<String> allLowerCase1 = maybeName1.map(String::toLowerCase);
        assertThat(allLowerCase1.isPresent()).isTrue();
        Optional<String> noName = maybeName2.map(String::toLowerCase);
        assertThat(noName.isEmpty()).isTrue();

        // Do something if present:
        Optional<String> address = Optional.of("Random Ave");
        address.ifPresent(this::logStr); // if address were empty, logString wouldn't execute.

        // Filter example
        var maybeName3 = Optional.of("    ");
        maybeName3
                // .filter(s -> !s.isBlank())
                .filter(Predicate.not(String::isBlank)).ifPresent(this::logString); // Does not print anything

        // Give an alternative if empty
        // superHero is "Superman" in this example
        String superHero = Optional.<String>empty().orElse("Superman");
        // villain is "Megatron" in this example
        String villain = Optional.<String>empty().orElseGet(() -> "Megatron");

        try {
            // When you are sure the optional should be present, you can do something like
            Optional.<String>empty().orElseThrow(() -> new RuntimeException("Boom!"));
        } catch (RuntimeException e) {
            logString("Boom is expected!");
        }

        // log contents if present, otherwise logs "Was empty".
        // One of two possible actions execute depending on the presence/absence of the value
        // inside the optional
        Optional.<String>empty().ifPresentOrElse(this::logString, () -> logString("Was empty"));

        // Avoiding nulls by transforming to Optional
        // (when you are not sure if you're given a null or not)!
        String maybeName = null;
        maybeLog1(maybeName); // Does not log anything
        LocalDate maybeLocalDate = LocalDate.now(ZoneOffset.UTC);
        maybeLog1(maybeLocalDate); // Does log the local date.
    }

    // Using fj.data.Option
    @Test
    void optionTest2() {
        // Creation
        Option<String> maybeName1 = Option.some("BaTmAn");
        Option<String> maybeName2 = Option.none();

        // map
        Option<String> allLowerCase1 = maybeName1.map(String::toLowerCase);
        assertThat(allLowerCase1.isSome()).isTrue();
        Option<String> noName = maybeName2.map(String::toLowerCase);
        assertThat(noName.isNone()).isTrue();

        // Do something if present:
        Option<String> address = Option.some("Random Ave");
        address.foreachDoEffect(this::logString); // if address were empty, logString wouldn't execute.

        // Filter example
        var maybeName3 = Option.some("    ");
        maybeName3.filter(Booleans.not(String::isBlank)).foreachDoEffect(this::logString); // Does not print anything

        // Give an alternative if empty
        // superHero is "Superman" in this example
        String superHero = Option.<String>none().orSome("Superman");

        String anotherSuperHero = Option.<String>none().orSome(FunctionalProgrammingTest::getOtherSuperHero);
        // Alternatively
        // String anotherSuperHero = Option.<String>none().orSome(() -> getOtherSuperHero());

        // villain is "Megatron" in this example
        String villain1 = Option.<String>none().orSome(() -> "Megatron");

        try {
            // When you are sure the optional should be present, you can do something like
            Option.<String>none().valueE("Boom!");
        } catch (Error e) {
            logString("Boom is expected!");
        }

        // log contents if present, otherwise logs "Was empty".
        // One of two possible actions execute depending on the presence/absence of the value
        // inside the optional
        Option.<String>none().option(logStr("Was empty"), this::logStr);

        // Avoiding nulls by transforming to Optional
        // (when you are not sure if you're given a null or not)!
        String maybeName = null;
        maybeLog(maybeName); // Does not log anything
        LocalDate maybeLocalDate = LocalDate.now(ZoneOffset.UTC);
        maybeLog(maybeLocalDate); // Does log the local date.
    }

    @Test
    void simpleFunctionTest() {
        // Exercise: use this functions and make some simple assertions.
        Function<Integer, String> printInt0 = n -> n.toString();
        F<Integer, String> printInt1 = n -> n.toString();
        F<Integer, String> printInt = Object::toString;
    }

    @Test
    void eitherTest1() {
        var characters =
                List.<Either<Villain, Hero>>list(left(JOKER), right(OPTIMUS_PRIME), left(LEX_LUTHOR), right(BATMAN));
        F<Villain, String> leftF = villain -> villain.name().toLowerCase();

        F<Hero, String> rightF = Enum::name;
        // Alternatively
        // F<Hero, String> rightF = heroe -> heroe.name();

        F<Either<Villain, Hero>, String> nameF = character -> character.either(leftF, rightF);
        fj.data.List<String> formattedNames = characters.map(nameF);
        assertThat(formattedNames).containsExactly("joker", "OPTIMUS_PRIME", "lex_luthor", "BATMAN");
        // You would usually do it like this, and not create so many intermediate variables.
        // We followed the above procedure to help understanding of each function.
        /*
         * fj.data.List<Either<Villain, Hero>> characters = list(left(JOKER), right(OPTIMUS_PRIME),
         * left(LEX_LUTHOR), right(BATMAN)); var formattedNames = characters.map(c -> c.either(v ->
         * v.name().toLowerCase(), Enum::name));
         */

        fj.data.List<Hero> someHeroes = Either.rights(characters); // Get only the heroes
        assertThat(someHeroes).containsExactly(OPTIMUS_PRIME, BATMAN);
        fj.data.List<Villain> someVillains = Either.lefts(characters); // Get only the villains
        assertThat(someVillains).containsExactly(JOKER, LEX_LUTHOR);
    }

    @Test
    void simpleValidationTest() {
        // Docs for Validation<E, T> says:
        // Isomorphic to Either but has renamed functions and represents failure on the left and
        // success on the right.

        Validation<DateTimeParseException, LocalDate> dateOrParseEx1 = tryReadDate.f("2011-12-23");
        assertThat(dateOrParseEx1.isSuccess()).isTrue();
        // Get success value information
        LocalDate date1 = dateOrParseEx1.success();
        assertThat(date1.getYear()).isEqualTo(2011);
        assertThat(date1.getMonthValue()).isEqualTo(12);
        assertThat(date1.getDayOfMonth()).isEqualTo(23);
        assertThat(dateOrParseEx1.toOption()).isNotEmpty();

        Validation<DateTimeParseException, LocalDate> dateOrParseEx2 = tryReadDate.f("2011-13-01");
        assertThat(dateOrParseEx2.isFail()).isTrue();
        // Get information about the failure:
        DateTimeParseException ex2 = dateOrParseEx2.fail();
        var actualErrorMsg = castNonNull(ex2.getMessage());
        log.info(actualErrorMsg);
        var expectedSubstringErrorMsg = "Invalid value for MonthOfYear (valid values 1 - 12): 13";
        assertThat(actualErrorMsg).containsSubsequence(expectedSubstringErrorMsg);
        assertThat(dateOrParseEx2.toOption()).isEmpty();
    }

    // More complex functions used here
    // However, those are built from smaller functions, and the pattern repeats.
    // Understand the smaller blocks/functions, then you'll grasp the "bigger" functions.
    @Test
    void parseLocalDate1Test() {
        String maybeDate = "2011-12-03";
        assertThat(parseLocalDate1(maybeDate)).isNotEmpty();

        String wrongFormatDate = "03-12-2011";
        assertThat(parseLocalDate1(wrongFormatDate)).isEmpty();
    }

    @Test
    void bindTest() {
        var maybeLocalDates = list("2024-01-01", "2024-13-01");
        var validLocalDateTimes = maybeLocalDates.bind(ldt -> parseLocalDate1(ldt).toList());
        // Alternatively, using
        // import static fj.data.List.join;
        // var validLocalDateTimes = join(maybeLocalDates.map(ldt -> parseLocalDate(ldt).toList()));
        assertThat(validLocalDateTimes).hasSize(1);

        fj.data.List<Validation<String, LocalDate>> validatedDates =
                maybeLocalDates.map(FunctionalProgrammingTest::parseLocalDate);
        fj.data.List<LocalDate> correctDates = Validation.successes(validatedDates);
        fj.data.List<String> errorMsgs = validatedDates.filter(Validation::isFail).map(v -> v.fail());
    }

    @Test
    void validationRulesTest1() {
        String expectedMsg = "Name cannot be empty or contain only white space, Age cannot be negative";
        assertThatExceptionOfType(RuntimeException.class).isThrownBy(() -> new Person(" ", -5))
                .withMessage(expectedMsg);
    }

    @Test
    void validationRulesTest2() {
        String expectedMsg = "Age cannot be bigger than %d years".formatted(MAX_AGE);
        assertThatExceptionOfType(RuntimeException.class).isThrownBy(() -> new Person("Chabelo", 340))
                .withMessage(expectedMsg);
    }

    record Person(String name, int age) {

        public Person {
            // Validation rules
            var validatedName = check(!name.isBlank(),
                    NAME_EMPTY_OR_WHITESPACE_ERROR_MSG,
                    name
            );

            var validatedMinAge = check(age >= 0,
                    NEGATIVE_AGE_ERROR_MSG,
                    age
            );

            var validatedMaxAge = check(age <= MAX_AGE,
                    MAX_AGE_ERROR_MSG,
                    age
            );

            validatedName.accumulate(
                    nonEmptyListSemigroup(),
                    validatedMinAge,
                    validatedMaxAge
            ).foreachDoEffect(errors -> {
                throw new RuntimeException(
                        String.join(", ", errors.toCollection())
                );
            });
        }
    }
}
