package kz.greetgo.dtlang.algo;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.*;
import kz.greetgo.dtlang.data.*;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.function.BiFunction;
import java.util.function.BiPredicate;
import java.util.function.Consumer;
import java.util.function.Function;

/**
 * Evaluate AST into value using scope
 * Created by den on 28.05.16.
 */

// TODO trace
public class Eval implements Stmt.V, Consumer<Stmt> {
    public interface Scope {

        // TODO check type conformance
        Boolean getBool(Path path);

        BigDecimal getNum(Path path);

        String getStr(Path path);

        LocalDate getDat(Path path);

        void setBool(Path path, Boolean bool);

        void setNum(Path path, BigDecimal num);

        void setStr(Path path, String str);

        void setDat(Path path, LocalDate dat);
    }

    private final JsonNode root;

    private JsonNode path(List<Path.Segment> segments) {
        JsonNode curr = root;
        for (Path.Segment segment : segments) {
            curr = segment.accept(curr, PATH);
        }
        return curr;
    }

    private final Scope scope = new Scope() {
        @Override
        public Boolean getBool(Path path) {
            return path(path.segments).booleanValue();
        }

        @Override
        public BigDecimal getNum(Path path) {
            return path(path.segments).decimalValue();
        }

        @Override
        public String getStr(Path path) {
            return path(path.segments).textValue();
        }

        @Override
        public LocalDate getDat(Path path) {
            return LocalDate.parse(path(path.segments).textValue());
        }

        @Override
        public void setBool(Path path, Boolean bool) {
            int last = path.segments.size() - 1;
            JsonNode parent = path(path.segments.subList(0, last));
            path.segments.get(last).accept(null, new Path.Segment.VC<Void, Void>() {
                @Override
                public Void on(Void ctx, Path.Segment.Lit path) {
                    ((ObjectNode)parent).put(path.name, bool);
                    return null;
                }

                @Override
                public Void on(Void ctx, Path.Segment.Index path) {
                    ((ArrayNode)parent).set(path.num.accept(NUM).intValue(), BooleanNode.valueOf(bool));
                    return null;
                }

                @Override
                public Void on(Void ctx, Path.Segment.Pred path) {
                    throw  new UnsupportedOperationException("Last segment must not be Pred");
                }
            });
        }

        @Override
        public void setNum(Path path, BigDecimal num) {
            int last = path.segments.size() - 1;
            JsonNode parent = path(path.segments.subList(0, last));
            path.segments.get(last).accept(null, new Path.Segment.VC<Void, Void>() {
                @Override
                public Void on(Void ctx, Path.Segment.Lit path) {
                    ((ObjectNode)parent).put(path.name, num);
                    return null;
                }

                @Override
                public Void on(Void ctx, Path.Segment.Index path) {
                    ((ArrayNode)parent).set(path.num.accept(NUM).intValue(), DecimalNode.valueOf(num));
                    return null;
                }

                @Override
                public Void on(Void ctx, Path.Segment.Pred path) {
                    throw  new UnsupportedOperationException("Last segment must not be Pred");
                }
            });
        }

        @Override
        public void setStr(Path path, String str) {
            int last = path.segments.size() - 1;
            JsonNode parent = path(path.segments.subList(0, last));
            path.segments.get(last).accept(null, new Path.Segment.VC<Void, Void>() {
                @Override
                public Void on(Void ctx, Path.Segment.Lit path) {
                    ((ObjectNode)parent).put(path.name, str);
                    return null;
                }

                @Override
                public Void on(Void ctx, Path.Segment.Index path) {
                    ((ArrayNode)parent).set(path.num.accept(NUM).intValue(), TextNode.valueOf(str));
                    return null;
                }

                @Override
                public Void on(Void ctx, Path.Segment.Pred path) {
                    throw  new UnsupportedOperationException("Last segment must not be Pred");
                }
            });
        }

        @Override
        public void setDat(Path path, LocalDate dat) {
            int last = path.segments.size() - 1;
            JsonNode parent = path(path.segments.subList(0, last));
            path.segments.get(last).accept(null, new Path.Segment.VC<Void, Void>() {
                @Override
                public Void on(Void ctx, Path.Segment.Lit path) {
                    ((ObjectNode)parent).put(path.name, dat.toString());
                    return null;
                }

                @Override
                public Void on(Void ctx, Path.Segment.Index path) {
                    ((ArrayNode)parent).set(path.num.accept(NUM).intValue(), TextNode.valueOf(dat.toString()));
                    return null;
                }

                @Override
                public Void on(Void ctx, Path.Segment.Pred path) {
                    throw  new UnsupportedOperationException("Last segment must not be Pred");
                }
            });
        }
    };
    private final Function<String, Stmt> proc;
    private final LocalDate today; // TODO stop the time to reproduce evaluation

    private static boolean op(int cmp, Bool.Cmp.Op op) {
        switch (op) {
            case EQ:
                return cmp == 0;
            case NE:
                return cmp != 0;
            case LT:
                return cmp < 0;
            case LE:
                return cmp <= 0;
            case GT:
                return cmp > 0;
            case GE:
                return cmp >= 0;
            default:
                throw new IllegalArgumentException("Unknown Bool.Cmp.Op " + op);
        }
    }

    private final Bool.V<Boolean> BOOL = new Bool.V<Boolean>() {
        public Boolean on(Bool.Lit bool) {
            return bool.value;
        }

        public Boolean on(Bool.Var bool) {
            return scope.getBool(bool.path);
        }

        public Boolean on(Bool.Not bool) {
            Boolean b = bool.sub.accept(this);
            return b == null ? null : !b;
        }

        public Boolean on(Bool.And bool) {
            Boolean left = bool.left.accept(this);
            if (Boolean.FALSE.equals(left)) return false;
            Boolean right = bool.right.accept(this);
            if (Boolean.FALSE.equals(right)) return false;
            return left == null || right == null ? null : true;
        }

        public Boolean on(Bool.Or bool) {
            Boolean left = bool.left.accept(this);
            if (Boolean.TRUE.equals(left)) return true;
            Boolean right = bool.right.accept(this);
            if (Boolean.TRUE.equals(right)) return true;
            return left == null || right == null ? null : false;
        }

        @Override
        public Boolean on(Bool.Cmp<?> bool) {
            return bool.left.accept(new Exp.V<Boolean>() {
                @Override
                public Boolean on(Bool exp) {
                    // WARN another semantics is possible for nonstrinct compare: (false <= null) and (null <= true)
                    Boolean left = exp.accept(BOOL);
                    if (left == null) return false;
                    Boolean right = ((Bool) bool.right).accept(BOOL); // type of left = type of right
                    if (right == null) return false;
                    return op(left.compareTo(right), bool.op);
                }

                @Override
                public Boolean on(Num exp) {
                    BigDecimal left = exp.accept(NUM);
                    if (left == null) return false;
                    BigDecimal right = ((Num) bool.right).accept(NUM); // type of left = type of right
                    if (right == null) return false;
                    return op(left.compareTo(right), bool.op);
                }

                @Override
                public Boolean on(Str exp) {
                    String left = exp.accept(STR);
                    if (left == null) return false;
                    String right = ((Str) bool.right).accept(STR); // type of left = type of right
                    if (right == null) return false;
                    return op(left.compareTo(right), bool.op);
                }

                @Override
                public Boolean on(Dat exp) {
                    LocalDate left = exp.accept(DAT);
                    if (left == null) return false;
                    LocalDate right = ((Dat) bool.right).accept(DAT); // type of left = type of right
                    if (right == null) return false;
                    return op(left.compareTo(right), bool.op);
                }
            });
        }
    };

    private static final BiFunction<BigDecimal, BigDecimal, BigDecimal> divide = (d1, d2) -> d1.divide(d2, 9, RoundingMode.HALF_UP);

    private final Num.V<BigDecimal> NUM = new Num.V<BigDecimal>() {

        @Override
        public BigDecimal on(Num.Lit num) {
            return num.value;
        }

        @Override
        public BigDecimal on(Num.Var num) {
            return scope.getNum(num.path);
        }

        private BigDecimal nvl(Num left, Num right, BiFunction<BigDecimal, BigDecimal, BigDecimal> f) {
            BigDecimal l = left.accept(this);
            if (l == null) return null;
            BigDecimal r = right.accept(this);
            if (r == null) return null;
            return f.apply(l, r);
        }

        @Override
        public BigDecimal on(Num.Plus num) {
            return nvl(num.left, num.right, BigDecimal::add);
        }

        @Override
        public BigDecimal on(Num.Minus num) {
            return nvl(num.left, num.right, BigDecimal::subtract);
        }

        @Override
        public BigDecimal on(Num.Mult num) {
            return nvl(num.left, num.right, BigDecimal::multiply);
        }

        @Override
        public BigDecimal on(Num.Div num) {
            return nvl(num.left, num.right, divide);
        }

        @Override
        public BigDecimal on(Num.Len num) {
            return null; // TODO
        }

        @Override
        public BigDecimal on(Num.Min num) {
            return null;
        }

        @Override
        public BigDecimal on(Num.Max num) {
            return null;
        }

        @Override
        public BigDecimal on(Num.Round num) {
            return null;
        }
    };

    private final Str.V<String> STR = new Str.V<String>() {
        @Override
        public String on(Str.Lit str) {
            return str.value;
        }

        @Override
        public String on(Str.Var str) {
            return scope.getStr(str.path);
        }

        @Override
        public String on(Str.Concat str) {
            return str.left.accept(NVL) + str.right.accept(NVL);
        }
    };

    private final Exp.V<String> NVL = new Exp.V<String>() {
        @Override
        public String on(Bool exp) {
            return nvl(exp.accept(BOOL));
        }

        @Override
        public String on(Num exp) {
            return nvl(exp.accept(NUM));
        }

        @Override
        public String on(Str exp) {
            return nvl(exp.accept(STR));
        }

        @Override
        public String on(Dat exp) {
            return nvl(exp.accept(DAT));
        }
    };

    private static String nvl(Object val) {
        return val == null ? "" : val.toString();
    }

    private static LocalDate offset(LocalDate base, BigDecimal offset, BiFunction<LocalDate, Long, LocalDate> f) {
        if (base == null || offset == null) return null;
        return f.apply(base, offset.longValueExact());
    }

    private final Dat.V<LocalDate> DAT = new Dat.V<LocalDate>() {
        @Override
        public LocalDate on(Dat.Lit dat) {
            return dat.value;
        }

        @Override
        public LocalDate on(Dat.Var dat) {
            return scope.getDat(dat.path);
        }

        @Override
        public LocalDate on(Dat.Today dat) {
            return today;
        }

        @Override
        public LocalDate on(Dat.OffsetDay dat) {
            return offset(dat.base.accept(this), dat.offset.accept(NUM), LocalDate::plusDays);
        }

        @Override
        public LocalDate on(Dat.OffsetMonth dat) {
            return offset(dat.base.accept(this), dat.offset.accept(NUM), LocalDate::plusMonths);
        }

        @Override
        public LocalDate on(Dat.OffsetYear dat) {
            return offset(dat.base.accept(this), dat.offset.accept(NUM), LocalDate::plusYears);
        }
    };

    private final Path.Segment.VC<JsonNode, JsonNode> PATH = new Path.Segment.VC<JsonNode, JsonNode>() {

        @Override
        public JsonNode on(JsonNode ctx, Path.Segment.Lit path) {
            while (ctx instanceof ArrayNode) {
                ctx = ((ArrayNode) ctx).get(0);
            }
            if (ctx instanceof ObjectNode) {
                return ((ObjectNode) ctx).get(path.name);
            }
            throw new IllegalArgumentException("Node type must be Object");
        }

        @Override
        public JsonNode on(JsonNode ctx, Path.Segment.Index path) {
            if (ctx instanceof ArrayNode) {
                BigDecimal index = path.num.accept(NUM);
                if (index == null) return null;
                return ((ArrayNode) ctx).get(index.intValue());
            }
            throw new IllegalArgumentException("Node type must be Array");
        }

        @Override
        public JsonNode on(JsonNode ctx, Path.Segment.Pred path) {
            if (ctx instanceof ArrayNode) {
                return path.exp.accept(new Exp.V<JsonNode>() {
                    @Override
                    public JsonNode on(Bool exp) {
                        return findFirst(ctx, path.name, exp.accept(BOOL), (attr, val) ->
                                attr instanceof BooleanNode && val.equals(attr.booleanValue()));
                    }

                    @Override
                    public JsonNode on(Num exp) {
                        return findFirst(ctx, path.name, exp.accept(NUM), (attr, val) ->
                                attr instanceof NumericNode && val.equals(attr.decimalValue()));
                    }

                    @Override
                    public JsonNode on(Str exp) {
                        return findFirst(ctx, path.name, exp.accept(STR), (attr, val) ->
                                attr instanceof TextNode && val.equals(attr.textValue()));
                    }

                    @Override
                    public JsonNode on(Dat exp) {
                        return findFirst(ctx, path.name, exp.accept(DAT), (attr, val) ->
                                attr instanceof TextNode && val.equals(LocalDate.parse(attr.textValue())));
                    }
                });
            }
            throw new IllegalArgumentException("Node type must be Array");
        }
    };

    private static <T> JsonNode findFirst(JsonNode ctx, String name, T val, BiPredicate<JsonNode, T> p) {
        if (val == null) return null;
        ArrayNode array = (ArrayNode) ctx;
        int size = array.size();
        for (int i = 0; i < size; i++) {
            JsonNode item = array.get(i);
            JsonNode attr = item.get(name);
            if (p.test(attr, val)) return item;
        }
        return null;
    }

    public Eval(JsonNode root, Function<String, Stmt> proc, LocalDate today) {
        this.root = root;
        this.proc = proc;
        this.today = today;
    }

    public void on(Stmt.Seq stmt) {
        for (Stmt s : stmt.stmts)
            s.accept(this);
    }

    public void on(Stmt.Alt stmt) {
        for (Stmt.Alt.Case c : stmt.cases)
            if (Boolean.TRUE.equals(c.bool.accept(BOOL))) {
                c.stmt.accept(this);
                break;
            }
    }

    public void on(Stmt.Assign stmt) {
        stmt.exp.accept(new Exp.V<Void>() {
            @Override
            public Void on(Bool exp) {
                scope.setBool(stmt.path, exp.accept(BOOL));
                return null;
            }

            @Override
            public Void on(Num exp) {
                scope.setNum(stmt.path, exp.accept(NUM));
                return null;
            }

            @Override
            public Void on(Str exp) {
                scope.setStr(stmt.path, exp.accept(STR));
                return null;
            }

            @Override
            public Void on(Dat exp) {
                scope.setDat(stmt.path, exp.accept(DAT));
                return null;
            }
        });
    }

    public void on(Stmt.For stmt) {
        BigDecimal i = stmt.from.accept(NUM);
        BigDecimal to = stmt.to.accept(NUM);
        if (i == null) i = BigDecimal.ONE;
        while (to == null || i.compareTo(to) <= 0) { // TODO overcount or timeout exception
            scope.setNum(stmt.path, i);
            i = i.add(BigDecimal.ONE);
            try {
                stmt.stmt.accept(this);
            } catch (JumpException e) {
                if (e.label == null || e.label.equals(stmt.label)) {
                    if (e.exit) break;
                    // else continue;
                } else {
                    throw e;
                }
            }
        }
    }

    public void on(Stmt.Break stmt) {
        throw new JumpException(stmt.label, true);
    }

    public void on(Stmt.Continue stmt) {
        throw new JumpException(stmt.label, false);
    }

    public void on(Stmt.Call stmt) {
        Stmt body = proc.apply(stmt.name);
        accept(body);
    }

    private static class JumpException extends RuntimeException {
        final String label;
        final boolean exit;

        JumpException(String label, boolean exit) {
            this.label = label;
            this.exit = exit;
        }
    }

    public void accept(Stmt stmt) {
        try { // Wrap break and continue
            stmt.accept(this);
        } catch (JumpException ignored) {
        }
    }
}
