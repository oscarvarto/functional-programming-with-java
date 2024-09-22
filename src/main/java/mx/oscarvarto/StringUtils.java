package mx.oscarvarto;

import fj.Equal;
import fj.Ord;
import fj.Ordering;
import fj.Show;
import fj.data.List;
import fj.data.Option;
import fj.data.Set;
import java.util.Arrays;
import java.util.Collection;
import java.util.stream.Collectors;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.PolyNull;

import static fj.Equal.*;
import static fj.Ord.ordDef;
import static fj.Show.listShow;
import static fj.Show.longShow;
import static fj.function.Booleans.not;
import static fj.function.Strings.isNotNullOrBlank;
import static mx.oscarvarto.CollectionUtils.fromNullableCollection;
import static mx.oscarvarto.CollectionUtils.optionsExistAndEqual;
import static org.checkerframework.checker.nullness.util.NullnessUtil.castNonNull;

public class StringUtils {

    public static final Equal<String> stringIgnoreCaseEq = equalDef(String::equalsIgnoreCase);
    public static final Equal<Option<String>> optionStringIgnoreCaseEq = optionEqual(stringIgnoreCaseEq);
    public static final Ord<String> caseInsensitiveStringOrd =
            ordDef((a1, a2) -> Ordering.fromInt(a1.compareToIgnoreCase(a2)));

    private StringUtils() {}

    @SuppressWarnings("nullness")
    public static @NonNull String pformat(@NonNull String s, @NonNull String @NonNull [] args) {
        if (args.length == 0) {
            return s;
        }
        String current = args[0];
        return pformat(s.replaceFirst("\\{}", current), Arrays.copyOfRange(castNonNull(args), 1, args.length));
    }

    public static String format(@NonNull String s, @NonNull String... args) {
        return pformat(s, args);
    }

    public static <T> String prettyPrint(@PolyNull Collection<@PolyNull T> ts) {
        return fromNullableCollection(ts).map(Object::toString).collect(Collectors.joining(", ", "[", "]"));
    }

    public static <T> String pprint(Iterable<T> ts, Show<T> st) {
        return listShow(st).showS(List.iterableList(ts));
    }

    public static String pprint(Iterable<Long> ls) {
        return pprint(ls, longShow);
    }

    public static Option<String> getDigitsOnly(String input) {
        return Option.fromString(input).map(s -> s.replaceAll("\\D", "")).filter(not(String::isEmpty));
    }

    @SuppressWarnings("nullness")
    public static Option<String> getNonNull(@PolyNull String input) {
        return Option.fromString(input);
    }

    @SuppressWarnings("nullness")
    public static Option<String> getNonBlank(@PolyNull String input) {
        return Option.fromString(input).filter(not(String::isBlank));
    }

    public static Set<String> filterNullOrBlank(Ord<String> o, java.util.Set<String> input) {
        return Option.fromNull(input).option(Set.empty(o),
                ss -> Set.iteratorSet(o, ss.stream().filter(isNotNullOrBlank::f).iterator()));
    }

    public static boolean isOptEqual(Equal<String> eqT, String s1, String s2) {
        return optionsExistAndEqual(eqT, getNonNull(s1), getNonNull(s2));
    }

    public static boolean isOptEqual(String s1, String s2) {
        return optionsExistAndEqual(stringEqual, getNonNull(s1), getNonNull(s2));
    }
}
