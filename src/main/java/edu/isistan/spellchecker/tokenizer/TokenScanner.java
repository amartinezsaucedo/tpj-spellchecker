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
  private static final int BUFFER_SIZE = 2048;

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
    char[] buffer = new char[BUFFER_SIZE];
    int result = this.reader.read(buffer, 0, buffer.length);
    int leftIndex = 0;
    int rightIndex;
    StringBuilder endOfWordBuilder = new StringBuilder();
    String endOfWordSeparator = "";
    boolean allCharsRead = result != -1;
    if (allCharsRead) { // Como leímos todos los caracteres debemos seguir hasta encontrar un separador
      int character;
      while ((character = this.reader.read()) != -1 && isWordCharacter(character)) {
        endOfWordBuilder.append((char) character);
      }
      endOfWordSeparator = endOfWordSeparator.concat(String.valueOf((char)character));
    }
    String endOfWord = endOfWordBuilder.toString();
    while (leftIndex < BUFFER_SIZE && buffer[leftIndex] != Character.MIN_VALUE) {
      rightIndex = leftIndex;
      while (rightIndex < BUFFER_SIZE && isWordCharacter(buffer[rightIndex])) { // Buscamos posición donde termina la palabra
        rightIndex++;
      }
      String candidateWord;
      if (leftIndex == rightIndex) { // Único carácter
        candidateWord = String.valueOf(buffer[leftIndex]);
        if (isWord(candidateWord) || this.tokens.isEmpty()) { // Si es palabra, la agregamos
          this.tokens.add(candidateWord);
        } else { // ... si no, lo concatenamos a la última palabra agregada (saltos de línea)
          String lastWord = this.tokens.remove(this.tokens.size() - 1);
          this.tokens.add(lastWord.concat(candidateWord));
        }
      } else { // Encontramos palabra
        candidateWord = String.valueOf(Arrays.copyOfRange(buffer, leftIndex, rightIndex));
        this.tokens.add(candidateWord);
        if (rightIndex < BUFFER_SIZE) { // Agregamos el token no-palabra al final
          this.tokens.add(String.valueOf(buffer[rightIndex]));
        }
      }
      leftIndex = rightIndex + 1;
    }
    // Terminamos de procesar el buffer, falta definir qué hacer con el último token agregado
    if (!this.tokens.isEmpty()) {
      String lastWord = this.tokens.remove(this.tokens.size() - 1);
      if (isWord(lastWord) && isWord(endOfWord)) { // Por el tamaño del buffer una palabra se cortó, concatenamos las partes
        this.tokens.add(lastWord.concat(endOfWord));
        this.tokens.add(endOfWordSeparator);
      } else { // Alguna de las dos partes no es una palabra, volvemos a agregar la última
        if (lastWord.charAt(0) != Character.MIN_VALUE) {
          this.tokens.add(lastWord);
        }
        if (isWord(endOfWord)) { // lastWord es un token no-palabra, y endOfWord es palabra. Lo agregamos también
          this.tokens.add(endOfWord);
        }
        if (endOfWordSeparator.charAt(0) != Character.MAX_VALUE) { //Si el separador de la última palabra no es el fin del archivo, lo agregamos también
          this.tokens.add(endOfWordSeparator);
        }
      }
    }
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
