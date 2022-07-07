package edu.isistan.spellchecker.corrector;

import java.util.HashMap;
import java.util.Map;

public class TrieNode {
	private Map<Character, TrieNode> children;
	private char value;
	private boolean isTerminal;

	public TrieNode() {
		this.children = new HashMap<>();
	}

	public Map<Character, TrieNode> getChildren() {
		return children;
	}

	public void setChildren(Map<Character, TrieNode> children) {
		this.children = children;
	}

	public char getValue() {
		return value;
	}

	public void setValue(char value) {
		this.value = value;
	}

	public boolean isTerminal() {
		return isTerminal;
	}

	public void setTerminal(boolean terminal) {
		isTerminal = terminal;
	}
}