package change;

import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.runner.Runner;
import org.openjdk.jmh.runner.RunnerException;
import org.openjdk.jmh.runner.options.Options;
import org.openjdk.jmh.runner.options.OptionsBuilder;

import java.util.Collection;
import java.util.concurrent.TimeUnit;
import java.util.stream.IntStream;

@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.MILLISECONDS)
@State(Scope.Benchmark)
@Fork(value = 1, warmups = 2)
public class ChangeResolverAddedSameTypeBenchmark {

    public static void main(String[] args) throws RunnerException {

        Options options = new OptionsBuilder()
                .include(ChangeResolverAddedSameTypeBenchmark.class.getSimpleName())
                .forks(1)
                .build();

        new Runner(options).run();
    }

    @Param({"10000000"})
    private int N;

    private Collection<Pair<Integer, String>> original;

    private Collection<Pair<Integer, String>> incoming;

    @Setup
    public void setup(){
        this.original = IntStream.range(0, N)
                .filter(n -> n % 5 == 0)
                .mapToObj(n -> new Pair<>(n, String.valueOf(n)))
                .toList();

        this.incoming = IntStream.range(0, N)
                .filter(n -> n % 7 == 0)
                .mapToObj(n -> new Pair<>(n, String.valueOf(n)))
                .toList();
    }



    @Benchmark
    public void added_same_type() {
       ChangeResolver.<Pair<Integer, String>, Integer>ofSameType(Pair::x)
                .resolve(original, incoming);
    }

}
