package mx.oscarvarto;

import com.google.common.base.Predicates;
import com.google.common.collect.Iterables;
import fj.Equal;
import fj.Ord;
import fj.data.List;
import fj.data.Option;
import java.util.Arrays;
import java.util.Collection;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Stream;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.PolyNull;

import static fj.data.List.fromIterator;
import static fj.data.List.iterableList;
import static fj.data.Set.iterableSet;

public class CollectionUtils {
    // as parameter can be null. Returned list will never be null
    public static <A> List<A> listFromIterable(Iterable<A> as) {
        return Option.fromNull(as).option(List.nil(), aas -> iterableList(Iterables.filter(aas, Predicates.notNull())));
    }

    @SafeVarargs
    public static <A> List<A> listFromArray(A... as) {
        return Option.fromNull(as).option(List.nil(),
                aas -> fromIterator(Arrays.stream(aas).filter(Objects::nonNull).iterator()));
    }

    public static <A> fj.data.Set<A> setFromJava(Ord<A> o, Set<A> as) {
        /*
         * return Option.fromNull(as).option( fj.data.Set.empty(o), aas -> iteratorSet(o,
         * aas.stream().filter(Objects::nonNull).iterator()) );
         */
        return iterableSet(o, as);
    }

    public static <T> Stream<@NonNull T> fromNullableCollection(@PolyNull Collection<@PolyNull T> collection) {
        return Stream.ofNullable(collection).flatMap(ts ->
        // ts.stream().filter(Objects::nonNull)
        ts.stream().flatMap(Stream::ofNullable));
    }

    public static <T> boolean optionsExistAndEqual(Equal<T> eqT, Option<T> optT1, Option<T> optT2) {
        return optT1.bind(t1 -> optT2.map(t2 -> eqT.eq(t1, t2))).orSome(false);
    }
}
