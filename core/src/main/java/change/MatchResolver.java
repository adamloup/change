package change;

import java.util.Collection;
import java.util.stream.Stream;

public interface MatchResolver<L, R, I> {
    Stream<Match<L, R>> resolve(Collection<L> lefts, Collection<R> rights);
}
