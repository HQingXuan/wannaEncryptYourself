package enigma;
import static enigma.EnigmaException.*;

/** An alphabet of encodable characters.  Provides a mapping from characters
 *  to and from indices into the alphabet.
 *  @author Haoqing Xuan
 */
class Alphabet {
    /** variable to store characters in the alphabet. */
    private String characters;

    /** A new alphabet containing CHARS. The K-th character has index
     *  K (numbering from 0). No character may be duplicated. */
    Alphabet(String chars) {
        characters = chars;
    }

    /** A default alphabet of all upper-case characters. */
    Alphabet() {
        this("ABCDEFGHIJKLMNOPQRSTUVWXYZ");
    }

    /** Returns the size of the alphabet. */
    int size() {
        return characters.length();
    }

    /** Returns true if CH is in this alphabet. */
    boolean contains(char ch) {
        for (char i : characters.toCharArray()) {
            if (i == ch) {
                return true;
            }
        }
        return false;
    }

    /** Returns character number INDEX in the alphabet, where
     *  0 <= INDEX < size(). */
    char toChar(int index) {
        if (index < 0 || index > size()) {
            throw error("Index is out of range");
        }
        return characters.charAt(index);
    }

    /** Returns the index of character CH which must be in
     *  the alphabet. This is the inverse of toChar(). */
    int toInt(char ch) {
        for (int i = 0; i < size(); i++) {
            if ((int) characters.charAt(i) == (int) ch) {
                return i;
            }
        }
        return -1;
    }

}
