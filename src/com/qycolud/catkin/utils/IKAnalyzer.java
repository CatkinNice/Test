package com.qycolud.catkin.utils;

import java.io.Reader;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.Tokenizer;
import org.apache.lucene.util.Version;
import org.wltea.analyzer.lucene.IKTokenizer;

/**
 * IK分词器，Lucene Analyzer接口实现
 * 
 */
public class IKAnalyzer extends Analyzer{
	
	private boolean useSmart;
	
	public boolean useSmart() {
		return useSmart;
	}

	public void setUseSmart(boolean useSmart) {
		this.useSmart = useSmart;
	}

	/**
	 * IK分词器Lucene 4.7
	 * 
	 * 默认细粒度切分算法
	 */
	public IKAnalyzer(Version matchVersion){
		this(matchVersion, false);
	}
	
	/**
	 * IK分词器Lucene 4.7
	 * 
	 * @param useSmart 当为true时，分词器进行智能切分
	 */
	public IKAnalyzer(Version matchVersion, boolean useSmart){
		super();
		this.useSmart = useSmart;
	}

	@Override
	protected TokenStreamComponents createComponents(String s, Reader reader) {
		Tokenizer tokenizer = new IKTokenizer(reader, useSmart()); 
		TokenStream tokenStream = new IKTokenizer(reader , this.useSmart());
		return new TokenStreamComponents(tokenizer, tokenStream);
	}

}
