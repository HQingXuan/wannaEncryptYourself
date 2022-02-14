package enigma;

import java.util.ArrayList;
import java.util.HashMap;


import static enigma.EnigmaException.*;

/** Class that represents a complete enigma machine.
 *  @author  Haoqing Xuan
 */
class Machine {
    /** variable to track the numRotors. */
    private int _numRotors;
    /** variable to track the pawls.*/
    private int _pawls;
    /** object to store info of allRotors. */
    private ArrayList<Rotor> _allRotors;
    /** Rotor object to store info of rotors. */
    private final Rotor[] _rotors;
    /** Permutation to track the plugboard.*/
    private Permutation _plugBoard;


    /** A new Enigma machine with alphabet ALPHA, 1 < NUMROTORS rotor slots,
     *  and 0 <= PAWLS < NUMROTORS pawls.  ALLROTORS contains all the
     *  available rotors. */
    Machine(Alphabet alpha, int numRotors, int pawls,
            ArrayList<Rotor> allRotors) {
        _alphabet = alpha;
        if (numRotors <= 1) {
            throw error("Wrong number of rotors");
        }
        _rotors = new Rotor[numRotors];
        if (pawls < 0 || pawls > numRotors) {
            throw error("Wrong number of pawls");
        }
        _pawls = pawls;
        if (allRotors.size() == 0) {
            throw error("The set cannot be empty");
        }
        _allRotors = allRotors;


    }

    /** Return the number of rotor slots I have. */
    int numRotors() {
        return _rotors.length;
    }

    /** Return the number pawls (and thus rotating rotors) I have. */
    int numPawls() {
        return _pawls;
    }

    /** @return rotorExist to check whether the rotor exist.
     * @param name */
    boolean rotorExist(String name) {
        for (Rotor r: _allRotors) {
            if (r.name().equals(name)) {
                return true;
            }
        }
        return false;
    }

    /** Set my rotor slots to the rotors named ROTORS from my set of
     *  available rotors (ROTORS[0] names the reflector).
     *  Initially, all rotors are set at their 0 setting. */
    void insertRotors(String[] rotors) {
        if (rotors.length != _rotors.length) {
            throw error("Wrong number of rotors");
        }
        if (rotors.length == 0) {
            throw error("Rotor set cannot be empty");
        }
        for (int i = 0; i < rotors.length; i++) {
            for (Rotor r : _allRotors) {
                if (r.name().equals(rotors[i])) {
                    if (i >= _rotors.length - _pawls && !r.rotates()) {
                        throw error("Rotor at wrong positino");
                    } else if (i < _rotors.length - _pawls && r.rotates()) {
                        throw error("Rotor at wrong position");
                    } else if (i == 0 && !r.reflecting()) {
                        throw error("Rotor at wrong position");
                    }
                    _rotors[i] = r;
                }
            }
        }
        if (_rotors.length != rotors.length
                || _rotors.length == 0
                || _rotors.length != numRotors()) {
            throw new EnigmaException("Wrong info about rotors");
        }


    }

    /** Set my rotors according to SETTING, which must be a string of
     *  numRotors()-1 characters in my alphabet. The first letter refers
     *  to the leftmost rotor setting (not counting the reflector).  */
    void setRotors(String setting) {
        if (setting.length() != _rotors.length - 1) {
            throw error("Rotor length doesn't match");
        }
        for (int i = 1; i < _rotors.length; i++) {
            _rotors[i].set(setting.charAt(i - 1));
            if (!_alphabet.contains(setting.charAt(i - 1))) {
                throw error("Character out of range");
            }
        }
    }

    /** Set the plugboard to PLUGBOARD. */
    void setPlugboard(Permutation plugboard) {
        _plugBoard = plugboard;
    }

    /** Returns the result of converting the input character C (as an
     *  index in the range 0..alphabet size - 1), after first advancing
     *  the machine. */
    int convert(int c) {

        HashMap<Integer, Rotor> advance = new HashMap<>();
        advance.put(_rotors.length - 1, _rotors[_rotors.length - 1]);

        for (int i = _rotors.length - _pawls + 1; i < _rotors.length; i++) {
            if (_rotors[i].atNotch()) {
                advance.put(i, _rotors[i]);
                advance.put(i - 1, _rotors[i - 1]);
            }
        }
        for (Rotor rot : advance.values()) {
            rot.advance();
        }

        int cConverted = c;
        if (_plugBoard != null) {
            cConverted = _plugBoard.permute(c);
        }
        for (int i = _rotors.length - 1; i >= 0; i--) {
            cConverted = _rotors[i].convertForward(cConverted);
        }
        for (int i = 1; i < _rotors.length; i++) {
            cConverted = _rotors[i].convertBackward(cConverted);
        }
        if (_plugBoard != null) {
            cConverted = _plugBoard.invert(cConverted);
        }
        return cConverted;

    }





    /** Returns the encoding/decoding of MSG, updating the state of
     *  the rotors accordingly. */
    String convert(String msg) {
        String decodes = "";
        msg = msg.trim();
        msg = msg.replace(" ", "");
        for (int i = 0; i < msg.length(); i++) {
            if (msg.charAt(i) == ' ') {
                i++;
            }
            decodes += _alphabet.toChar(convert(
                    _alphabet.toInt(msg.charAt(i))));
        }
        return decodes;
    }
    /** Common alphabet of my rotors. */
    private final Alphabet _alphabet;
}

