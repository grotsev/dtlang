package kz.greetgo.dtlang.data;

/**
 * Created by den on 01.06.16.
 */
public abstract class Str extends Exp {

    public <T> T accept(Exp.V<T> v) {
        return v.on(this);
    }

    public abstract <T> T accept(Str.V<T> v);

    public interface V<T> {
        T on(Lit str);

        T on(Var str);

        T on(Concat str);
    }

    public static class Lit extends Str {
        public String value;

        public Lit(String value) {
            this.value = value;
        }

        public <T> T accept(Str.V<T> v) {
            return v.on(this);
        }
    }

    public static class Var extends Str {
        public Path path;

        public Var(Path path) {
            this.path = path;
        }

        public <T> T accept(Str.V<T> v) {
            return v.on(this);
        }
    }

    public static class Concat extends Str {
        public Exp left, right;

        public Concat(Exp left) {
            this.left = left;
        }

        public <T> T accept(Str.V<T> v) {
            return v.on(this);
        }
    }
}
