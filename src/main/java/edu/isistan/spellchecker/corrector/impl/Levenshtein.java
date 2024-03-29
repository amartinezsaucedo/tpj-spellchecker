package edu.isistan.spellchecker.corrector.impl;
import java.util.LinkedHashSet;
import java.util.Set;

import edu.isistan.spellchecker.corrector.Corrector;
import edu.isistan.spellchecker.corrector.Dictionary;

/**
 *
 * Un corrector inteligente que utiliza "edit distance" para generar correcciones.
 * 
 * La distancia de Levenshtein es el n�mero minimo de ediciones que se deber
 * realizar a un string para igualarlo a otro. Por edici�n se entiende:
 * <ul>
 * <li> insertar una letra
 * <li> borrar una letra
 * <li> cambiar una letra
 * </ul>
 *
 * Una "letra" es un caracter a-z (no contar los apostrofes).
 * Intercambiar letras (thsi -> this) <it>no</it> cuenta como una edici�n.
 * <p>
 * Este corrector sugiere palabras que esten a edit distance uno.
 */
public class Levenshtein extends Corrector {

	private static final int MAXIMUM_EDIT_DISTANCE = 1;
	private Dictionary dictionary;
	/**
	 * Construye un Levenshtein Corrector usando un Dictionary.
	 * Debe arrojar <code>IllegalArgumentException</code> si el diccionario es null.
	 *
	 * @param dict
	 */
	public Levenshtein(Dictionary dict) {
		if (dict == null) {
			throw new IllegalArgumentException("El diccionario es null");
		}
		this.dictionary = dict;
	}

	/**
	 * @param s palabra
	 * @return todas las palabras a erase distance uno
	 */
	Set<String> getDeletions(String s) {
		Set<String> deletions = new LinkedHashSet<>();
		if (s.length() > 1) {
			for (int index = 0; index < s.length(); index++) {
				String candidate = new StringBuilder(s).deleteCharAt(index).toString();
				if (this.dictionary.isWord(candidate)) {
					deletions.add(candidate);
				}
			}
		}
		return deletions;
	}

	/**
	 * @param s palabra
	 * @return todas las palabras a substitution distance uno
	 */
	public Set<String> getSubstitutions(String s) {
		Set<String> substitutions = new LinkedHashSet<>();
		for (int index = 0; index < s.length(); index++) {
			StringBuilder stringBuilder = new StringBuilder(s);
			for (char letter = 'a'; letter <= 'z'; letter++) {
				stringBuilder.setCharAt(index, letter);
				String candidate = stringBuilder.toString();
				if (!candidate.equals(s) && this.dictionary.isWord(candidate)) {
					substitutions.add(candidate);
				}
			}
		}
		return substitutions;
	}


	/**
	 * @param s palabra
	 * @return todas las palabras a insert distance uno
	 */
	public Set<String> getInsertions(String s) {
		Set<String> insertions = new LinkedHashSet<>();
		for (int index = 0; index <= s.length(); index++) {
			for (char letter = 'a'; letter <= 'z'; letter++) {
				StringBuilder stringBuilder = new StringBuilder(s);
				stringBuilder.insert(index, letter);
				String candidate = stringBuilder.toString();
				if (this.dictionary.isWord(candidate)) {
					insertions.add(candidate);
				}
			}
		}
		return insertions;
	}

    public Set<String> getCorrections(String wrong) {
        if (wrong == null) {
            throw new IllegalArgumentException("Word is null");
        }
        Set<String> corrections = new LinkedHashSet<>();
        Set<String> suggestions = this.dictionary.getSimilarWords(wrong.toLowerCase());
        if (suggestions.isEmpty()) {
            corrections.addAll(this.getDeletions(wrong));
            corrections.addAll(this.getSubstitutions(wrong));
            corrections.addAll(this.getInsertions(wrong));
        } else {
            for (String suggestion : suggestions) {
                if (calculateDistance(suggestion, wrong) <= MAXIMUM_EDIT_DISTANCE) {
                    corrections.add(suggestion);
                }
            }
        }
        return this.matchCase(wrong, corrections);
    }

    private int calculateDistance(String x, String y) {
        int[][] distanceMatrix = new int[x.length() + 1][y.length() + 1];
        for (int i = 0; i <= x.length(); i++) {
            for (int j = 0; j <= y.length(); j++) {
                if (i == 0) {
                    distanceMatrix[i][j] = j;
                } else if (j == 0) {
                    distanceMatrix[i][j] = i;
                } else {
                    distanceMatrix[i][j] = Math.min(Math.min(distanceMatrix[i - 1][j - 1]
                                            + (x.charAt(i - 1) == y.charAt(j - 1) ? 0 : 1),
                                    distanceMatrix[i - 1][j] + 1),
                            distanceMatrix[i][j - 1] + 1);
                }
            }
        }
        return distanceMatrix[x.length()][y.length()];
    }
}
