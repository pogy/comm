package com.lucene.test4;

import java.io.File;
import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.StringField;
import org.apache.lucene.document.TextField;
import org.apache.lucene.index.CorruptIndexException;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.queryparser.classic.MultiFieldQueryParser;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.CachingWrapperFilter;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.QueryWrapperFilter;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.search.highlight.Highlighter;
import org.apache.lucene.search.highlight.InvalidTokenOffsetsException;
import org.apache.lucene.search.highlight.QueryScorer;
import org.apache.lucene.search.highlight.SimpleHTMLFormatter;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.util.Version;

import com.lucene.utils.BbsBean;
import com.lucene.utils.Lucene;
import com.lucene.utils.LuceneType;
import com.lucene.utils.Page;

public class LuceneUtils {

	private String indexDir = "D:\\test\\gm\\index4\\";
	@SuppressWarnings("deprecation")
	private static Analyzer analyzer = new StandardAnalyzer(Version.LUCENE_4_10_2);

	/**
	 * 索引创建
	 * 
	 * @param revert
	 * @return
	 */
	public boolean createrIndex(List<BbsBean> lbean) {
		Directory directory = null;
		IndexWriter indexWriter = null;
		try {
			directory = FSDirectory.open(new File(indexDir));
			IndexWriterConfig iwc = new IndexWriterConfig(Version.LUCENE_4_10_2, analyzer);
			indexWriter = new IndexWriter(directory, iwc);
			indexWriter.deleteAll();
			for(int i = 0 ; i < lbean.size(); i++){
				Document doc = new Document();
				BbsBean bean = lbean.get(i);
				doc.add(new StringField(LuceneType.ALL_TYPE,LuceneType.TYPE_TOPIC, Field.Store.YES));
				doc.add(new TextField(LuceneType.ALL_ID, bean.getTopicId(),Field.Store.YES));
				doc.add(new TextField(LuceneType.ALL_TITLE, bean.getTitle(),Field.Store.YES));
				doc.add(new TextField(LuceneType.ALL_CONTENT, bean.getRevertContent().replaceAll("<\\S[^>]+>", "").replaceAll("<p>", ""), Field.Store.YES));
				doc.add(new TextField(LuceneType.ALL_URL,bean.getRevertUrl(), Field.Store.YES));
				indexWriter.addDocument(doc);
			}
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		} finally {
			try {
				if (indexWriter != null) {
					indexWriter.close();
				}
			} catch (CorruptIndexException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return true;
	}

	
	public Page<Map<String, String>> search(String[] queryStr,String[] field,int start,int pageSize) throws Exception {
		Page<Map<String, String>> pager = new Page<Map<String, String>>(start,pageSize, 0, new ArrayList<Map<String, String>>());
		IndexReader reader = DirectoryReader.open(FSDirectory.open(new File(indexDir)));
		IndexSearcher indexSearch = new IndexSearcher(reader);
		/**
		 * 同时搜索name和descr两个field，并设定它们在搜索结果排序过程中的权重，权重越高，排名越靠前
		 * 为了后面解释score问题的方便，这里设置相同的权重
		 * */
		Map<String, Float> boosts = new HashMap<String, Float>();
		boosts.put(LuceneType.ALL_TITLE, 1.0f);
		boosts.put(LuceneType.ALL_CONTENT, 1.0f);
		/**
		 * 用MultiFieldQueryParser类实现对同一关键词的跨域搜索
		 * */
		QueryParser queryParser = new QueryParser(LuceneType.ALL_CONTENT, analyzer);
		Query query = queryParser.parse(queryStr[0]);
		CachingWrapperFilter filter = null; 				// 创建二次索引前的一次缓存
			if (queryStr.length > 1) { 						// 判断是否要进行二次检索
			filter = filterIndex(field, queryStr[1]);
		}
		Lucene entity = new Lucene();
		if(start<=0){start = 1;}
		TopDocs result = indexSearch.search(query, filter, start*pageSize);
		int index = (start - 1) * pageSize;
		if (index > result.totalHits) {
			index -= pageSize;
			start--;
		}
		ScoreDoc scoreDoc = null;
		if (index > 0) {
			scoreDoc = result.scoreDocs[index - 1];
		}
		TopDocs topDocs = indexSearch.searchAfter(scoreDoc, query, filter,pageSize);
		entity.setTopDocs(topDocs);
		entity.setRowCount(topDocs.totalHits); // 总条数
		entity.setPageSize(pageSize);
		entity.setCurrent(start);
		List<Map<String, String>> list = searchHLResult(indexSearch, query,entity.getTopDocs(), field, field);
		pager = new Page<Map<String, String>>(entity.getCurrent(),pageSize, entity.getRowCount(), list);
		return pager;
	}
	
	/**
	 * 二次检索的一次缓存检索
	 * 
	 * @param field
	 *            索引字段
	 * @param content
	 *            检索条件
	 * @return
	 * @throws ParseException
	 */
	@SuppressWarnings("deprecation")
	public CachingWrapperFilter filterIndex(String[] fields, String content)
			throws ParseException {
		String[] field = new String[1]; // 取得检索缓存字段
		field[0] = fields[0];
		QueryParser queryParser = new MultiFieldQueryParser(Version.LUCENE_4_10_2,field, analyzer);
		Query query = queryParser.parse(content); // 创建检索语句
		QueryWrapperFilter oldFilter = new QueryWrapperFilter(query); // 创建检索缓存
		CachingWrapperFilter filter = new CachingWrapperFilter(oldFilter); // 取得检索结果
		return filter;
	}
	
	/**
	 * 高亮处理，返回处理结果列表
	 * 
	 * @param indexSearch
	 *            检索器
	 * @param query
	 *            检索内容
	 * @param topDocs
	 *            检索结果
	 * @param reValues
	 *            返回参数
	 * @return
	 * @throws CorruptIndexException
	 * @throws IOException
	 * @throws InvalidTokenOffsetsException
	 */
	public List<Map<String, String>> searchHLResult(IndexSearcher indexSearch,
			Query query, TopDocs topDocs, String[] reValues, String[] fields)
			throws CorruptIndexException, IOException,
			InvalidTokenOffsetsException {
		List<Map<String, String>> list = new ArrayList<Map<String, String>>();
		SimpleHTMLFormatter shf = new SimpleHTMLFormatter(LuceneType.HIGH_LIGHT_PRE, LuceneType.HIGH_LIGHT_SUB); // 创建高亮模式
		Highlighter hl = new Highlighter(shf, new QueryScorer(query)); // 创建高亮检索
		if (topDocs != null) {
			for (int i = 0; i < topDocs.scoreDocs.length; i++) {
				Document doc = indexSearch.doc(topDocs.scoreDocs[i].doc);
				Map<String, String> map = new HashMap<String, String>();
				for (String reValue : reValues) { // 根据返回参数设置，循环取得检索结果
					for (String field : fields) {
						if (reValue.equals(field)) { // 判断是否做高亮处理
							TokenStream tokenStream = analyzer.tokenStream(reValue, // 取得匹配检索结果
									new StringReader(doc.get(reValue)));
							String str = hl.getBestFragment(tokenStream,doc.get(reValue));
							map.put(reValue, str == null ? doc.get(reValue): str); // 判断该高亮处理是否为空
							break; // 高亮处理完毕，跳出内层循环
						} else { // 不做高亮处理
							map.put(reValue, doc.get(reValue));
						}
					}
				}
				list.add(map);
			}
		}
		return list;
	}
}
