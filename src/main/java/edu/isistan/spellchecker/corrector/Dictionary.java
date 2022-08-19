package edu.isistan.spellchecker.corrector;

import edu.isistan.spellchecker.lsh.MinHash;
import edu.isistan.spellchecker.lsh.MinHashLSH;
import edu.isistan.spellchecker.lsh.NGram;
import edu.isistan.spellchecker.tokenizer.TokenScanner;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.util.HashSet;
import java.util.Set;

/**
 * El diccionario maneja todas las palabras conocidas.
 * El diccionario es case insensitive
 *
 * Una palabra "v�lida" es una secuencia de letras (determinado por Character.isLetter)
 * o apostrofes.
 */
public class Dictionary {
	private static final int NUMBER_OF_PERMUTATIONS = 64;
	private Set<String> dictionary;
	private MinHashLSH lsh;
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
	public Dictionary(TokenScanner ts) throws IOException {
		if (ts == null) {
			throw new IllegalArgumentException("TokenScanner es null");
		}
		this.lsh = new MinHashLSH();
		this.doInitializeDictionary(ts);
	}

	protected void doInitializeDictionary(TokenScanner tokenScanner) {
		this.dictionary = new HashSet<>();
		while (tokenScanner.hasNext()) {
			String token = tokenScanner.next().toLowerCase();
			if (TokenScanner.isWord(token)) {
				this.insertInLSH(token);
				this.dictionary.add(token);
			}
		}
	}

	protected void insertInLSH(String token) {
		MinHash minHash = new MinHash(NUMBER_OF_PERMUTATIONS);
		Set<String> ngrams = NGram.ngrams(1, token);
		for (String ngram : ngrams) {
			minHash.update(ngram);
		}
		lsh.insert(token, minHash);
	}

	/**
	 * Construye un diccionario usando un archivo.
	 *
	 *
	 * @param filename
	 * @throws FileNotFoundException si el archivo no existe
	 * @throws IOException Error leyendo el archivo
	 */
	public static Dictionary make(String filename) throws IOException {
		Reader r = new FileReader(filename);
		Dictionary d = new Dictionary(new TokenScanner(r));
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
	public int getNumWords() {
		return this.dictionary.size();
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
	public boolean isWord(String word) {
		if (word == null) {
			return false;
		}
		return this.dictionary.contains(word.toLowerCase()) ;
	}

	public Set<String> getSimilarWords(String misspelledWord) {
		MinHash queryMinHash = new MinHash(NUMBER_OF_PERMUTATIONS);
		Set<String> ngrams = NGram.ngrams(1, misspelledWord.toLowerCase());
		for (String ngram : ngrams) {
			queryMinHash.update(ngram);
		}
		return lsh.query(queryMinHash);
	}
}