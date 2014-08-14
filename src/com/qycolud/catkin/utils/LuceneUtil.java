package com.qycolud.catkin.utils;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.tokenattributes.CharTermAttribute;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.Field.Store;
import org.apache.lucene.document.LongField;
import org.apache.lucene.document.StringField;
import org.apache.lucene.document.TextField;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig.OpenMode;
import org.apache.lucene.index.Term;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.highlight.Formatter;
import org.apache.lucene.search.highlight.Fragmenter;
import org.apache.lucene.search.highlight.Highlighter;
import org.apache.lucene.search.highlight.QueryScorer;
import org.apache.lucene.search.highlight.SimpleFragmenter;
import org.apache.lucene.search.highlight.SimpleHTMLFormatter;
import org.apache.lucene.util.Version;

public class LuceneUtil {

	/**
	 * 写入索引
	 * 
	 * @param writer
	 * @param file
	 * @throws IOException
	 */
	public static void indexDocs(IndexWriter writer, File file) throws IOException {
		if (file != null && file.canRead()) {
			if (file.isDirectory()) {
				File[] files = file.listFiles();
				for (File f : files) {
					indexDocs(writer, f);
				}
			} else {
				Document doc = new Document();
				doc.add(new Field("name", file.getName(), TextField.TYPE_STORED));
				doc.add(new StringField("path", file.getPath(), Store.YES));
				doc.add(new LongField("size", file.length(), Store.YES));
				doc.add(new TextField("content", FileReaderAll(file), Store.NO));

				if (OpenMode.CREATE == writer.getConfig().getOpenMode()) {
					writer.addDocument(doc);
				} else {
					writer.updateDocument(new Term("path", file.getPath()), doc);
				}
			}
		}
	}

	/**
	 * 获取摘要文本并关键字高亮
	 * 
	 * @param analyzer
	 * @param query
	 * @param doc
	 * @return
	 * @throws Exception
	 */
	public static String getBestFragment(Analyzer analyzer, Query query, Document doc) throws Exception {
		QueryScorer scorer = new QueryScorer(query);
		Formatter formatter = new SimpleHTMLFormatter("【", "】");
		Fragmenter fragmenter = new SimpleFragmenter(50);
		Highlighter highlighter = new Highlighter(formatter, scorer);
		highlighter.setTextFragmenter(fragmenter);
		String content = FileReaderAll(new File(doc.get("path")));

		highlighter.setTextFragmenter(fragmenter);
		String bestFragment = highlighter.getBestFragment(analyzer, "content", content);
		if (bestFragment == null) {
			int endIndex = content.length() < 30 ? content.length() : 30;
			bestFragment = content.substring(0, endIndex);
		}

		return bestFragment;
	}

	/**
	 * 打印分词字典
	 * 
	 * @param analyzer
	 * @param content
	 * @throws Exception
	 */
	public static void printTerms(Analyzer analyzer, String content) throws Exception {
		StringBuffer stringBuffer = new StringBuffer();

		/**
		 * 打印分词字典
		 */
		
		  TokenStream ts = analyzer.tokenStream("", content); 
		  CharTermAttribute cta = ts.getAttribute(CharTermAttribute.class);
		  while (ts.incrementToken()) stringBuffer.append(cta.toString()).append("\n");
		 

		/**
		 * 打印分词详细信息
		 */
		/*Directory ramDirectory = new RAMDirectory();
		IndexWriter iWriter = new IndexWriter(ramDirectory, new IndexWriterConfig(Version.LUCENE_47, analyzer));
		Document document = new Document();
		document.add(new TextField("content", content, Store.NO));
		iWriter.addDocument(document);
		iWriter.close();

		DirectoryReader reader = DirectoryReader.open(ramDirectory);
		System.out.println("关键字\t\t文档ID[出现频率]\t\t出现位置");
		TermsEnum termsEnum = MultiFields.getTerms(reader, "content").iterator(null);

		while (termsEnum.next() != null) {
			stringBuffer.append(termsEnum.term().utf8ToString() + "\t\t");
			DocsAndPositionsEnum dpe = termsEnum.docsAndPositions(null, null);
			int i = 0;
			int j = 0;
			while (dpe.nextDoc() != DocIdSetIterator.NO_MORE_DOCS) {
				if (i > 0) {
					stringBuffer.append("\t\t");
				}

				stringBuffer.append(dpe.docID()).append("[").append(dpe.freq()).append("]\t\t");
				for (j = 0; j < dpe.freq(); j++) {
					stringBuffer.append(dpe.nextPosition()).append(", ");
				}
				stringBuffer.append("\n");
				i++;
			}
		}*/

		System.out.println(stringBuffer);
	}

	private static String FileReaderAll(File file) throws IOException {
		BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(file), "UTF-8"));
		String line = new String();
		StringBuffer stringBuffer = new StringBuffer();

		while ((line = reader.readLine()) != null) {
			stringBuffer.append(line);
		}
		reader.close();
		return stringBuffer.toString();
	}
	
	public static void main(String[] args) throws Exception {
		printTerms(new IKAnalyzer(Version.LUCENE_47), "这是一王二麻个在U盘很漂亮");
	}
}
