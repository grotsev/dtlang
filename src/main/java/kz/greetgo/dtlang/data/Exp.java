package kz.greetgo.dtlang.data;

/**
 * Created by den on 28.05.16.
 */
public abstract class Exp {
    public abstract <T> T accept(V<T> v);

    public interface V<T> {
        T on(Bool exp);

        T on(Num exp);

        T on(Str exp);

        T on(Dat exp);
    }
}
