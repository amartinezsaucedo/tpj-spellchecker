package edu.isistan.spellchecker.tokenizer;

import java.io.BufferedReader;
import java.util.*;
import java.io.IOException;

/**
 * Dado un archivo provee un m�todo para recorrerlo.
 */
public class TokenScanner implements Iterator<String> {
  private final BufferedReader reader;
  private final List<String> tokens;
  private static final int TOKENS_SIZE = 2000;

  /**
   * Crea un TokenScanner.
   * <p>
   * Como un iterador, el TokenScanner solo debe leer lo justo y
   * necesario para implementar los m�todos next() y hasNext(). 
   * No se debe leer toda la entrada de una.
   * <p>
   *
   * @param in fuente de entrada
   * @throws IOException si hay alg�n error leyendo.
   * @throws IllegalArgumentException si el Reader provisto es null
   */
  public TokenScanner(java.io.Reader in) throws IOException {
    if (in == null) {
      throw new IllegalArgumentException("El Reader provisto es null");
    }
    this.tokens = new LinkedList<>();
    this.reader = new BufferedReader(in);
    this.readInput();
  }

  /**
   * Lee BUFFER_SIZE caracteres del reader y se agregan los tokens
   * palabra y no-palabra
   * @throws IOException si hay un error al leer la entrada
   */
  private void readInput() throws IOException {
    int tokensAdded = 0;
    int character;
    StringBuilder wordToken = new StringBuilder();
    StringBuilder nonWordToken = new StringBuilder();
    while (tokensAdded <= TOKENS_SIZE && (character = this.reader.read()) != -1) {
      if (isWordCharacter(character)) {
        tokensAdded = checkTokenTypeChange(nonWordToken, this.tokens, tokensAdded);
        wordToken.append((char) character);
      } else {
        tokensAdded = checkTokenTypeChange(wordToken, this.tokens, tokensAdded);
        nonWordToken.append((char) character);
      }
    }
    checkUnprocessedToken(wordToken, this.reader, this.tokens, TokenScanner::isWordCharacter);
    checkUnprocessedToken(nonWordToken, this.reader, this.tokens, c -> !isWordCharacter(c));
  }

  private void checkUnprocessedToken(StringBuilder stringBuilder, BufferedReader reader, List<String> tokens, Condition condition) throws IOException {
    int character;
    if (!stringBuilder.isEmpty()) {
      boolean endToken = false;
      reader.mark(0);
      while (!endToken && (character = reader.read()) != -1) {
        if (condition.check(character)) {
          stringBuilder.append((char) character);
        } else {
          endToken = true;
          reader.reset();
        }
      }
      tokens.add(stringBuilder.toString());
    }
  }

  private int checkTokenTypeChange(StringBuilder stringBuilder, List<String> tokens, int tokensAdded) {
    if (!stringBuilder.isEmpty()) {
      tokens.add(stringBuilder.toString());
      stringBuilder.setLength(0);
      return tokensAdded + 1;
    }
    return tokensAdded;
  }

  private interface Condition {
    boolean check(int c);
  }

  /**
   * Determina si un car�cer es una caracter v�lido para una palabra.
   * <p>
   * Un caracter v�lido es una letra (
   * Character.isLetter) o una apostrofe '\''.
   *
   * @param c 
   * @return true si es un caracter
   */
  public static boolean isWordCharacter(int c) {
    return Character.isLetter(c) || c == '\'';
  }


   /**
   * Determina si un string es una palabra v�lida.
   * Null no es una palabra v�lida.
   * Un string que todos sus caracteres son v�lidos es una 
   * palabra. Por lo tanto, el string vac�o es una palabra v�lida.
   * @param s
   * @return true si el string es una palabra.
   */
  public static boolean isWord(String s) {
    if (s == null || s.length() == 0) {
      return false;
    }
    for (int index = 0; index < s.length(); index++) {
      if (!isWordCharacter(s.charAt(index))) {
        return false;
      }
    }
    return true;
  }

  /**
   * Determina si hay otro token en el reader.
   */
  public boolean hasNext() {
    this.checkRemainingLines();
    return !this.tokens.isEmpty();
  }

  private void checkRemainingLines() {
    if (this.tokens.isEmpty()) {
      try {
        this.readInput();
      } catch (IOException e) {
        throw new RuntimeException(e);
      }
    }
  }

  /**
   * Retorna el siguiente token.
   *
   * @throws NoSuchElementException cuando se alcanz� el final de stream
   */
  public String next() {
    if (!this.hasNext()) {
      throw new NoSuchElementException("Se alcanzo el final del stream");
    }
    return this.tokens.remove(0);
  }

}
