package kz.greetgo.dtlang.data;

import java.util.List;

/**
 * Created by den on 28.05.16.
 */
public class Path {
    public List<Segment> segments;

    public Path(List<Segment> segments) {
        this.segments = segments;
    }

    public abstract static class Segment {
        public abstract <C, T> T accept(C ctx, VC<C, T> v);

        public interface VC<C, T> {
            T on(C ctx, Lit path);

            T on(C ctx, Index path);

            T on(C ctx, Pred path);
        }

        public static class Lit extends Segment {
            public String name;

            public Lit(String name) {
                this.name = name;
            }

            public <C, T> T accept(C ctx, VC<C, T> v) {
                return v.on(ctx, this);
            }
        }

        public static class Index extends Segment {
            public Num num;

            public Index(Num num) {
                this.num = num;
            }

            public <C, T> T accept(C ctx, VC<C, T> v) {
                return v.on(ctx, this);
            }
        }

        public static class Pred extends Segment {
            public String name;
            public Exp exp;

            public Pred(String name) {
                this.name = name;
            }

            public <C, T> T accept(C ctx, VC<C, T> v) {
                return v.on(ctx, this);
            }
        }
    }
}
