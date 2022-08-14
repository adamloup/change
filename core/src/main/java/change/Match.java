package change;

import java.util.function.BiFunction;
import java.util.function.Predicate;

public sealed interface Match<L, R> {

    static <L, R> Predicate<Match<L, R>> onlyRight() {
        return match -> match.left() == null && match.right() != null;
    }

    static <L, R> Predicate<Match<L, R>> both() {
        return match -> match.left() != null && match.right() != null;
    }

    static <L, R> Predicate<Match<L, R>> onlyLeft() {
        return match -> match.left() != null && match.right() == null;
    }

    static <L, R> Match<L, R> of(L left, R right) {
        if (left != null && right != null) {
            return new Both<>(left, right);
        } else if (left == null && right != null) {
            return new OnlyRight<>(right);
        } else if (left != null) {
            return new OnlyLeft<>(left);
        } else {
            throw new IllegalStateException("Either a left or a right value is required.");
        }
    }

    record OnlyRight<L, R>(R right) implements Match<L, R> {

    }

    record OnlyLeft<L, R>(L left) implements Match<L, R> {
    }

    record Both<L, R>(L left, R right) implements Match<L, R> {
    }

    default L left() {
        return null;
    }

    default R right() {
        return null;
    }

    default <T> T withBoth(final BiFunction<L, R, T> function) {
        return function.apply(left(), right());
    }
}
