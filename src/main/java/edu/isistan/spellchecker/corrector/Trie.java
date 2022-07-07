package edu.isistan.spellchecker.corrector;

import java.util.ArrayDeque;
import java.util.Queue;

public class Trie {
	private TrieNode root;

	public Trie() {
		this.root = new TrieNode();
	}

	public void addWord(String word) {
		String value = word.toLowerCase();
		TrieNode node = this.root;
		for (int index = 0; index < value.length(); index++) {
			node = node.getChildren().computeIfAbsent(value.charAt(index), absent -> new TrieNode());
		}
		node.setTerminal(true);
	}

	public boolean isWord(String word) {
		TrieNode node = this.root;
		for (int index = 0; index < word.length(); index++) {
			node = node.getChildren().get(word.charAt(index));
			if (node == null) {
				return false;
			}
		}
		return node.isTerminal();
	}

	public int getUniqueWords() {
		int uniqueWords = 0;
		Queue<TrieNode> queue = new ArrayDeque<>();
		queue.add(this.root);
		while (!queue.isEmpty()) {
			TrieNode node = queue.remove();
			if (node.isTerminal()) {
				uniqueWords++;
			}
			queue.addAll(node.getChildren().values());
		}
		return uniqueWords;
	}
}