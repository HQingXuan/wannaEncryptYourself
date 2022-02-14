package enigma;

import java.util.HashSet;

import static enigma.EnigmaException.*;

/** Class that represents a rotating rotor in the enigma machine.
 *  @author Haoqing Xuan
 */
class MovingRotor extends Rotor {
    /** Hashset variable to track info of notches.*/
    private HashSet<Integer> _notches;
    /** A rotor named NAME whose permutation in its default setting is
     *  PERM, and whose notches are at the positions indicated in NOTCHES.
     *  The Rotor is initally in its 0 setting (first character of its
     *  alphabet).
     */
    MovingRotor(String name, Permutation perm, String notches) {
        super(name, perm);
        _notches = new HashSet<Integer>();
        if (notches.length() < 0) {
            throw error("Moving rotor needs to have notch");
        }
        if (notches.length() > 0) {
            for (int i = 0; i < notches.length(); i++) {
                _notches.add(permutation().alphabet().toInt(notches.charAt(i)));
            }
        }
    }
    @Override
    boolean atNotch() {
        return _notches.contains(permutation().wrap(_setting));
    }

    @Override
    void advance() {
        set(setting() + 1);
    }


    @Override
    boolean rotates() {
        return true;
    }

    @Override
    public String toString() {
        return "MovingRotor " + name();
    }
}
