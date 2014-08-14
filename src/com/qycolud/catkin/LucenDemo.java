package com.qycolud.catkin;

import java.io.File;
import java.io.IOException;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.index.IndexWriterConfig.OpenMode;
import org.apache.lucene.queryparser.classic.MultiFieldQueryParser;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.store.RAMDirectory;
import org.apache.lucene.util.Version;
import org.junit.Test;

import com.qycolud.catkin.utils.IKAnalyzer;
import com.qycolud.catkin.utils.LuceneUtil;

public class LucenDemo {

	private static final String filePath = "D:/Lucene/source";
	private static final String indexPath = "D:/Lucene/index";
	private static final Analyzer analyzer = new IKAnalyzer(Version.LUCENE_47, true);

	@Test
	public void createIndex() throws IOException {
		Directory fsDir = FSDirectory.open(new File(indexPath));
		Directory ramDir = new RAMDirectory(fsDir, null);

		IndexWriterConfig fsConfig = new IndexWriterConfig(Version.LUCENE_47, analyzer);
		fsConfig.setOpenMode(OpenMode.CREATE);
		IndexWriter fsWriter = new IndexWriter(fsDir, fsConfig);

		IndexWriterConfig ramConfig = new IndexWriterConfig(Version.LUCENE_47, analyzer);
		ramConfig.setRAMBufferSizeMB(256);
		IndexWriter ramWriter = new IndexWriter(ramDir, ramConfig);
		LuceneUtil.indexDocs(ramWriter, new File(filePath));
		ramWriter.close();

		fsWriter.forceMerge(3);
		fsWriter.addIndexes(ramDir);
		fsWriter.close();
	}

	@Test
	public void searcher() throws Exception {
		IndexReader reader = DirectoryReader.open(FSDirectory.open(new File(indexPath)));

		IndexSearcher indexSearcher = new IndexSearcher(reader);
		String[] fields = { "name", "content", "size", "path" };
		QueryParser queryParser = new MultiFieldQueryParser(Version.LUCENE_47, fields, analyzer);
		Query query = queryParser.parse("中国");

		if (indexSearcher != null) {
			TopDocs docs = indexSearcher.search(query, 100);
			System.out.println("========共有" + docs.totalHits + "条匹配结果========");
			for (ScoreDoc scoreDoc : docs.scoreDocs) {
				Document document = indexSearcher.doc(scoreDoc.doc);

				System.out.println("标题:" + document.get("name"));
				// System.out.println("size=" + document.get("size"));
				// System.out.println("path=" + document.get("path"));
				System.out.println("摘要:" + LuceneUtil.getBestFragment(analyzer, query, document));
			}
		}
		reader.close();
	}

	@Test
	public void printTerms() throws Exception {
		String content = "I 课程you由中国浅入深的介绍了Lucene4的发展历史，开发环中国境搭建中国，分析lucene4的中文分词原理，深入讲了lucene4的系统架构，分析lucene4索引实现原理中国及性能优化，了解中国关于lucene4的搜索算法优中国化及利用java中国结合lucene4实中国现类百度文库中国的全文检索功能中国等相对中国高端实用的内容等。";
		LuceneUtil.printTerms(analyzer, content);
	}
	
}
