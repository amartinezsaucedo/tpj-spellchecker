package edu.isistan.spellchecker.corrector.impl;

import java.util.LinkedHashSet;
import java.util.Set;

import edu.isistan.spellchecker.corrector.Corrector;
import edu.isistan.spellchecker.corrector.Dictionary;
import edu.isistan.spellchecker.tokenizer.TokenScanner;

/**
 * Este corrector sugiere correciones cuando dos letras adyacentes han sido cambiadas.
 * <p>
 * Un error común es cambiar las letras de orden, e.g.
 * "with" -> "wiht". Este corrector intenta dectectar palabras con exactamente un swap.
 * <p>
 * Por ejemplo, si la palabra mal escrita es "haet", se debe sugerir
 * tanto "heat" como "hate".
 * <p>
 * Solo cambio de letras contiguas se considera como swap.
 */
public class SwapCorrector extends Corrector {

	private final Dictionary dictionary;

	/**
	 * Construcye el SwapCorrector usando un Dictionary.
	 *
	 * @param dict 
	 * @throws IllegalArgumentException si el diccionario provisto es null
	 */
	public SwapCorrector(Dictionary dict) {
		if (dict == null) {
			throw new IllegalArgumentException("El diccionario es null");
		}
		this.dictionary = dict;
	}

	/**
	 * 
	 * Este corrector sugiere correciones cuando dos letras adyacentes han sido cambiadas.
	 * <p>
	 * Un error común es cambiar las letras de orden, e.g.
	 * "with" -> "wiht". Este corrector intenta dectectar palabras con exactamente un swap.
	 * <p>
	 * Por ejemplo, si la palabra mal escrita es "haet", se debe sugerir
	 * tanto "heat" como "hate".
	 * <p>
	 * Solo cambio de letras contiguas se considera como swap.
	 * <p>
	 * Ver superclase.
	 *
	 * @param wrong 
	 * @return retorna un conjunto (potencialmente vacío) de sugerencias.
	 * @throws IllegalArgumentException si la entrada no es una palabra válida 
	 */

	public Set<String> getCorrections(String wrong) {
		if (!TokenScanner.isWord(wrong)) {
			throw new IllegalArgumentException("La entrada no es una palabra valida");
		}
		Set<String> swaps = new LinkedHashSet<>();
		for (int index = 0; index < wrong.length() - 1; index++) {
			StringBuilder stringBuilder = new StringBuilder(wrong);
			stringBuilder.setCharAt(index, wrong.charAt(index + 1));
			stringBuilder.setCharAt(index + 1, wrong.charAt(index));
			String swap = stringBuilder.toString();
			if (this.dictionary.isWord(swap)) {
				swaps.add(swap);
			}
		}
		return this.matchCase(wrong, swaps);
	}
}
