package kz.greetgo.dtlang.data;

import java.time.LocalDate;

/**
 * Created by den on 01.06.16.
 */
public abstract class Dat extends Exp {
    public <T> T accept(Exp.V<T> v) {
        return v.on(this);
    }

    public abstract <T> T accept(Dat.V<T> v);

    public interface V<T> {
        T on(Lit dat);

        T on(Var dat);

        T on(Today dat);

        T on(OffsetDay dat);

        T on(OffsetMonth dat);

        T on(OffsetYear dat);
    }

    public static class Lit extends Dat {
        public LocalDate value;

        public <T> T accept(Dat.V<T> v) {
            return v.on(this);
        }
    }

    public static class Var extends Dat {
        public Path path;

        public <T> T accept(Dat.V<T> v) {
            return v.on(this);
        }
    }

    public static class Today extends Dat {
        public <T> T accept(Dat.V<T> v) {
            return v.on(this);
        }
    }

    public static class OffsetDay extends Dat {
        public Dat base;
        public Num offset;

        public <T> T accept(Dat.V<T> v) {
            return v.on(this);
        }
    }

    public static class OffsetMonth extends Dat {
        public Dat base;
        public Num offset;

        public <T> T accept(Dat.V<T> v) {
            return v.on(this);
        }
    }

    public static class OffsetYear extends Dat {
        public Dat base;
        public Num offset;

        public <T> T accept(Dat.V<T> v) {
            return v.on(this);
        }
    }
}
