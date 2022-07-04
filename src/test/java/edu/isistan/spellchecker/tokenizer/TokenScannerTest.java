package edu.isistan.spellchecker.tokenizer;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import java.util.NoSuchElementException;



import org.junit.Test;

import edu.isistan.spellchecker.tokenizer.TokenScanner;

public class TokenScannerTest {

  
  @Test public void testIsWordCharacter() {
    assertTrue(TokenScanner.isWordCharacter('\''));
    assertTrue(TokenScanner.isWordCharacter('a'));
    assertFalse(TokenScanner.isWordCharacter(' '));
    assertFalse(TokenScanner.isWordCharacter('\n'));
  }

  
  @Test public void testIsWord() {
      assertTrue(TokenScanner.isWord("a"));
      assertTrue(TokenScanner.isWord("A"));
		assertTrue(TokenScanner.isWord("true"));
		assertTrue(TokenScanner.isWord("CIS'S"));
		assertFalse(TokenScanner.isWord(""));
		assertFalse(TokenScanner.isWord(null));
  	   assertFalse(TokenScanner.isWord("1"));
  	   assertFalse(TokenScanner.isWord("a1"));
  	   assertFalse(TokenScanner.isWord("123"));
  	   assertFalse(TokenScanner.isWord("123"));
  	   assertFalse(TokenScanner.isWord(" a"));
  }		  

  
  @Test public void testGetNextTokenWord() throws IOException {
	 Reader in = new StringReader("Aren't you \ntired"); 
    TokenScanner d = new TokenScanner(in);
    try {
      assertTrue("has next", d.hasNext());
      assertEquals("Aren't", d.next());

      assertTrue("has next", d.hasNext());
      assertEquals(" ", d.next());

      assertTrue("has next", d.hasNext());
      assertEquals("you", d.next());

      assertTrue("has next", d.hasNext());
      assertEquals(" \n", d.next());

      assertTrue("has next", d.hasNext());
      assertEquals("tired", d.next());

      assertFalse("reached end of stream", d.hasNext());
    } finally {
		  in.close();
    }
  }

}
