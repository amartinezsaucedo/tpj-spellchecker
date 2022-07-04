package edu.isistan.spellchecker.tokenizer;

import java.util.Iterator;
import java.io.IOException;

/**
 * Dado un archivo provee un método para recorrerlo.
 */
public class TokenScanner implements Iterator<String> {

  /**
   * Crea un TokenScanner.
   * <p>
   * Como un iterador, el TokenScanner solo debe leer lo justo y
   * necesario para implementar los métodos next() y hasNext(). 
   * No se debe leer toda la entrada de una.
   * <p>
   *
   * @param in fuente de entrada
   * @throws IOException si hay algún error leyendo.
   * @throws IllegalArgumentException si el Reader provisto es null
   */
  public TokenScanner(java.io.Reader in) throws IOException {
  
  }

  /**
   * Determina si un carácer es una caracter válido para una palabra.
   * <p>
   * Un caracter válido es una letra (
   * Character.isLetter) o una apostrofe '\''.
   *
   * @param c 
   * @return true si es un caracter
   */
  public static boolean isWordCharacter(int c) {
    return false;
  }


   /**
   * Determina si un string es una palabra válida.
   * Null no es una palabra válida.
   * Un string que todos sus caracteres son válidos es una 
   * palabra. Por lo tanto, el string vacío es una palabra válida.
   * @param s 
   * @return true si el string es una palabra.
   */
  public static boolean isWord(String s) {
		return false;
  }

  /**
   * Determina si hay otro token en el reader.
   */
  public boolean hasNext() {
    return false;
  }

  /**
   * Retorna el siguiente token.
   *
   * @throws NoSuchElementException cuando se alcanzó el final de stream
   */
  public String next() {
    return null;
  }

}
