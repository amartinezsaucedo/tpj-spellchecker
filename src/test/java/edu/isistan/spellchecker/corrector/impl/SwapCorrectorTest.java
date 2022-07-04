package edu.isistan.spellchecker.corrector.impl;
import static org.junit.Assert.*;
import org.junit.*;

import edu.isistan.spellchecker.corrector.Dictionary;
import edu.isistan.spellchecker.corrector.impl.SwapCorrector;
import edu.isistan.spellchecker.tokenizer.TokenScanner;

import java.io.*;
import java.util.TreeSet;
import java.util.Set;



public class SwapCorrectorTest {


	private Set<String> makeSet(String[] strings) {
		Set<String> mySet = new TreeSet<String>();
		for (String s : strings) {
			mySet.add(s);
		}
		return mySet;
	}


	@Test public void testSwapCorrections() throws IOException {
		Reader reader = new FileReader("smallDictionary.txt");
		try {
			Dictionary d = new Dictionary(new TokenScanner(reader));
			SwapCorrector swap = new SwapCorrector(d);
			assertEquals("cya -> {cay}", makeSet(new String[]{"cay"}), swap.getCorrections("cya"));
			assertEquals("oYurs -> {yours}", makeSet(new String[]{"yours"}), swap.getCorrections("oYurs"));
		} finally {
			reader.close();
		}
	}

}
