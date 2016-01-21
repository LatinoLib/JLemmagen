package org.latinolib.token;

import com.google.common.collect.Iterables;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import java.util.Iterator;
import java.util.NoSuchElementException;

import static org.junit.Assert.*;


/**
 * Author saxo
 */
public class SimpleTokenizerTest {

    @Test
    public void testEmpty() {
        String text = "";
        Token[] tokens = Iterables.toArray(new SimpleTokenizer().getTokens(text), Token.class);
        assertEquals(0, tokens.length);
    }

    @Test
    public void testNone() {
        String text = "   \t \n\r  ";
        Token[] tokens = Iterables.toArray(new SimpleTokenizer().getTokens(text), Token.class);
        assertEquals(0, tokens.length);
    }

    @Test
    public void testSingle() {
        String text = "Lorem";
        Token[] tokens = Iterables.toArray(new SimpleTokenizer().getTokens(text), Token.class);
        assertEquals(0, tokens[0].getStart());
        assertEquals(5, tokens[0].getEnd());
        assertEquals("Lorem", tokens[0].getText());
    }

    @Test
    public void testMany() {
        String text = "Lorem ipsum   dolor sit amet.";
        Token[] tokens = Iterables.toArray(new SimpleTokenizer().getTokens(text), Token.class);

        // first
        assertEquals(0, tokens[0].getStart());
        assertEquals(5, tokens[0].getEnd());
        assertEquals("Lorem", tokens[0].getText());

        // middle
        assertEquals(14, tokens[2].getStart());
        assertEquals(19, tokens[2].getEnd());
        assertEquals("dolor", tokens[2].getText());

        // last
        assertEquals(24, tokens[4].getStart());
        assertEquals(29, tokens[4].getEnd());
        assertEquals("amet.", tokens[4].getText());
    }

    @Rule
    public final ExpectedException exception = ExpectedException.none();

    @Test
    public void testIteratorCallSequence() {
        String text = "Lorem ipsum   dolor sit amet.";
        Iterator<Token> iter = new SimpleTokenizer().getTokens(text).iterator();

        iter.next();
        boolean b = iter.hasNext();
        assertEquals(true, b);
        b = iter.hasNext();
        assertEquals(true, b);
        iter.next();
        b = iter.hasNext();
        assertEquals(true, b);
        b = iter.hasNext();
        assertEquals(true, b);
        iter.next();
        iter.next();
        b = iter.hasNext();
        assertEquals(true, b);
        iter.next();
        b = iter.hasNext();
        assertEquals(false, b);
        b = iter.hasNext();
        assertEquals(false, b);
        b = iter.hasNext();
        assertEquals(false, b);

        exception.expect(NoSuchElementException.class);
        iter.next();
    }

}
