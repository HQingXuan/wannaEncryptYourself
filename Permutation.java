package enigma;

import static enigma.EnigmaException.*;

/** Represents a permutation of a range of integers starting at 0 corresponding
 *  to the characters of an alphabet.
 *   Citation: I learned how to replace single element from a string from
 *  * https://stackoverflow.com/questions/13386107/.
 *  * how-to-remove-single-character-from-a-string/32757971
 *  @author Haoqing Xuan
 */


class Permutation {
    /** String[] variable to store cycles.*/
    private String[] _cycles;

    /** Set this Permutation to that specified by CYCLES, a string in the
     *  form "(cccc) (cc) ..." where the c's are characters in ALPHABET, which
     *  is interpreted as a permutation in cycle notation.  Characters in the
     *  alphabet that are not included in any cycle map to themselves.
     *  Whitespace is ignored. */
    Permutation(String cycles, Alphabet alphabet) {
        _alphabet = alphabet;
        String newCycle = cycles;
        newCycle = newCycle.replace("(", "");
        newCycle = newCycle.replace(" ", "");
        newCycle = newCycle.replace(")", ",");
        _cycles = newCycle.split(",");
    }

    /** Add the cycle c0->c1->...->cm->c0 to the permutation, where CYCLE is
     *  c0c1...cm.
     * creating a newCycle that has one more length than cycle to
     * store all elements in cycle, and then point the last
     * element of newCycle to cycle. */

    private void addCycle(String cycle) {
        String[] newCycle = new String[_cycles.length + 1];
        for (int i = 0; i < _cycles.length; i++) {
            newCycle[i] = _cycles[i];
            newCycle[i + 1] = cycle;
            _cycles = newCycle;
        }
    }

    /** Return the value of P modulo the size of this permutation. */
    final int wrap(int p) {
        int r = p % size();
        if (r < 0) {
            r += size();
        }
        return r;
    }

    /** Returns the size of the alphabet I permute. */
    int size() {
        return _alphabet.size();
    }

    /** Return the result of applying this permutation to P modulo the
     *  alphabet size.
     * locating the character in the cycles, after finding the character,
     * find the character that has index + 1 of the
     * character and output its index in the alphabet. */

    int permute(int p) {
        char character = _alphabet.toChar(wrap(p));
        char next = ' ';
        for (int i = 0; i < _cycles.length; i++) {
            for (int j = 0; j < _cycles[i].length(); j++) {
                if (_cycles[i].charAt(j) == character) {
                    next = _cycles[i].charAt((j + 1) % _cycles[i].length());
                    return _alphabet.toInt(next);
                }
            }
        }

        return p;
    }


    /** Return the result of applying the inverse of this permutation
     *  to  C modulo the alphabet size.
     * locating the character in the cycles,
     * after finding the character,
     * find the character that has index - 1 of the
     * character and output its index in the alphabet. */
    int invert(int c) {
        char character = _alphabet.toChar(wrap(c));
        char previous = '0';
        for (int i = 0; i < _cycles.length; i++) {
            for (int j = 0; j < _cycles[i].length(); j++) {
                if (_cycles[i].charAt(j) == character) {
                    previous = _cycles[i].charAt(
                            Math.floorMod((j - 1), _cycles[i].length()));
                    return _alphabet.toInt(previous);
                }
            }
        }
        return c;

    }



    /** Return the result of applying this permutation to the index of P
     *  in ALPHABET, and converting the result to a character of ALPHABET. */
    char permute(char p) {
        int charIndex = _alphabet.toInt(p);
        return _alphabet.toChar(permute(charIndex));
    }

    /** Return the result of applying the inverse of this permutation to C. */
    char invert(char c) {
        int charIndex = _alphabet.toInt(c);
        return _alphabet.toChar(invert(charIndex));
    }

    /** Return the alphabet used to initialize this Permutation. */
    Alphabet alphabet() {
        return _alphabet;
    }

    /** Return true iff this permutation is a derangement (i.e., a
     *  permutation for which no value maps to itself).
     * if the permutation is a derangement, then no cycle
     * should have length less or equals to 1, since cycle that has
     * length of 1 means that the character maps to itself. */

    boolean derangement() {
        for (int i = 0; i < _cycles.length; i++) {
            if (_cycles[i].length() <= 1) {
                return false;
            }
        }
        return true;
    }

    /** Alphabet of this permutation. */
    private Alphabet _alphabet;
}
