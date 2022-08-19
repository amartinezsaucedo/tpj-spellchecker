package edu.isistan.spellchecker.corrector.impl;

import java.util.LinkedHashSet;
import java.util.Set;

import edu.isistan.spellchecker.corrector.Corrector;
import edu.isistan.spellchecker.corrector.Dictionary;
import edu.isistan.spellchecker.tokenizer.TokenScanner;

/**
 * Este corrector sugiere correciones cuando dos letras adyacentes han sido cambiadas.
 * <p>
 * Un error com�n es cambiar las letras de orden, e.g.
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
	 * Un error com�n es cambiar las letras de orden, e.g.
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
	 * @return retorna un conjunto (potencialmente vac�o) de sugerencias.
	 * @throws IllegalArgumentException si la entrada no es una palabra v�lida 
	 */

	public Set<String> getCorrections(String wrong) {
		if (!TokenScanner.isWord(wrong)) {
			throw new IllegalArgumentException("La entrada no es una palabra valida");
		}
		Set<String> swaps = new LinkedHashSet<>();
		Set<String> suggestions = this.dictionary.getSimilarWords(wrong);
		for (String suggestion : suggestions) {
			if (isSwap(suggestion, wrong.toLowerCase())) {
				swaps.add(suggestion);
			}
		}
		return this.matchCase(wrong, swaps);
	}

	private boolean isSwap(String suggestion, String wrong) {
		int suggestionLength = suggestion.length();
		int wrongLength = wrong.length();
		if (suggestionLength != wrongLength) {
			return false;
		}
		for (int i = 0; i < suggestion.length() - 1; i++) {
			if (suggestion.charAt(i) != wrong.charAt(i) && ((suggestion.charAt(i) == wrong.charAt(i + 1) && wrong.charAt(i) == suggestion.charAt(i + 1)))) {
				return true;
			}
		}
		return false;
	}
}
