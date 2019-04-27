package tail;

import org.apache.commons.io.input.ReversedLinesFileReader;
import org.kohsuke.args4j.Option;
import org.kohsuke.args4j.Argument;

import java.io.*;
import java.util.ArrayList;
import java.util.InputMismatchException;
import java.util.List;
import java.util.Scanner;


public class Tail {

    @Argument(usage = "Input file name", metaVar = "fileName", multiValued = true)
    private ArrayList<String> inputFileName;

    @Option(name = "-o", usage = "Sets a name of output file", metaVar = "ofile")
    private String o;

    @Option(name = "-c", usage = "Sets a number of symbols", forbids = "-n", metaVar = "num")
    private int c;

    @Option(name = "-n", usage = "Sets a number of lines", forbids = "-c", metaVar = "num")
    private int n;

    private List<String> getLastLines() throws IOException {
        List<String> result = new ArrayList<>();
        if (inputFileName != null) {
            for (String anInputFileName : inputFileName) {
                if (inputFileName.size() > 1)
                    result.add(anInputFileName);
                File file = new File(anInputFileName);
                ReversedLinesFileReader reader = new ReversedLinesFileReader(file); //try-with-resources
                for (int i = 0; i < n; i++) {
                    String line = reader.readLine();
                    if (line != null)
                        result.add(result.size() - i, line);
                    else break;
                }
                reader.close();
            }
        } else {
            result = cmdGetLastLines();
        }
        return result;
    }

    private List<String> getLastSymbols() throws IOException {
        List<String> result = new ArrayList<>();
        if (inputFileName != null) {
            for (String anInputFileName : inputFileName) {
                if (inputFileName.size() > 1)
                    result.add(anInputFileName);
                File file = new File(anInputFileName);
                ReversedLinesFileReader reader = new ReversedLinesFileReader(file);
                int counter = 0;
                while (c > 0) {
                    String line = reader.readLine();
                    if (line != null)
                        if (c >= line.length()) {
                            result.add(result.size() - counter, line);
                            c -= line.length();
                        } else result.add(result.size() - counter, line.substring(line.length() - c));
                    else break;
                    counter++;
                }
            }
        } else result = cmdGetLastSymbols();
        return result;
    }

    private List<String> cmdGetLastLines() {
        List<String> result = cmdInput();
        if (n > result.size())
            return result;
        else return result.subList(result.size() - n, result.size());
    }

    private List<String> cmdGetLastSymbols() {
        List<String> res = cmdInput();
        List<String> result = new ArrayList<>();
        for (int i = res.size() - 1; i >= 0; i--) {
            if (c >= res.get(i).length()) {
                result.add(res.get(i));
                c -= res.get(i).length();
            } else {
                result.add(res.get(i).substring(res.get(i).length() - c));
                break;
            }
        }
        return result;
    }

    private List<String> cmdInput() {
        List<String> res = new ArrayList<>();
        Scanner scan = new Scanner(System.in);
        String str = "";
        System.out.println("console input mode (type 'stop' to stop):");
        while (!str.equals("stop")) {
            str = scan.nextLine();
            res.add(str);
        }
        res.remove(res.size() - 1);
        return res;
    }

    private void inputCheck() {
        if (c < 0 || n < 0) throw new InputMismatchException();
    }

    public void writeResult(List<String> res) throws IOException {
        if (o != null) {
            File f = new File(o);
            BufferedWriter buf = new BufferedWriter(new FileWriter(f));
            for (String re : res) {
                buf.write(re + "\n"); //System.lineSeparator()
            }
            buf.close();
        } else {
            System.out.println("console output mode:");
            for (String line : res) System.out.println(line);
        }
    }

    public List<String> run() throws IOException {
        inputCheck();
        List<String> out;
        if (c == 0) {
            if (n != 0)
                out = getLastLines();
            else {
                n = 20;
                out = getLastLines();
            }
        } else out = getLastSymbols();
        return out;
    }
}

