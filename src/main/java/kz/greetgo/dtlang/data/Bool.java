package kz.greetgo.dtlang.data;

/**
 * Created by den on 01.06.16.
 */
public abstract class Bool extends Exp {

    public <T> T accept(Exp.V<T> v) {
        return v.on(this);
    }

    public abstract <T> T accept(Bool.V<T> v);

    public interface V<T> {
        T on(Lit bool);

        T on(Var bool);

        T on(Not bool);

        T on(And bool);

        T on(Or bool);

        T on(Cmp<?> bool);

        // TODO (str in str) and (str in strs)
    }

    public static class Lit extends Bool {
        public boolean value;

        public <T> T accept(Bool.V<T> v) {
            return v.on(this);
        }
    }

    public static class Var extends Bool {
        public Path path;

        public <T> T accept(Bool.V<T> v) {
            return v.on(this);
        }
    }

    public static class Not extends Bool {
        public Bool sub;

        public <T> T accept(Bool.V<T> v) {
            return v.on(this);
        }
    }

    public static class And extends Bool {
        public Bool left, right;

        public <T> T accept(Bool.V<T> v) {
            return v.on(this);
        }
    }

    public static class Or extends Bool {
        public Bool left, right;

        public <T> T accept(Bool.V<T> v) {
            return v.on(this);
        }
    }

    public static class Cmp<E extends Exp> extends Bool {
        public static enum Op {EQ, NE, LT, LE, GT, GE}

        public E left, right;
        public Op op;

        public <T> T accept(Bool.V<T> v) {
            return v.on(this);
        }
    }
}
