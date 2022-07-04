package edu.isistan.spellchecker;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Reader;
import java.io.Writer;

import edu.isistan.spellchecker.corrector.Corrector;
import edu.isistan.spellchecker.corrector.Dictionary;
import edu.isistan.spellchecker.corrector.impl.FileCorrector;
import edu.isistan.spellchecker.corrector.impl.Levenshtein;
import edu.isistan.spellchecker.corrector.impl.SwapCorrector;

/**
 * 
 * Main para el programa del spellcheker.  
 * <p>
 * Puede ser usado desde linea de comando:
 * <p>
 * <code>java SpellCheckerRunner &ltin&gt &ltout&gt &ltdictionary&gt &ltcorrector&gt</code>
 * <p>
 * <ul>
 * <li> &ltin&gt - archivo de entrada
 * <li> &ltout&gt - archivo de salida
 * <li> &ltdictionary&gt - diccionario.
 * <li> &ltcorrector&gt -  SWAP (para SwapCorrector), 
 * LEV (para Levenshtein), o nombre de archivo (para FileCorrector)
 * </ul>
 * 
 */
public class SpellCheckerRunner {
	/**
	 * Crea el corrector adecuado dada la entrada de la linea de comando.
	 * 
	 * @param type
	 * @param dict
	 * @throws IOException
	 * @throws FileCorrector.FormatException
	 */
	private static Corrector makeCorrector(String type, Dictionary dict)
			throws IOException, FileCorrector.FormatException {
		if (type.equals("SWAP")) {
			return new SwapCorrector(dict);
		}
		if (type.equals("LEV")) {
			return new Levenshtein(dict);
		}

		return FileCorrector.make(type);
	}

	public static void main(String[] args) {
		if (args.length != 4) {
			System.out.println("uso: java SpellCheckRunner <in> <out> <dictionary> <corrector>");
			System.out.println("<corrector> es SWAP, LEV, or el path para instanciar el FileCorrector.");
			return;
		}
		try {
			Reader in = new BufferedReader(new FileReader(args[0]));
			Writer out = new BufferedWriter(new FileWriter(args[1]));
			Dictionary dict = Dictionary.make(args[2]);
			SpellChecker sp = new SpellChecker(makeCorrector(args[3], dict), dict);
			sp.checkDocument(in, System.in, out);
			in.close();
			out.flush();
			out.close();
		} catch (IOException e) {
			System.out.println("error procesando el document: " + e.getMessage());
		} catch (FileCorrector.FormatException e) {
			System.out.println("error de formato: " + e.getMessage());
		}
	}
}
