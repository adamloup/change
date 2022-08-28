package change;

import change.Match.Both;
import change.Match.OnlyLeft;
import change.Match.OnlyRight;

import java.util.Collection;
import java.util.Objects;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Stream;

public class ChangeResolver<L, R, I> {


    public static <X, Y, I> ChangeResolver<X, Y, I> ofDifferingTypes(
            final Function<X, I> leftToIdentifier,
            final Function<Y, I> rightToIdentifier
    ) {
        MatchResolver<X, Y, I> resolver = new DefaultMatchResolver<>(leftToIdentifier, rightToIdentifier);
        return new ChangeResolver<>(resolver);
    }

    public static <X> ChangeResolver<X, X, X> ofSameType() {
        return ofSameType(Function.identity());
    }

    public static <V, I> ChangeResolver<V, V, I> ofSameType(final Function<V, I> toIdentifier) {
        MatchResolver<V, V, I> resolver = new DefaultMatchResolver<>(toIdentifier, toIdentifier);
        return new ChangeResolver<>(resolver);
    }

    private final MatchResolver<L, R, I> resolver;

    private ChangeResolver(final MatchResolver<L, R, I> resolver) {
        this.resolver = resolver;
    }

    public Changes<L, R> resolve(final Collection<L> left, final Collection<R> right) {
        return new Changes<>(resolver.resolve(left, right).toList());
    }

    public static final class Changes<L, R> {

        private static <X, Y> Boolean notEqual(final X x, final Y y) {
            return !Objects.equals(x, y);
        }

        private static <L, R> Predicate<Match<L, R>> changed(final BiFunction<L, R, Boolean> hasChange) {
            return match -> (match instanceof Both<L, R> both) ? both.withBoth(hasChange) : false;
        }

        private final Collection<Match<L, R>> matches;

        private Changes(final Collection<Match<L, R>> matches) {
            this.matches = matches;
        }

        public Stream<R> added() {
            return this.matches
                    .stream()
                    .filter(OnlyRight.class::isInstance)
                    .map(m -> ((OnlyRight<L, R>) m).right());
        }

        public Stream<Both<L, R>> altered() {
            return altered(Changes::notEqual);
        }

        public Stream<Both<L, R>> altered(final BiFunction<L, R, Boolean> hasChange) {
            return this.matches
                    .stream()
                    .filter(changed(hasChange))
                    .map(c -> (Both<L, R>) c)
                    .distinct();
        }

        public Stream<L> removed() {
            return matches
                    .stream()
                    .filter(OnlyLeft.class::isInstance)
                    .map(m -> ((OnlyLeft<L, R>) m).left());
        }


    }

}
