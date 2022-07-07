package edu.isistan.spellchecker;
import edu.isistan.spellchecker.corrector.Corrector;
import edu.isistan.spellchecker.corrector.Dictionary;
import edu.isistan.spellchecker.corrector.DictionaryTrie;
import edu.isistan.spellchecker.corrector.impl.FileCorrector;
import edu.isistan.spellchecker.corrector.impl.SwapCorrector;
import edu.isistan.spellchecker.tokenizer.TokenScanner;
import org.junit.*;

import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import java.util.Arrays;
import java.util.Set;
import java.util.TreeSet;

import static org.junit.Assert.*;

/** Cree sus propios tests. */
public class MyTests {
    @Test public void emptyInput() throws IOException {
        try {
            Reader reader = null;
            new TokenScanner(reader);
            fail("Expected IllegalArgumentException - null reader");
        } catch (IllegalArgumentException e){
            //Do nothing - it's supposed to throw this
        }
    }

    @Test public void oneTokenIsWord() throws IOException {
        Reader in = new StringReader("Hello");
        try (in) {
            TokenScanner d = new TokenScanner(in);
            assertTrue("has next", d.hasNext());
            String token = d.next();
            assertEquals("Hello", token);
            assertTrue(TokenScanner.isWord(token));
            assertFalse("reached end of stream", d.hasNext());
        }
    }

    @Test public void oneTokenIsNotWord() throws IOException {
        Reader in = new StringReader("\n");
        try (in) {
            TokenScanner d = new TokenScanner(in);
            assertTrue("has next", d.hasNext());
            String token = d.next();
            assertEquals("\n", token);
            assertFalse(TokenScanner.isWord(token));
            assertFalse("reached end of stream", d.hasNext());
        }
    }

    @Test public void twoTokensEndsWithWord() throws IOException {
        Reader in = new StringReader("!Hello");
        try (in) {
            TokenScanner d = new TokenScanner(in);
            assertTrue("has next", d.hasNext());
            String token = d.next();
            assertEquals("!", token);
            assertFalse(TokenScanner.isWord(token));
            assertTrue("has next", d.hasNext());
            token = d.next();
            assertEquals("Hello", token);
            assertTrue(TokenScanner.isWord(token));
            assertFalse("reached end of stream", d.hasNext());
        }
    }

    @Test public void twoTokensDoesNotEndWithWord() throws IOException {
        Reader in = new StringReader("Hello\n");
        try (in) {
            TokenScanner d = new TokenScanner(in);
            assertTrue("has next", d.hasNext());
            String token = d.next();
            assertEquals("Hello", token);
            assertTrue(TokenScanner.isWord(token));
            assertTrue("has next", d.hasNext());
            token = d.next();
            assertEquals("\n", token);
            assertFalse(TokenScanner.isWord(token));
            assertFalse("reached end of stream", d.hasNext());
        }
    }

    @Test public void getTokens() throws IOException {
        Reader in = new StringReader("It's time\n2 e-mail!");
        try (in) {
            TokenScanner d = new TokenScanner(in);
            assertTrue("has next", d.hasNext());
            assertEquals("It's", d.next());
            assertTrue("has next", d.hasNext());
            assertEquals(" ", d.next());
            assertTrue("has next", d.hasNext());
            assertEquals("time", d.next());
            assertTrue("has next", d.hasNext());
            assertEquals("\n2 ", d.next());
            assertTrue("has next", d.hasNext());
            assertEquals("e", d.next());
            assertTrue("has next", d.hasNext());
            assertEquals("-", d.next());
            assertTrue("has next", d.hasNext());
            assertEquals("mail", d.next());
            assertTrue("has next", d.hasNext());
            assertEquals("!", d.next());
            assertFalse("reached end of stream", d.hasNext());
        }
    }

    @Test(timeout=500) public void testDictionaryContainsWord() throws IOException {
        Dictionary d = new Dictionary(new TokenScanner(new FileReader("smallDictionary.txt")));
        assertTrue("'apple' -> should be true ('heh' in file)", d.isWord("heh"));
    }

    @Test(timeout=500) public void testDictionaryDoesNotContainWord() throws IOException {
        Dictionary d = new Dictionary(new TokenScanner(new FileReader("smallDictionary.txt")));
        assertFalse("'apple' -> should be false ('ana' not in file)", d.isWord("ana"));
    }

    @Test(timeout=500) public void testDictionaryWordCount() throws IOException {
        Dictionary d = new Dictionary(new TokenScanner(new FileReader("smallDictionary.txt")));
        assertEquals("'Dictionary word length should be 32",32, d.getNumWords());
    }

    @Test(timeout=500) public void testEmptyStringIsNotWord() throws IOException {
        Dictionary d = new Dictionary(new TokenScanner(new FileReader("smallDictionary.txt")));
        assertFalse("'' -> should be false", d.isWord(""));
    }

    @Test(timeout=500) public void testSameWordDifferentCaseExistsInDictionary() throws IOException {
        Dictionary d = new Dictionary(new TokenScanner(new FileReader("smallDictionary.txt")));
        assertTrue("'Apple' -> should be true ('apple' in file)", d.isWord("Apple"));
        assertTrue("'APPLE' -> should be true ('apple' in file)", d.isWord("APPLE"));
        assertTrue("'BananA' -> should be true ('banana' in file)", d.isWord("BananA"));
    }

    private Set<String> makeSet(String[] strings) {
        return new TreeSet<>(Arrays.asList(strings));
    }

    @Test public void testFileCorrectorGetCorrectionsExtraSpaces() throws IOException, FileCorrector.FormatException  {
        Corrector c = FileCorrector.make("smallMisspellingsSpaces.txt");
        assertEquals("lyon -> lion", makeSet(new String[]{"lion"}), c.getCorrections("lyon"));
        assertEquals("TIGGER -> {Trigger,Tiger}", makeSet(new String[]{"Trigger","Tiger"}), c.getCorrections("TIGGER"));
    }

    @Test public void testFileCorrectorGetCorrectionsEmpty() throws IOException, FileCorrector.FormatException  {
        Corrector c = FileCorrector.make("smallMisspellings.txt");
        assertEquals("banana -> {}", makeSet(new String[]{}), c.getCorrections("banana"));
    }

    @Test public void testFileCorrectorGetMultipleCorrections() throws IOException, FileCorrector.FormatException  {
        Corrector c = FileCorrector.make("smallMisspellingsSpaces.txt");
        assertEquals("ho -> {hoy, hora, hola}", makeSet(new String[]{"hoy", "hora", "hola"}), c.getCorrections("ho"));
    }

    @Test public void testFileCorrectorGetCorrectionsMultipleCases() throws IOException, FileCorrector.FormatException  {
        Corrector c = FileCorrector.make("smallMisspellingsSpaces.txt");
        assertEquals("palaBar -> {palabra}", makeSet(new String[]{"palabra"}), c.getCorrections("palaBar"));
        assertEquals("PaLaBar -> {Palabra}", makeSet(new String[]{"Palabra"}), c.getCorrections("PaLaBar"));
        assertEquals("PALABAR -> {Palabra}", makeSet(new String[]{"Palabra"}), c.getCorrections("PALABAR"));
        assertEquals("palabra -> {}", makeSet(new String[]{}), c.getCorrections("palabra"));
    }

    @Test public void testSwapCorrectionNullDictionary()  {
        try {
            new SwapCorrector(null);
            fail("Expected an IllegalArgumentException - cannot create SwapCorrector with null.");
        } catch (IllegalArgumentException f) {
            //Do nothing. It's supposed to throw an exception
        }
    }

    @Test public void testSwapCorrections() throws IOException {
        try (Reader reader = new FileReader("smallDictionary.txt")) {
            Dictionary d = new Dictionary(new TokenScanner(reader));
            SwapCorrector swap = new SwapCorrector(d);
            assertEquals("crarot -> {carrot}", makeSet(new String[]{"carrot"}), swap.getCorrections("crarot"));
            assertEquals("acrrot -> {carrot}", makeSet(new String[]{"carrot"}), swap.getCorrections("acrrot"));
            assertEquals("carrto -> {carrot}", makeSet(new String[]{"carrot"}), swap.getCorrections("carrto"));
        }
    }

    @Test public void testSwapCorrectionCapitalization() throws IOException {
        try (Reader reader = new FileReader("smallDictionary.txt")) {
            Dictionary d = new Dictionary(new TokenScanner(reader));
            SwapCorrector swap = new SwapCorrector(d);
            assertEquals("paple -> {apple}", makeSet(new String[]{"apple"}), swap.getCorrections("paple"));
            assertEquals("Paple -> {Apple}", makeSet(new String[]{"Apple"}), swap.getCorrections("Paple"));
        }
    }

    @Test(timeout=500) public void testDictionaryTrieContainsSimple() throws IOException {
        DictionaryTrie d = new DictionaryTrie(new TokenScanner(new FileReader("smallDictionary.txt")));
        assertTrue("'apple' -> should be true ('apple' in file)", d.isWord("apple"));
        assertTrue("'Banana' -> should be true ('banana' in file)", d.isWord("Banana"));
        assertFalse("'pineapple' -> should be false", d.isWord("pineapple"));
    }


    @Test(timeout=500) public void testDictionaryTrieContainsApostrophe() throws IOException {
        DictionaryTrie d = new DictionaryTrie(new TokenScanner(new FileReader("smallDictionary.txt")));
        assertTrue("'it's' -> should be true ('it's' in file)", d.isWord("it's"));
    }


    @Test(timeout=500) public void testDictionaryTrieConstructorInvalidTokenScanner(){
        try {
            TokenScanner ts = null;
            new DictionaryTrie(ts);
            fail("Expected IllegalArgumentException - null TokenScanner");
        } catch (IllegalArgumentException e){
            //Do nothing - it's supposed to throw this
        }
    }

    @Test(timeout=500) public void testDictionaryTrieContainsWord() throws IOException {
        DictionaryTrie d = new DictionaryTrie(new TokenScanner(new FileReader("smallDictionary.txt")));
        assertTrue("'apple' -> should be true ('heh' in file)", d.isWord("heh"));
    }

    @Test(timeout=500) public void testDictionaryTrieDoesNotContainWord() throws IOException {
        DictionaryTrie d = new DictionaryTrie(new TokenScanner(new FileReader("smallDictionary.txt")));
        assertFalse("'apple' -> should be false ('ana' not in file)", d.isWord("ana"));
    }

    @Test(timeout=500) public void testDictionaryTrieWordCount() throws IOException {
        DictionaryTrie d = new DictionaryTrie(new TokenScanner(new FileReader("smallDictionary.txt")));
        assertEquals("'Dictionary word length should be 32",32, d.getNumWords());
    }

    @Test(timeout=500) public void testEmptyStringIsNotWordDictionaryTrie() throws IOException {
        DictionaryTrie d = new DictionaryTrie(new TokenScanner(new FileReader("smallDictionary.txt")));
        assertFalse("'' -> should be false", d.isWord(""));
    }

    @Test(timeout=500) public void testSameWordDifferentCaseExistsInDictionaryTrie() throws IOException {
        DictionaryTrie d = new DictionaryTrie(new TokenScanner(new FileReader("smallDictionary.txt")));
        assertTrue("'Apple' -> should be true ('apple' in file)", d.isWord("Apple"));
        assertTrue("'APPLE' -> should be true ('apple' in file)", d.isWord("APPLE"));
        assertTrue("'BananA' -> should be true ('banana' in file)", d.isWord("BananA"));
    }
}