package tail;

import org.kohsuke.args4j.CmdLineException;

import java.io.IOException;
import java.util.List;

public class Main {

    public static void main(String[] args) throws IOException, CmdLineException {
        Tail tail = new Tail(args);
        List<String> result = tail.run();
        tail.writeResult(result);
    }
}
