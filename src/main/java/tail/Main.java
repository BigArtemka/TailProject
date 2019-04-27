package tail;

import org.kohsuke.args4j.CmdLineException;
import org.kohsuke.args4j.CmdLineParser;

import java.io.IOException;
import java.util.List;

public class Main {

    public static void main(String[] args) throws CmdLineException, IOException {
        Tail tail = new Tail();
        CmdLineParser parser = new CmdLineParser(tail);
            parser.parseArgument(args);
            List<String> result = tail.run();
            tail.writeResult(result);
    }
}
