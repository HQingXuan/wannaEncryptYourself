package enigma;

import java.io.File;
import java.io.IOException;
import java.io.PrintStream;

import java.util.ArrayList;
import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.Scanner;

import static enigma.EnigmaException.*;

/** Enigma simulator.
 *  @author Haoqing Xuan
 */
public final class Main {

    /** Process a sequence of encryptions and decryptions, as
     *  specified by ARGS, where 1 <= ARGS.length <= 3.
     *  ARGS[0] is the name of a configuration file.
     *  ARGS[1] is optional; when present, it names an input file
     *  containing messages.  Otherwise, input comes from the standard
     *  input.  ARGS[2] is optional; when present, it names an output
     *  file for processed messages.  Otherwise, output goes to the
     *  standard output. Exits normally if there are no errors in the input;
     *  otherwise with code 1. */
    public static void main(String... args) {
        try {
            new Main(args).process();
            return;
        } catch (EnigmaException excp) {
            System.err.printf("Error: %s%n", excp.getMessage());
        }
        System.exit(1);
    }

    /** Check ARGS and open the necessary files (see comment on main). */
    Main(String[] args) {
        if (args.length < 1 || args.length > 3) {
            throw error("Only 1, 2, or 3 command-line arguments allowed");
        }

        _config = getInput(args[0]);

        if (args.length > 1) {
            _input = getInput(args[1]);
        } else {
            _input = new Scanner(System.in);
        }

        if (args.length > 2) {
            _output = getOutput(args[2]);
        } else {
            _output = System.out;
        }
    }

    /** Return a Scanner reading from the file named NAME. */
    private Scanner getInput(String name) {
        try {
            return new Scanner(new File(name));
        } catch (IOException excp) {
            throw error("could not open %s", name);
        }
    }

    /** Return a PrintStream writing to the file named NAME. */
    private PrintStream getOutput(String name) {
        try {
            return new PrintStream(new File(name));
        } catch (IOException excp) {
            throw error("could not open %s", name);
        }
    }

    /** Configure an Enigma machine from the contents of configuration
     *  file _config and apply it to the messages in _input, sending the
     *  results to _output. */
    private void process() {
        Machine enigma = readConfig();
        String next = _input.nextLine();
        if (next.charAt(0) != '*') {
            throw error("wrong input format");
        }

        setUp(enigma, next);
        while (_input.hasNextLine()) {
            String nextLine = _input.nextLine();
            if (nextLine.isEmpty()) {
                _output.println(nextLine);
                continue;
            }
            if (nextLine.contains("*")) {
                setUp(enigma, nextLine);
            } else {
                String output = enigma.convert(nextLine);
                printMessageLine(output);
            }
        }
    }

    /** Return an Enigma machine configured from the contents of configuration
     *  file _config. */
    private Machine readConfig() {
        try {
            _alphabet = new Alphabet(_config.next());
            if (!_config.hasNextLine()) {
                throw error("Wrong input");
            }
            int numRotors = _config.nextInt();
            if (!_config.hasNextLine()) {
                throw error("Wrong input");
            }
            int pawls = _config.nextInt();
            ArrayList<Rotor> allRotor = new ArrayList<>();
            while (_config.hasNext()) {
                Rotor rotor = readRotor();
                allRotor.add(rotor);
            }
            for (int i = 0; i < allRotor.size(); i++) {
                for (int j = 0; j < allRotor.size(); j++) {
                    if (i != j && Objects.equals(allRotor.get(i).name(),
                            allRotor.get(j).name())) {
                        throw error("Duplicate rotors"
                                + allRotor.get(j).name());
                    }
                }
            }
            return new Machine(_alphabet,
                    numRotors, pawls, allRotor);
        } catch (NoSuchElementException excp) {
            throw error("configuration file truncated");
        }
    }

    /** Return a rotor, reading its description from _config. */
    private Rotor readRotor() {
        try {
            if (!_config.hasNext()) {
                throw error("No proper type for the rotor");
            }
            String rotorname = _config.next();
            String notch = _config.next();
            String permuted = "";
            String temp;



            if (rotorname.contains("(") || rotorname.contains(")")) {
                throw error("Incorrect format for rotor name");
            }
            while (_config.hasNext("\\(.+\\)")) {
                temp = _config.next();
                permuted += temp;
            }

            if (notch.indexOf("N") == 0) {
                Rotor rotor = new FixedRotor(rotorname,
                        new Permutation(permuted, _alphabet));

                return rotor;

            } else if (notch.indexOf("R") == 0) {
                Rotor rotor = new Reflector(rotorname,
                        new Permutation(permuted, _alphabet));

                return rotor;
            } else {
                Rotor rotor = new MovingRotor(rotorname,
                        new Permutation(permuted, _alphabet),
                        notch.substring(1));

                return rotor;
            }
        } catch (NoSuchElementException excp) {
            throw error("bad rotor description");
        }
    }


    /** Set M according to the specification given on SETTINGS,
     *  which must have the format specified in the assignment. */
    private void setUp(Machine M, String settings) {
        String[] contents = settings.split(" ");
        if (contents.length < M.numRotors() + 1) {
            throw error("Need more info about rotors");
        }
        String[] rotorInfo = new String[M.numRotors()];
        for (int i = 0; i < M.numRotors(); i++) {
            if (M.rotorExist(contents[i + 1])) {
                rotorInfo[i] = contents[i + 1];
            } else {
                throw new EnigmaException("invalid rotor name");
            }

        }
        M.insertRotors(rotorInfo);
        M.setRotors(contents[M.numRotors() + 1]);

        if (M.numRotors() + 2 < contents.length) {
            String cycles = "";
            for (int i = M.numRotors() + 2; i < contents.length; i++) {
                cycles += contents[i];
            }
            M.setPlugboard(new Permutation(cycles, _alphabet));
        }
    }

    /** Print MSG in groups of five (except that the last group may
     *  have fewer letters). */
    private void printMessageLine(String msg) {
        if (msg.isEmpty()) {
            _output.println();
        }
        for (int i = 0; i < msg.length(); i++) {
            _output.print(msg.charAt(i));
            if ((i + 1) % 5 == 0 && i != msg.length() - 1) {
                _output.print(" ");
            }
        }
        _output.print("\r\n");
    }

    /** Alphabet used in this machine. */
    private Alphabet _alphabet;

    /** Source of input messages. */
    private Scanner _input;

    /** Source of machine configuration. */
    private Scanner _config;

    /** File for encoded/decoded messages. */
    private PrintStream _output;

    /** String that can track each rotor's type in the file. */
    private String rotorName;
}
