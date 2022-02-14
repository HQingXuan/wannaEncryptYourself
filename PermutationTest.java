package enigma;

import org.junit.Test;
import org.junit.Rule;
import org.junit.rules.Timeout;
import static org.junit.Assert.*;

import static enigma.TestUtils.*;

/**
 * The suite of all JUnit tests for the Permutation class. For the purposes of
 * this lab (in order to test) this is an abstract class, but in proj1, it will
 * be a concrete class. If you want to copy your tests for proj1, you can make
 * this class concrete by removing the 4 abstract keywords and implementing the
 * 3 abstract methods.
 *
 *  @ Haoqing Xuan
 */
public class PermutationTest {

    /**
     * For this lab, you must use this to get a new Permutation,
     * the equivalent to:
     * new Permutation(cycles, alphabet)
     * @return a Permutation with cycles as its cycles and alphabet as
     * its alphabet
     * @see Permutation for description of the Permutation conctructor
     */


    /** Testing time limit. */
    @Rule
    public Timeout globalTimeout = Timeout.seconds(5);

    /** Check that PERM has an ALPHABET whose size is that of
     *  FROMALPHA and TOALPHA and that maps each character of
     *  FROMALPHA to the corresponding character of FROMALPHA, and
     *  vice-versa. TESTID is used in error messages. */
    private void checkPerm(String testId,
                           String fromAlpha, String toAlpha,
                           Permutation perm, Alphabet alpha) {
        int N = fromAlpha.length();
        assertEquals(testId + " (wrong length)", N, perm.size());
        for (int i = 0; i < N; i += 1) {
            char c = fromAlpha.charAt(i), e = toAlpha.charAt(i);
            assertEquals(msg(testId, "wrong translation of '%c'", c),
                         e, perm.permute(c));
            assertEquals(msg(testId, "wrong inverse of '%c'", e),
                         c, perm.invert(e));
            int ci = alpha.toInt(c), ei = alpha.toInt(e);
            assertEquals(msg(testId, "wrong translation of %d", ci),
                         ei, perm.permute(ci));
            assertEquals(msg(testId, "wrong inverse of %d", ei),
                         ci, perm.invert(ei));
        }
    }

    /* ***** TESTS ***** */

    @Test
    public void checkIdTransform() {
        Alphabet alpha = new Alphabet();
        Permutation perm = new Permutation("", alpha);
        checkPerm("identity", UPPER_STRING, UPPER_STRING, perm, alpha);
    }


    @Test
    public void testInvertChar() {
        Permutation p = new Permutation("(AOQNGIH)(X)",
                new Alphabet("AGHINOQX"));
        assertEquals('G', p.invert('I'));
        assertEquals('A', p.invert('O'));
        assertEquals('H', p.invert('A'));
        assertEquals('O', p.invert('Q'));
        assertEquals('X', p.invert('X'));

    }
    @Test
    public void testSize() {
        Permutation p = new Permutation("(AOQNGIH)(X)",
                new Alphabet("AGHINOQX"));
        assertEquals(8, p.size());
    }
    @Test
    public void testPermuteChar() {
        Permutation p = new Permutation("(AOQNGIH)(X)",
                new Alphabet("AGHINOQX"));
        assertEquals('G', p.permute('N'));
        assertEquals('H', p.permute('I'));
        assertEquals('A', p.permute('H'));
        assertEquals('X', p.permute('X'));


    }
    @Test
    public void testInvertInt() {
        Permutation p = new Permutation("(AOQNGIH) (X)",
                new Alphabet("AGHINOQX"));
        assertEquals(7, p.invert(7));
        assertEquals(0, p.invert(5));
        assertEquals(2, p.invert(0));
    }
    @Test
    public void testPermuteInt() {
        Permutation p = new Permutation("(AOQNGIH) (X)",
                new Alphabet("AGHINOQX"));
        assertEquals(7, p.permute(7));
        assertEquals(5, p.permute(0));
        assertEquals(0, p.permute(2));
    }
    @Test
    public void testAlphabet() {
        Alphabet a = new Alphabet("AGHINOQX");
        Permutation p = new Permutation("(AOQNGIH) (X)", a);
        assertEquals(a, p.alphabet());
    }
    @Test
    public void testDerangement() {
        Permutation p = new Permutation("(AOQNGIH) (X)",
                new Alphabet("AGHINOQX"));
        assertFalse(p.derangement());
        Permutation a = new Permutation("(HAOQINGX)",
                new Alphabet("AGHINOQX"));
        assertTrue(a.derangement());
        Permutation b = new Permutation("()",
                new Alphabet("AGHINOQX"));
        assertTrue(b.derangement());
        Permutation c = new Permutation("(HAOQ)",
                new Alphabet("AGHINOQX"));
        assertTrue(c.derangement());

    }
    @Test(expected = EnigmaException.class)
    public void testNotInAlphabet() {
        Permutation p = new Permutation("(AOQNGIH) (X)",
                new Alphabet("AGHINOQX"));
        p.invert('B');
        p.permute('B');
    }







}
