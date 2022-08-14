package change;

import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Objects;
import java.util.function.Function;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

class ChangeResolverTest {


    @Test
    void should_return_added_matches_for_same_type() {
        // setup
        List<String> original = List.of();

        List<String> incoming = List.of("one", "two", "three", "four", "five", "six", "seven");

        //  execute
        ChangeResolver.Changes<String, String> changes = ChangeResolver.<String, String>ofSameType(Function.identity())
                .resolve(original, incoming);

        List<String> added = changes.added().toList();

        List<String> removed = changes.removed().toList();

        List<Match<String, String>> changed = changes.altered()
                .toList();

        //  verify
        assertThat(added).hasSize(7);
        assertThat(removed).isEmpty();
        assertThat(changed).isEmpty();
    }

    @Test
    void should_return_removed_matches_for_same_type() {
        // setup
        List<String> original = List.of("one", "two", "three", "four", "five", "six", "seven");

        List<String> incoming = List.of();

        //  execute
        ChangeResolver.Changes<String, String> changes = ChangeResolver.<String, String>ofSameType(Function.identity())
                .resolve(original, incoming);

        List<String> added = changes.added().toList();

        List<String> removed = changes.removed().toList();

        List<Match<String, String>> changed = changes.altered()
                .toList();

        //  verify
        assertThat(added).isEmpty();
        assertThat(removed).hasSize(7).contains("one", "two", "three", "four", "five", "six", "seven");
        assertThat(changed).isEmpty();
    }

    @Test
    void should_return_changed_matches_for_same_type_using_default_change_decider() {
        //  setup

        List<Tuple<Integer, String>> original = List.of(new Tuple<>(1, "one"), new Tuple<>(2, "two"), new Tuple<>(3, "three"), new Tuple<>(4, "four"));

        List<Tuple<Integer, String>> incoming = List.of(new Tuple<>(1, "one"), new Tuple<>(2, "too"), new Tuple<>(3, "three"), new Tuple<>(4, "for"));

        //  execute
        ChangeResolver.Changes<Tuple<Integer, String>, Tuple<Integer, String>> changes = ChangeResolver.<Tuple<Integer, String>, Integer>ofSameType(Tuple::x)
                .resolve(original, incoming);

        List<Tuple<Integer, String>> added = changes.added().toList();

        List<Tuple<Integer, String>> removed = changes.removed().toList();

        List<Match<Tuple<Integer, String>, Tuple<Integer, String>>> changed = changes
                .altered()
                .toList();

        //  verify
        assertThat(added).isEmpty();
        assertThat(removed).isEmpty();
        assertThat(changed).satisfiesExactlyInAnyOrder(
                actual -> assertAll(
                        () -> assertThat(actual.left().x()).isEqualTo(2),
                        () -> assertThat(actual.left().y()).isEqualTo("two"),
                        () -> assertThat(actual.right().x()).isEqualTo(2),
                        () -> assertThat(actual.right().y()).isEqualTo("too")
                ),
                actual -> assertAll(
                        () -> assertThat(actual.left().x()).isEqualTo(4),
                        () -> assertThat(actual.left().y()).isEqualTo("four"),
                        () -> assertThat(actual.right().x()).isEqualTo(4),
                        () -> assertThat(actual.right().y()).isEqualTo("for")
                )
        );
    }

    @Test
    void should_return_changed_matches_for_same_type_using_provided_change_decider() {
        //  setup

        List<Tuple<Integer, String>> original = List.of(new Tuple<>(1, "one"), new Tuple<>(2, "two"), new Tuple<>(3, "three"), new Tuple<>(4, "four"));

        List<Tuple<Integer, String>> incoming = List.of(new Tuple<>(1, "one"), new Tuple<>(2, "too"), new Tuple<>(3, "three"), new Tuple<>(4, "for"));

        //  execute
        ChangeResolver.Changes<Tuple<Integer, String>, Tuple<Integer, String>> changes = ChangeResolver.<Tuple<Integer, String>, Integer>ofSameType(Tuple::x)
                .resolve(original, incoming);

        List<Tuple<Integer, String>> added = changes.added().toList();

        List<Tuple<Integer, String>> removed = changes.removed().toList();

        List<Match<Tuple<Integer, String>, Tuple<Integer, String>>> changed = changes
                .altered((left, right) -> !Objects.equals(right.y(), left.y()))
                .toList();

        //  verify
        assertThat(added).isEmpty();
        assertThat(removed).isEmpty();
        assertThat(changed).hasSize(2)
                .contains(Match.of(new Tuple<>(2, "two"), new Tuple<>(2, "too")))
                .contains(Match.of(new Tuple<>(4, "four"), new Tuple<>(4, "for")));
    }

    @Test
    void should_return_changed_matches_for_differing_types() {
        //  setup

        List<Tuple<Integer, Integer>> original = List.of(new Tuple<>(1, 1), new Tuple<>(2, 2), new Tuple<>(3, 3), new Tuple<>(4, 4));

        List<Tuple<Integer, String>> incoming = List.of(new Tuple<>(1, "1"), new Tuple<>(2, "two"), new Tuple<>(3, "3"), new Tuple<>(4, "four"));

        //  execute
        ChangeResolver.Changes<Tuple<Integer, Integer>, Tuple<Integer, String>> changes = ChangeResolver.<Tuple<Integer, Integer>, Tuple<Integer, String>, Integer>ofDifferingTypes(Tuple::x, Tuple::x)
                .resolve(original, incoming);

        List<Tuple<Integer, String>> added = changes.added().toList();

        List<Tuple<Integer, Integer>> removed = changes.removed().toList();

        List<Match<Tuple<Integer, Integer>, Tuple<Integer, String>>> changed = changes
                .altered((left, right) -> !Objects.equals(right.y(), String.valueOf(left.y())))
                .toList();

        //  verify
        assertThat(added).isEmpty();
        assertThat(removed).isEmpty();
        assertThat(changed).hasSize(2)
                .contains(Match.of(new Tuple<>(2, 2), new Tuple<>(2, "two")))
                .contains(Match.of(new Tuple<>(4, 4), new Tuple<>(4, "four")));
    }

}