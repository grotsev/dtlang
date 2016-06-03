package kz.greetgo.dtlang.data;

/**
 * Created by den on 28.05.16.
 */
public abstract class Stmt { // TODO Cosumer<V>

    public abstract void accept(V v);

    public interface V {
        void on(Seq stmt);

        void on(Alt stmt);

        void on(Assign stmt);

        void on(For stmt);

        void on(Break stmt);

        void on(Continue stmt);

        void on(Call stmt);
    }

    public static class Seq extends Stmt {
        public Iterable<Stmt> stmts;

        public Seq(Iterable<Stmt> stmts) {
            this.stmts = stmts;
        }

        public void accept(V v) {
            v.on(this);
        }
    }

    public static class Alt extends Stmt {
        public static class Case {
            public Bool bool;
            public Stmt stmt;

            public Case(Bool bool) {
                this.bool = bool;
            }
        }

        public Iterable<Case> cases;

        public Alt(Iterable<Case> cases) {
            this.cases = cases;
        }

        public void accept(V v) {
            v.on(this);
        }
    }

    public static class Assign<A> extends Stmt {
        public Path path;
        public Exp exp;

        public Assign(Path path, Exp exp) {
            this.path = path;
            this.exp = exp;
        }

        public void accept(V v) {
            v.on(this);
        }
    }

    public static class For extends Stmt {
        public String label;
        public Path path;
        public Num from, to;
        public Stmt stmt;

        public For(String label) {
            this.label = label;
        }

        public void accept(V v) {
            v.on(this);
        }
    }

    public static class Break extends Stmt {
        public String label;

        public Break(String label) {
            this.label = label;
        }

        public void accept(V v) {
            v.on(this);
        }
    }

    public static class Continue extends Stmt {
        public String label;

        public Continue(String label) {
            this.label = label;
        }

        public void accept(V v) {
            v.on(this);
        }
    }

    public static class Call extends Stmt {
        public String name;

        public Call(String name) {
            this.name = name;
        }

        public void accept(V v) {
            v.on(this);
        }
    }
}
