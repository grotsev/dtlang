package kz.greetgo.dtlang.data;

import java.math.BigDecimal;

/**
 * Created by den on 01.06.16.
 */
public abstract class Num extends Exp {

    public <T> T accept(Exp.V<T> v) {
        return v.on(this);
    }

    public abstract <T> T accept(Num.V<T> v);

    public interface V<T> {
        T on(Lit num);

        T on(Var num);

        T on(Plus num);

        T on(Minus num);

        T on(Mult num);

        T on(Div num);

        T on(Len num);

        T on(Min num);

        T on(Max num);

        T on(Round num);
    }

    public static class Lit extends Num {
        public BigDecimal value;

        public Lit(BigDecimal value) {
            this.value = value;
        }

        public <T> T accept(Num.V<T> v) {
            return v.on(this);
        }
    }

    public static class Var extends Num {
        public Path path;

        public Var(Path path) {
            this.path = path;
        }

        public <T> T accept(Num.V<T> v) {
            return v.on(this);
        }
    }

    public static class Plus extends Num {
        public Num left, right;

        public Plus(Num left, Num right) {
            this.left = left;
            this.right = right;
        }

        public <T> T accept(Num.V<T> v) {
            return v.on(this);
        }
    }

    public static class Minus extends Num {
        public Num left, right;

        public Minus(Num left, Num right) {
            this.left = left;
            this.right = right;
        }


        public <T> T accept(Num.V<T> v) {
            return v.on(this);
        }
    }

    public static class Mult extends Num {
        public Num left, right;

        public Mult(Num left, Num right) {
            this.left = left;
            this.right = right;
        }


        public <T> T accept(Num.V<T> v) {
            return v.on(this);
        }
    }

    public static class Div extends Num {
        public Num left, right;

        public Div(Num left, Num right) {
            this.left = left;
            this.right = right;
        }


        public <T> T accept(Num.V<T> v) {
            return v.on(this);
        }
    }

    public static class Len extends Num {
        public Path path;

        public Len(Path path) {
            this.path = path;
        }

        public <T> T accept(Num.V<T> v) {
            return v.on(this);
        }
    }

    public static class Min extends Num {
        public Path path;

        public Min(Path path) {
            this.path = path;
        }

        public <T> T accept(Num.V<T> v) {
            return v.on(this);
        }
    }

    public static class Max extends Num {
        public Path path;

        public Max(Path path) {
            this.path = path;
        }

        public <T> T accept(Num.V<T> v) {
            return v.on(this);
        }
    }

    public static class Round extends Num {
        public Num left, right;

        public Round(Num left, Num right) {
            this.left = left;
            this.right = right;
        }


        public <T> T accept(Num.V<T> v) {
            return v.on(this);
        }
    }
}
