package edu.isistan.spellchecker;

import edu.isistan.spellchecker.corrector.Corrector;
import edu.isistan.spellchecker.corrector.Dictionary;
import edu.isistan.spellchecker.corrector.DictionaryTrie;
import edu.isistan.spellchecker.corrector.impl.FileCorrector;
import edu.isistan.spellchecker.corrector.impl.SwapCorrector;
import edu.isistan.spellchecker.tokenizer.TokenScanner;
import org.junit.*;
import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.BenchmarkMode;
import org.openjdk.jmh.annotations.Mode;
import org.openjdk.jmh.annotations.OutputTimeUnit;
import org.openjdk.jmh.infra.Blackhole;

import java.io.*;
import java.util.Arrays;
import java.util.Set;
import java.util.TreeSet;
import java.util.concurrent.TimeUnit;

import static org.junit.Assert.*;

/**
 * Cree sus propios tests.
 */
public class MyTests {
    @Test
    public void emptyInput() throws IOException {
        try {
            Reader reader = null;
            new TokenScanner(reader);
            fail("Expected IllegalArgumentException - null reader");
        } catch (IllegalArgumentException e) {
            //Do nothing - it's supposed to throw this
        }
    }

    @Test
    public void oneTokenIsWord() throws IOException {
        Reader in = new StringReader("Hello");
        try (in) {
            TokenScanner d = new TokenScanner(in);
            assertTrue("has next", d.hasNext());
            String token = d.next();
            assertEquals("Hello", token);
            assertTrue(TokenScanner.isWord(token));
            assertFalse("reached end of stream", d.hasNext());
        }
        Reader in2 = new StringReader("Hello!!!\n123456");
        try (in2) {
            TokenScanner d2 = new TokenScanner(in2);
            assertTrue("has next", d2.hasNext());
            String token = d2.next();
            assertEquals("Hello", token);
            assertTrue(TokenScanner.isWord(token));
            assertTrue(d2.hasNext());
            token = d2.next();
            assertEquals("!!!\n123456", token);
            assertFalse(TokenScanner.isWord(token));
            assertFalse("reached end of stream", d2.hasNext());
        }
    }

    @Test
    public void oneTokenIsNotWord() throws IOException {
        Reader in = new StringReader("\n");
        try (in) {
            TokenScanner d = new TokenScanner(in);
            assertTrue("has next", d.hasNext());
            String token = d.next();
            assertEquals("\n", token);
            assertFalse(TokenScanner.isWord(token));
            assertFalse("reached end of stream", d.hasNext());
        }
        Reader in2 = new StringReader("Hello!!!\n123456\nWorld");
        try (in2) {
            TokenScanner d2 = new TokenScanner(in2);
            assertTrue("has next", d2.hasNext());
            String token = d2.next();
            assertEquals("Hello", token);
            assertTrue(TokenScanner.isWord(token));
            assertTrue(d2.hasNext());
            token = d2.next();
            assertEquals("!!!\n123456\n", token);
            assertFalse(TokenScanner.isWord(token));
            token = d2.next();
            assertEquals("World", token);
            assertTrue(TokenScanner.isWord(token));
            assertFalse("reached end of stream", d2.hasNext());
        }
    }

    @Test
    public void twoTokensEndsWithWord() throws IOException {
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

    @Test
    public void twoTokensDoesNotEndWithWord() throws IOException {
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

    @Test
    public void getTokens() throws IOException {
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

    @Test(timeout = 500)
    public void testDictionaryContainsWord() throws IOException {
        Dictionary d = new Dictionary(new TokenScanner(new FileReader("smallDictionary.txt")));
        assertTrue("'Heh' -> should be true ('heh' in file)", d.isWord("Heh"));
    }

    @Test(timeout = 500)
    public void testDictionaryDoesNotContainWord() throws IOException {
        Dictionary d = new Dictionary(new TokenScanner(new FileReader("smallDictionary.txt")));
        assertFalse("'Ana' -> should be false ('ana' not in file)", d.isWord("ana"));
    }

    @Test(timeout = 500)
    public void testDictionaryWordCount() throws IOException {
        Dictionary d = new Dictionary(new TokenScanner(new FileReader("smallDictionary.txt")));
        assertEquals("'Dictionary word length should be 32", 32, d.getNumWords());
    }

    @Test(timeout = 500)
    public void testEmptyStringIsNotWord() throws IOException {
        Dictionary d = new Dictionary(new TokenScanner(new FileReader("smallDictionary.txt")));
        assertFalse("'' -> should be false", d.isWord(""));
    }

    @Test(timeout = 500)
    public void testSameWordDifferentCaseExistsInDictionary() throws IOException {
        Dictionary d = new Dictionary(new TokenScanner(new FileReader("smallDictionary.txt")));
        assertTrue("'Apple' -> should be true ('apple' in file)", d.isWord("Apple"));
        assertTrue("'APPLE' -> should be true ('apple' in file)", d.isWord("APPLE"));
        assertTrue("'BananA' -> should be true ('banana' in file)", d.isWord("BananA"));
    }

    private Set<String> makeSet(String[] strings) {
        return new TreeSet<>(Arrays.asList(strings));
    }

    @Test
    public void testFileCorrectorGetCorrectionsExtraSpaces() throws IOException, FileCorrector.FormatException {
        Corrector c = FileCorrector.make("smallMisspellingsSpaces.txt");
        assertEquals("lyon -> lion", makeSet(new String[]{"lion"}), c.getCorrections("lyon"));
        assertEquals("TIGGER -> {Trigger,Tiger}", makeSet(new String[]{"Trigger", "Tiger"}), c.getCorrections("TIGGER"));
    }

    @Test
    public void testFileCorrectorGetCorrectionsEmpty() throws IOException, FileCorrector.FormatException {
        Corrector c = FileCorrector.make("smallMisspellings.txt");
        assertEquals("banana -> {}", makeSet(new String[]{}), c.getCorrections("banana"));
    }

    @Test
    public void testFileCorrectorGetMultipleCorrections() throws IOException, FileCorrector.FormatException {
        Corrector c = FileCorrector.make("smallMisspellingsSpaces.txt");
        assertEquals("ho -> {hoy, hora, hola}", makeSet(new String[]{"hoy", "hora", "hola"}), c.getCorrections("ho"));
    }

    @Test
    public void testFileCorrectorGetCorrectionsMultipleCases() throws IOException, FileCorrector.FormatException {
        Corrector c = FileCorrector.make("smallMisspellingsSpaces.txt");
        assertEquals("palaBar -> {palabra}", makeSet(new String[]{"palabra"}), c.getCorrections("palaBar"));
        assertEquals("PaLaBar -> {Palabra}", makeSet(new String[]{"Palabra"}), c.getCorrections("PaLaBar"));
        assertEquals("PALABAR -> {Palabra}", makeSet(new String[]{"Palabra"}), c.getCorrections("PALABAR"));
        assertEquals("palabra -> {}", makeSet(new String[]{}), c.getCorrections("palabra"));
    }

    @Test
    public void testSwapCorrectionNullDictionary() {
        try {
            new SwapCorrector(null);
            fail("Expected an IllegalArgumentException - cannot create SwapCorrector with null.");
        } catch (IllegalArgumentException f) {
            //Do nothing. It's supposed to throw an exception
        }
    }

    @Test
    public void testSwapCorrections() throws IOException {
        try (Reader reader = new FileReader("smallDictionary.txt")) {
            Dictionary d = new Dictionary(new TokenScanner(reader));
            SwapCorrector swap = new SwapCorrector(d);
            assertEquals("crarot -> {carrot}", makeSet(new String[]{"carrot"}), swap.getCorrections("crarot"));
            assertEquals("acrrot -> {carrot}", makeSet(new String[]{"carrot"}), swap.getCorrections("acrrot"));
            assertEquals("carrto -> {carrot}", makeSet(new String[]{"carrot"}), swap.getCorrections("carrto"));
        }
    }

    @Test
    public void testSwapCorrectionCapitalization() throws IOException {
        try (Reader reader = new FileReader("smallDictionary.txt")) {
            Dictionary d = new Dictionary(new TokenScanner(reader));
            SwapCorrector swap = new SwapCorrector(d);
            assertEquals("paple -> {apple}", makeSet(new String[]{"apple"}), swap.getCorrections("paple"));
            assertEquals("Paple -> {Apple}", makeSet(new String[]{"Apple"}), swap.getCorrections("Paple"));
        }
    }

    @Test(timeout = 500)
    public void testDictionaryTrieContainsSimple() throws IOException {
        DictionaryTrie d = new DictionaryTrie(new TokenScanner(new FileReader("smallDictionary.txt")));
        assertTrue("'apple' -> should be true ('apple' in file)", d.isWord("apple"));
        assertTrue("'Banana' -> should be true ('banana' in file)", d.isWord("Banana"));
        assertFalse("'pineapple' -> should be false", d.isWord("pineapple"));
    }


    @Test(timeout = 500)
    public void testDictionaryTrieContainsApostrophe() throws IOException {
        DictionaryTrie d = new DictionaryTrie(new TokenScanner(new FileReader("smallDictionary.txt")));
        assertTrue("'it's' -> should be true ('it's' in file)", d.isWord("it's"));
    }


    @Test(timeout = 500)
    public void testDictionaryTrieConstructorInvalidTokenScanner() {
        try {
            TokenScanner ts = null;
            new DictionaryTrie(ts);
            fail("Expected IllegalArgumentException - null TokenScanner");
        } catch (IllegalArgumentException e) {
            //Do nothing - it's supposed to throw this
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Test(timeout = 500)
    public void testDictionaryTrieContainsWord() throws IOException {
        DictionaryTrie d = new DictionaryTrie(new TokenScanner(new FileReader("smallDictionary.txt")));
        assertTrue("'apple' -> should be true ('heh' in file)", d.isWord("heh"));
    }

    @Test(timeout = 500)
    public void testDictionaryTrieDoesNotContainWord() throws IOException {
        DictionaryTrie d = new DictionaryTrie(new TokenScanner(new FileReader("smallDictionary.txt")));
        assertFalse("'apple' -> should be false ('ana' not in file)", d.isWord("ana"));
    }

    @Test(timeout = 500)
    public void testDictionaryTrieWordCount() throws IOException {
        DictionaryTrie d = new DictionaryTrie(new TokenScanner(new FileReader("smallDictionary.txt")));
        assertEquals("'Dictionary word length should be 32", 32, d.getNumWords());
    }

    @Test(timeout = 500)
    public void testEmptyStringIsNotWordDictionaryTrie() throws IOException {
        DictionaryTrie d = new DictionaryTrie(new TokenScanner(new FileReader("smallDictionary.txt")));
        assertFalse("'' -> should be false", d.isWord(""));
    }

    @Test(timeout = 500)
    public void testSameWordDifferentCaseExistsInDictionaryTrie() throws IOException {
        DictionaryTrie d = new DictionaryTrie(new TokenScanner(new FileReader("smallDictionary.txt")));
        assertTrue("'Apple' -> should be true ('apple' in file)", d.isWord("Apple"));
        assertTrue("'APPLE' -> should be true ('apple' in file)", d.isWord("APPLE"));
        assertTrue("'BananA' -> should be true ('banana' in file)", d.isWord("BananA"));
    }

    public static void spellCheckFilesTrie(String fdict, int dictSize, String fcorr,
                                           String fdoc, String fout, String finput)
            throws IOException, FileCorrector.FormatException {
        Dictionary dict = DictionaryTrie.make(fdict);
        Corrector corr;
        if (fcorr == null) {
            corr = new SwapCorrector(dict);
        } else {
            corr = FileCorrector.make(fcorr);
        }
        if (dictSize >= 0)
            assertEquals("Dictionary size = " + dictSize, dictSize,
                    dict.getNumWords());

        FileInputStream input = new FileInputStream(finput);
        Reader in = new BufferedReader(new FileReader(fdoc));
        Writer out = new BufferedWriter(new FileWriter(fout));
        SpellChecker sc = new SpellChecker(corr, dict);
        sc.checkDocument(in, input, out);
        in.close();
        input.close();
        out.flush();
        out.close();
    }

    @Test(timeout = 500)
    public void testCheckFoxGoodTrie() throws IOException, FileCorrector.FormatException {
        spellCheckFilesTrie("theFoxDictionary.txt", 7, "theFoxMisspellings.txt",
                "theFox.txt", "foxout.txt", "theFox_goodinput.txt");
        compareDocs("foxout.txt", "theFox_expected_output.txt");
    }

    @Test(timeout = 500)
    public void testCheckMeanInputTrie() throws IOException, FileCorrector.FormatException {
        spellCheckFilesTrie("theFoxDictionary.txt", 7, "theFoxMisspellings.txt",
                "theFox.txt", "foxout.txt", "theFox_meaninput.txt");
        compareDocs("foxout.txt", "theFox_expected_output.txt");
    }

    @Test(timeout = 500)
    public Object testCheckGettysburgSwapTrie() throws IOException, FileCorrector.FormatException {
        // Use the SwapCorrector instead!
        spellCheckFilesTrie("dictionary.txt", 60822, null,
                "Gettysburg.txt", "Gettysburg-out.txt",
                "Gettysburg_input.txt");
        compareDocs("Gettysburg-out.txt", "Gettysburg_expected_output.txt");
        return new Object();
    }


    public static void compareDocs(String out, String expected)
            throws IOException {
        try (BufferedReader f1 = new BufferedReader(new FileReader(out)); BufferedReader f2 = new BufferedReader(new FileReader(expected))) {
            String line1 = f1.readLine();
            String line2 = f2.readLine();
            while (line1 != null && line2 != null) {
                assertEquals("Output file did not match expected output.", line2, line1);
                line1 = f1.readLine();
                line2 = f2.readLine();
            }
            if (line1 != null) {
                fail("Expected end of file, but found extra lines in the output.");
            } else if (line2 != null) {
                fail("Expected more lines, but found end of file in the output. ");
            }
        }
    }
    @Benchmark
    @BenchmarkMode(Mode.All)
    @OutputTimeUnit(TimeUnit.MILLISECONDS)
    public void measureName(Blackhole bh) throws IOException, FileCorrector.FormatException {
        bh.consume(testCheckGettysburgSwapTrie());
    }
}