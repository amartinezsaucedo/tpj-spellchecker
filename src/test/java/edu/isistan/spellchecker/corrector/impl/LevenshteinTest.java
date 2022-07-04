package edu.isistan.spellchecker.corrector.impl;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import java.io.*;
import java.util.TreeSet;
import java.util.Set;



import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import edu.isistan.spellchecker.corrector.Dictionary;
import edu.isistan.spellchecker.corrector.impl.Levenshtein;
import edu.isistan.spellchecker.tokenizer.TokenScanner;

public class LevenshteinTest {
	private Levenshtein corr;


	private Set<String> makeSet(String[] strings) {
		Set<String> mySet = new TreeSet<String>();
		for (String s : strings) {
			mySet.add(s);
		}
		return mySet;
	}


	@Before public void setUp() throws IOException {
		Dictionary dict = new Dictionary(new TokenScanner(new FileReader("smallDictionary.txt")));
		corr = new Levenshtein(dict);
	}


	@After public void tearDown() {
		corr = null;
	}


	@Test public void testConstructorInvalid() throws IOException {
		try{
			new Levenshtein(null);
			fail("Expected an IllegalArgumentException - null dictionary.");
		}
		catch (IllegalArgumentException ex){
			//Do nothing - its supposed to throw an exception!
		}
	}


	@Test public void testDeletion() throws IOException {
		assertEquals("teh -> {eh,th,te}", makeSet(new String[]{"eh","th","te"}), corr.getDeletions("teh"));
	}


	@Test public void testInsert() throws IOException {
		assertEquals("ay -> {bay, cay, day, any, aye}",
				makeSet(new String[]{"bay", "cay", "day", "any", "aye"}),
				corr.getInsertions("ay"));
	}


	@Test public void testSubstitution() throws IOException {
		assertEquals("teh -> {heh, meh, tah, tea, tee, ten, tex}",
				makeSet(new String[]{"heh", "meh", "tah", "tea", "tee", "ten", "tex"}),
				corr.getSubstitutions("teh"));
	}


	@Test public void testCorrections() throws IOException {
		assertEquals("h -> {a, i, ah, eh, th}",
				makeSet(new String[]{"a", "i", "ah", "eh", "th"}),
				corr.getCorrections("h"));
	}


	@Test public void testCorrectionsCase() throws IOException {
		assertEquals("H -> {A, I, Ah, Eh, Th}",
				makeSet(new String[]{"A", "I", "Ah", "Eh", "Th"}),
				corr.getCorrections("H"));
	}


	@Test public void testNull() throws IOException {
		try {
			assertEquals(" null -> illegal argument", new TreeSet<String>(), 
					corr.getCorrections(null));
			fail("Should have thrown an illegal argument exception");
		} catch (IllegalArgumentException e) {
			// do nothing
		}
	}

}
