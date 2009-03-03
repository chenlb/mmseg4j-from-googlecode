package com.chenlb.mmseg4j;

import java.util.ArrayList;
import java.util.Collections;

/**
 * 
 * 
 * @author chenlb 2009-2-20 下午11:30:14
 */
public class CharNode {

	private ArrayList<char[]> wordTails = new ArrayList<char[]>();	//word除去一个字的部分
	private int freq;	//单字才需要
	
	private CharArrayComparator cac = new CharArrayComparator();

	public void addWordTail(char[] wordTail) {
		wordTails.add(wordTail);
	}
	public int getFreq() {
		return freq;
	}
	
	public void setFreq(int freq) {
		this.freq = freq;
	}
	
	public void sort() {
		Collections.sort(wordTails, cac);
	}
	
	/**
	 * @return 能找到 >=0, 否则返回负数
	 * @author chenlb 2009-3-3 下午11:09:07
	 */
	public int indexOf(char[] wordTail) {
		return Collections.binarySearch(wordTails, wordTail, cac);
	}
}
