package change;

import java.util.Collection;
import java.util.Map;
import java.util.Objects;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import change.Match.OnlyLeft;

import change.Match.OnlyRight;

import change.Match.Both;

public class ChangeResolver<L, R, I> {

    public static <X, Y, I> ChangeResolver<X, Y, I> ofDifferingTypes(
            final Function<X, I> leftToIdentifier,
            final Function<Y, I> rightToIdentifier
    ) {
        return new ChangeResolver<>(leftToIdentifier, rightToIdentifier);
    }

    public static <V, I> ChangeResolver<V, V, I> ofSameType(final Function<V, I> toIdentifier) {
        return new ChangeResolver<>(toIdentifier, toIdentifier);
    }

    private final Function<L, I> leftToIdentifier;
    private final Function<R, I> rightToIdentifier;

    private ChangeResolver(
            final Function<L, I> leftToIdentifier,
            final Function<R, I> rightToIdentifier
    ) {
        this.leftToIdentifier = leftToIdentifier;
        this.rightToIdentifier = rightToIdentifier;
    }

    public Changes<L, R> resolve(final Collection<L> left, final Collection<R> right) {
        Map<I, L> leftMap = resolveLeft(left);

        Map<I, R> rightMap = resolveRight(right);

        return resolveMatches(leftMap, rightMap).collect(
                Collectors.collectingAndThen(
                        Collectors.toUnmodifiableList(),
                        Changes::new
                )
        );
    }

    private Stream<Match<L, R>> resolveMatches(
            final Map<I, L> lefts,
            final Map<I, R> rights
    ) {
        return Stream.concat(
                lefts.entrySet().stream()
                        .map(e -> Match.of(e.getValue(), rights.get(e.getKey()))),
                rights.entrySet().stream()
                        .map(e -> Match.of(lefts.get(e.getKey()), e.getValue()))
        );
    }

    private Map<I, R> resolveRight(final Collection<R> right) {
        return right.stream().collect(Collectors
                .toMap(rightToIdentifier, Function.identity()));
    }

    private Map<I, L> resolveLeft(final Collection<L> left) {
        return left.stream().collect(Collectors
                .toMap(leftToIdentifier, Function.identity()));

    }

    public static class Changes<L, R> {

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
                    .map(m -> ((OnlyRight<L,R>) m).right());
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
                    .map(m -> ((OnlyLeft<L,R>) m).left());
        }


    }

}
