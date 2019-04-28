package tail;

import org.apache.commons.io.FileUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.kohsuke.args4j.CmdLineException;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.PrintStream;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class TailTest {

    private ByteArrayOutputStream myOut = new ByteArrayOutputStream();
    private ByteArrayOutputStream errOut = new ByteArrayOutputStream();

    @BeforeEach
    void before() {
        System.setOut(new PrintStream(myOut));
        System.setErr(new PrintStream(errOut));
    }

    @Test
    void test1() throws IOException, CmdLineException {
        String[] args = {"input/1.txt", "-c", "17"};
        Tail tail = new Tail(args);
        String[] res = {"rapow",
                "mgklewmalkae"};
        List<String> a = Arrays.asList(res);
        assertEquals(a, tail.run());
    }

    @Test
    void test2() throws IOException, CmdLineException {
        String[] args = {"input/1.txt", "-n", "3"};
        Tail tail = new Tail(args);
        String[] res = {"kmeawgop",
                "orpewarapow",
                "mgklewmalkae"};
        List<String> a = Arrays.asList(res);
        assertEquals(a, tail.run());
    }

    @Test
    void test3() {
        String[] args = {"input/1.txt", "-n", "3", "-c", "10"};
        assertThrows(CmdLineException.class, () -> new Tail(args));
    }

    @Test
    void test4() throws IOException, CmdLineException {
        String[] args = {"input/1.txt"};
        Main.main(args);
        assertEquals(myOut.toString(), "console output mode:" + System.lineSeparator() +
                "ngjewngkjewn" + System.lineSeparator() +
                "dksaglkadgknl" + System.lineSeparator() +
                "kangk" + System.lineSeparator() +
                "adngiowegpawo" + System.lineSeparator() +
                "peoawktpowpogm" + System.lineSeparator() +
                "mklgawmegopa" + System.lineSeparator() +
                "kmgkawmekg" + System.lineSeparator() +
                "kmeawgop" + System.lineSeparator() +
                "orpewarapow" + System.lineSeparator() +
                "mgklewmalkae" + System.lineSeparator());
    }

    @Test
    void test5() throws CmdLineException {
        String[] args = {"e.boiiiiii"};
        Tail tail = new Tail(args);
        assertThrows(IOException.class, tail::run);
    }

    @Test
    void test6() throws CmdLineException, IOException {
        String[] args = {"input/1.txt", "input/2.txt", "-o", "output.txt", "-n", "11"};
        Main.main(args);
        File file = new File("output.txt");
        File file1 = new File("output/test1.txt");
        assertTrue(FileUtils.contentEquals(file1, file));
        file.delete();
    }
}