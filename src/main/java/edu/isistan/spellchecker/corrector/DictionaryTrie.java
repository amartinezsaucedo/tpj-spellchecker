package edu.isistan.spellchecker.corrector;

import edu.isistan.spellchecker.tokenizer.TokenScanner;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;

/**
 * El diccionario maneja todas las palabras conocidas.
 * El diccionario es case insensitive 
 * 
 * Una palabra "v�lida" es una secuencia de letras (determinado por Character.isLetter) 
 * o apostrofes.
 */
public class DictionaryTrie extends Dictionary{
	private Trie dictionary;
	/**
	 * Construye un diccionario usando un TokenScanner
	 * <p>
	 * Una palabra v�lida es una secuencia de letras (ver Character.isLetter) o apostrofes.
	 * Toda palabra no v�lida se debe ignorar
	 *
	 * <p>
	 *
	 * @param ts
	 * @throws IOException Error leyendo el archivo
	 * @throws IllegalArgumentException el TokenScanner es null
	 */
	public DictionaryTrie(TokenScanner ts) throws IOException {
		super(ts);
	}

	@Override
	protected void doInitializeDictionary(TokenScanner tokenScanner) {
		this.dictionary = new Trie();
		while (tokenScanner.hasNext()) {
			String token = tokenScanner.next().toLowerCase();
			if (TokenScanner.isWord(token)) {
				this.insertInLSH(token);
				this.dictionary.addWord(token);
			}
		}
	}

	/**
	 * Construye un diccionario usando un archivo.
	 *
	 *
	 * @param filename 
	 * @throws FileNotFoundException si el archivo no existe
	 * @throws IOException Error leyendo el archivo
	 */
	public static DictionaryTrie make(String filename) throws IOException {
		Reader r = new FileReader(filename);
		DictionaryTrie d = new DictionaryTrie(new TokenScanner(r));
		r.close();
		return d;
	}

	/**
	 * Retorna el n�mero de palabras correctas en el diccionario.
	 * Recuerde que como es case insensitive si Dogs y doGs est�n en el 
	 * diccionario, cuentan como una sola palabra.
	 * 
	 * @return n�mero de palabras �nicas
	 */
	@Override
	public int getNumWords() {
		return this.dictionary.getUniqueWords();
	}

	/**
	 * Testea si una palabra es parte del diccionario. Si la palabra no est� en
	 * el diccionario debe retornar false. null debe retornar falso.
	 * Si en el diccionario est� la palabra Dog y se pregunta por la palabra dog
	 * debe retornar true, ya que es case insensitive.
	 *
	 *Llamar a este m�todo no debe reabrir el archivo de palabras.
	 *
	 * @param word verifica si la palabra est� en el diccionario. 
	 * Asuma que todos los espacios en blanco antes y despues de la palabra fueron removidos.
	 * @return si la palabra est� en el diccionario.
	 */
	@Override
	public boolean isWord(String word) {
		if (word == null) {
			return false;
		}
		return this.dictionary.isWord(word.toLowerCase()) ;
	}
}