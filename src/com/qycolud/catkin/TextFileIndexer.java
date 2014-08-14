package com.qycolud.catkin;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field.Store;
import org.apache.lucene.document.StringField;
import org.apache.lucene.document.TextField;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.index.IndexWriterConfig.OpenMode;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.util.Version;
import org.junit.Test;

import com.qycolud.catkin.utils.IKAnalyzer;

public class TextFileIndexer {
	
	Analyzer analyzer = new IKAnalyzer(Version.LUCENE_47);
	
	@Test
	public void createIndexer() throws IOException {
		File fileDir = new File("D:/Lucene/source");
		File indexDir = new File("D:/Lucene/index");
		
		Directory directory = FSDirectory.open(indexDir);
		IndexWriterConfig iwc = new IndexWriterConfig(Version.LUCENE_47, analyzer);
		
		iwc.setOpenMode(OpenMode.CREATE);
		IndexWriter iw = new IndexWriter(directory, iwc);
		File [] textFiles = fileDir.listFiles();
		
		for (int i = 0; i < textFiles.length; i++) {    
            if (textFiles[i].isFile() && textFiles[i].getName().endsWith(".txt")) {    
                System.out.println("File " + textFiles[i].getCanonicalPath() + "正在被索引...");    
                String temp = FileReaderAll(textFiles[i].getCanonicalPath(), "UTF-8");    
                System.out.println(temp);    
                Document document = new Document();    
                  
                document.add(new StringField("path", textFiles[i].getPath(), Store.YES));
                document.add(new TextField("contents", temp, Store.YES));
                iw.addDocument(document);
            }    
        } 
        iw.close();
	}
	
	@Test
	public void searcher() throws Exception {
		File indexDir = new File("D:/Lucene/index");
		IndexReader reader = DirectoryReader.open(FSDirectory.open(indexDir));
		
		IndexSearcher searcher = new IndexSearcher(reader);
		
		ScoreDoc[] hits = null;
		String queryStr = "中国";
		Query query = null;
		
		QueryParser queryParser = new QueryParser(Version.LUCENE_47, "contents", analyzer);
		query = queryParser.parse(queryStr);

		if(searcher != null){
			TopDocs results = searcher.search(query, 10);
			hits = results.scoreDocs;
			for (ScoreDoc scoreDoc : hits) {
				Document doc = searcher.doc(scoreDoc.doc);
				System.out.println("path= " 	+ doc.get("path"));
				System.out.println("contents= " + doc.get("contents"));;
			}
			System.out.println("结果集:" + results.totalHits);
			
		}
		reader.close();
	}
	
	private static String FileReaderAll(String content, String charset)    
            throws IOException {    
        BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(content), charset));    
        String line = new String();    
        String temp = new String();    
            
        while ((line = reader.readLine()) != null) {    
            temp += line;    
        }    
        reader.close();    
        return temp;    
    }

}
