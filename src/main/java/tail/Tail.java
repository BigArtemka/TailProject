package tail;

import org.apache.commons.io.input.ReversedLinesFileReader;
import org.kohsuke.args4j.Argument;
import org.kohsuke.args4j.CmdLineException;
import org.kohsuke.args4j.CmdLineParser;
import org.kohsuke.args4j.Option;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.InputMismatchException;
import java.util.List;
import java.util.Scanner;

/**
 * @author BigArtemka
 * @version 1
 *
 * Класс, выделяющий из текстового файла(-ов) его(их) конца некоторого размера.
 *
 * Поля:
 *  <b>ArrayList<String></b> inputFileName
 *      Список имен входных файлов.
 *
 *  <b>String</b> o
 *      Имя выходного файла.
 *
 *  <b>int</b> c
 *      Количество выделяемых символов.
 *
 *  <b>int</b> n
 *      Количество выделяемых строк.
 */

public class Tail {

    //Конструктор, работающий с парсером параметров.
    public Tail(String[] args) throws CmdLineException {
        CmdLineParser parser = new CmdLineParser(this);
        parser.parseArgument(args);
    }

    /**
     * Список имен входных файлов. Если параметр отсутствует,
     * входные данные считываются с консольного ввода.
     */
    @Argument(usage = "Input file name", metaVar = "fileName", multiValued = true)
    private ArrayList<String> inputFileName;

    /**
     * [-o]
     * Флаг имени выходного файла. Если отсутствует,
     * результат выводится на консольный вывод.
     */
    @Option(name = "-o", usage = "Sets a name of output file", metaVar = "ofile")
    private String o;

    /**
     * [-c]
     * Флаг, задающий количество выделяемых символов.
     * Не может использоваться совместно с [-n].
     */
    @Option(name = "-c", usage = "Sets a number of symbols", forbids = "-n", metaVar = "num")
    private int c;

    /**
     * [-n]
     * Флаг, задающий количество выделяемых строк.
     * Не может использоваться совместно с [-c].
     */
    @Option(name = "-n", usage = "Sets a number of lines", forbids = "-c", metaVar = "num")
    private int n;

    /**
     *
     * Получение списка из num последних строк, выделяемых флагом [-n].
     * @throws IOException
     *          если не удалось открыть файл, обозначенный указанным путем.
     */
    private List<String> getLastLines() throws IOException {
        List<String> result = new ArrayList<>();
        if (inputFileName != null) {
            for (String anInputFileName : inputFileName) {
                /*
                  Если файлов несколько, перед выводом для
                  каждого файла выводится его имя в отдельной строке
                 */
                if (inputFileName.size() > 1)
                    result.add(anInputFileName);
                File file = new File(anInputFileName);
                try (ReversedLinesFileReader reader = new ReversedLinesFileReader(file)) {
                    for (int i = 0; i < n; i++) {
                        String line = reader.readLine();
                        /*
                        Пока строки в файле не закончатся или не выведется необходимое
                        строк, в список добавляется строка.
                        Так как текст читается с конца, строка добавляется перед раннее
                        добавленными строками.
                         */
                        if (line != null)
                            result.add(result.size() - i, line);
                        else break;
                    }
                }
            }
        } else {
            result = cmdGetLastLines();
        }
        return result;
    }

    /**
     *
     * Получение списка строк, содержащих num
     * последних символов, выделяемых флагом [-c]
     * @throws IOException
     *          если не удалось открыть файл, обозначенный указанным путем.
     */
    private List<String> getLastSymbols() throws IOException {
        List<String> result = new ArrayList<>();
        if (inputFileName != null) {
            for (String anInputFileName : inputFileName) {
                /*
                  Если файлов несколько, перед выводом для
                  каждого файла выводится его имя в отдельной строке
                 */
                if (inputFileName.size() > 1)
                    result.add(anInputFileName);
                File file = new File(anInputFileName);
                try (ReversedLinesFileReader reader = new ReversedLinesFileReader(file)) {
                    int counter = 0;
                    /*
                        Пока строки в файле не закончатся или не выведется необходимое
                        символов, в список добавляется строка.
                        Так как текст читается с конца, строка добавляется перед раннее
                        добавленными строками.
                    */
                    while (c > 0) {
                        String line = reader.readLine();
                        if (line != null)
                            /*
                                Если количество символов, которое осталось
                                вывести, больше числа символов в строке, то
                                добавляется вся строка.
                             */
                            if (c >= line.length()) {
                                result.add(result.size() - counter, line);
                                c -= line.length();
                            } else {
                                //Иначе добавляется подстрока из c последних символов.
                                result.add(result.size() - counter, line.substring(line.length() - c));
                                break;
                            }
                        else break;
                        counter++;
                    }
                }
            }
        } else result = cmdGetLastSymbols();
        return result;
    }

    /**
     *
     * Возвращает список из num последних строк,
     * выделяемых флагом [-n] из консоли,
     * если не указаны имена входных файлов.
     */
    private List<String> cmdGetLastLines() {
        List<String> result = cmdInput();
        /*
        Если количество выделяемых строк больше количества строк,
        введенных в консоль, возвращает все строки.
         */
        if (n > result.size())
            return result;
        else return result.subList(result.size() - n, result.size());
    }

    /**
     * Возвращает список строк, содержащих num
     * последних символов, выделяемых флагом [-c] с консоли,
     * если не указаны имена входных файлов.
     *
     */
    private List<String> cmdGetLastSymbols() {
        List<String> res = cmdInput();
        List<String> result = new ArrayList<>();
        for (int i = res.size() - 1; i >= 0; i--) {
            /*
            Если количество символов, которое осталось
            вывести, больше числа символов в строке, то
            добавляется вся строка.
             */
            if (c >= res.get(i).length()) {
                result.add(res.get(i));
                c -= res.get(i).length();
            } else {
                //Иначе добавляется подстрока из последних c символов.
                result.add(res.get(i).substring(res.get(i).length() - c));
                break;
            }
        }
        return result;
    }

    /**
     * Возвращает список строк, считанных с консольного ввода,
     * если не указаны имена входных файлов.
     */
    private List<String> cmdInput() {
        List<String> res = new ArrayList<>();
        Scanner scan = new Scanner(System.in);
        String str = "";
        System.out.println("console input mode (type 'stop' to stop):");
        while (!str.equals("stop")) {
            str = scan.nextLine();
            res.add(str);
        }
        //удаляет строку с надписью 'stop'.
        res.remove(res.size() - 1);
        return res;
    }

    /**
     * Проверка параметров.
     * @throws InputMismatchException, если получено отрицательное количество
     * строк или символов.
     */
    private void inputCheck() {
        if (c < 0 || n < 0) throw new InputMismatchException();
    }

    /**
     * Записывает результат в файл или на консольный вывод, если имя файла не указано.
     * @throws IOException, если не удается записать выходные данные в файл.
     */
    public void writeResult(List<String> res) throws IOException {
        if (o != null) {
            File f = new File(o);
            BufferedWriter buf = new BufferedWriter(new FileWriter(f));
            for (String re : res) {
                buf.write(re + System.lineSeparator());
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
                //Если ни один из флагов [-c] и [-n] не указан, выводит последние 10 строк.
                n = 10;
                out = getLastLines();
            }
        } else out = getLastSymbols();
        return out;
    }
}

