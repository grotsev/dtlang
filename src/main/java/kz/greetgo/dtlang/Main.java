package kz.greetgo.dtlang;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import kz.greetgo.dtlang.algo.Eval;
import kz.greetgo.dtlang.data.Num;
import kz.greetgo.dtlang.data.Path;
import kz.greetgo.dtlang.data.Stmt;
import kz.greetgo.dtlang.data.Str;

import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;
import java.net.MalformedURLException;
import java.net.URL;
import java.time.LocalDate;
import java.util.Arrays;

/**
 * Created by den on 31.05.16.
 */
public class Main {
    private static final Path SUM = new Path(Arrays.asList(
            new Path.Segment.Lit("client"),
            new Path.Segment.Lit("account"),
            new Path.Segment.Index(new Num.Lit(BigDecimal.ONE)),
            new Path.Segment.Lit("sum")
    ));
    private static final Stmt.Assign STMT = new Stmt.Assign<Num>(
            SUM,
            new Num.Plus(new Num.Var(SUM), new Num.Lit(new BigDecimal(123)))
    );

    //private static
    public static void main(String[] args) {
        ObjectMapper objectMapper = new ObjectMapper();


        URL url = Main.class.getResource("example.json");
        try {
            JsonNode node = objectMapper.readValue(url, JsonNode.class);

            Eval eval = new Eval(node, null, LocalDate.now());
            System.out.println(node);
            eval.accept(STMT);
            System.out.println(node);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
