package com.chenlb.mmseg4j;

import java.io.IOException;
import java.io.StringReader;

import junit.framework.TestCase;

public class MMSegTest extends TestCase {

	protected void setUp() throws Exception {
		super.setUp();
	}

	public void testNext() throws IOException {
		String txt = "";
		txt = "京华时报1月23日报道 昨天，受一股来自中西伯利亚的强冷空气影响，本市出现大风降温天气，白天最高气温只有零下7摄氏度，同时伴有6到7级的偏北风。";
		txt = "研究生命起源";
		Dictionary dic = new Dictionary();
		Seg seg = null;
		//seg = new SimpleSeg(dic);
		seg = new ComplexSeg(dic); 
		MMSeg mmSeg = new MMSeg(new StringReader(txt), seg);
		Chunk chunk = null;
		System.out.println();
		while((chunk=mmSeg.next())!=null) {
			int offset = chunk.getStartOffset();
			for(char[] word : chunk.words) {
				if(word != null) {
					System.out.print(new String(word)+" -> "+offset);
					offset += word.length;
					System.out.println(", "+offset);
				}
			}
		}
		
	}

}
