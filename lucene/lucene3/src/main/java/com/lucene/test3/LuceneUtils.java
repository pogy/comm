package com.lucene.test3;

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
import org.apache.lucene.document.Field.Index;
import org.apache.lucene.document.Field.Store;
import org.apache.lucene.index.CorruptIndexException;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.index.Term;
import org.apache.lucene.queryParser.MultiFieldQueryParser;
import org.apache.lucene.queryParser.ParseException;
import org.apache.lucene.queryParser.QueryParser;
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

	/**
	 * 索引文件夹
	 */
	private static String indexDir = "D:\\test\\gm\\index3\\";

	private String getIndexDir() {
		return indexDir;
	}
	/**
	 * 分析器
	 */
	private Analyzer analyzer = new StandardAnalyzer(Version.LUCENE_36);
//	private Analyzer analyzer = new SimpleAnalyzer(Version.LUCENE_36);
	/**
	 * 去除检索内容前的符号
	 * 
	 * @param content
	 *            检索内容
	 * @return 去除前置符号前的检索内容
	 */
	public String tranStr(String content) {
		char[] ch = content.toCharArray();
		for (int i = 0; i < ch.length; i++) {
			String str = (ch[i] + "").replace("[^\\w\\s]", "");
			if (str.length() > 0) {
				return content.substring(i).trim();
			}
		}
		return "";
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

	/**
	 * 普通的全文检索，查询参数与返回参数相同<br/>
	 * 
	 * @param content
	 *            查询内容 <br/>
	 * @param url
	 *            索引所在文件夹 例：版块为plate<br/>
	 * @param fields
	 *            获取结果参数和查询参数为相同<br/>
	 * @param start
	 *            当前页
	 * @param pageSize
	 *            分页大小
	 * @return
	 * @throws Exception
	 */
	public Page<Map<String, String>> search(String[] queryStr,String[] field, int start, int pageSize) throws Exception {
		Page<Map<String, String>> pager = new Page<Map<String, String>>(start,pageSize, 0, new ArrayList<Map<String, String>>());
		IndexReader reader = IndexReader.open(FSDirectory.open(new File(getIndexDir())));
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
		QueryParser queryParser = new MultiFieldQueryParser(Version.LUCENE_36,field, analyzer);
		Query query = queryParser.parse(queryStr[0]);
		CachingWrapperFilter filter = null; 				// 创建二次索引前的一次缓存
			if (queryStr.length > 1) { 						// 判断是否要进行二次检索
			filter = filterIndex(field, queryStr[1]);
		}
		Lucene entity = new Lucene();
		if(start<=0){start = 1;}
		TopDocs result = indexSearch.search(query, filter, pageSize*start);
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
	public CachingWrapperFilter filterIndex(String[] fields, String content)
			throws ParseException {
		String[] field = new String[1]; // 取得检索缓存字段
		field[0] = fields[0];
		QueryParser queryParser = new MultiFieldQueryParser(Version.LUCENE_36,field, analyzer);
		Query query = queryParser.parse(content); // 创建检索语句
		QueryWrapperFilter oldFilter = new QueryWrapperFilter(query); // 创建检索缓存
		CachingWrapperFilter filter = new CachingWrapperFilter(oldFilter); // 取得检索结果
		return filter;
	}

	/**
	 * 新增Bean索引
	 * 
	 * @param revert
	 * @return
	 */
	public boolean createrIndex(List<BbsBean> list) {
		Directory directory = null;
		IndexWriter indexWriter = null;
		try{
			directory = FSDirectory.open(new File(indexDir)); // 打开索引库
			IndexWriterConfig iwc = new IndexWriterConfig(Version.LUCENE_36, analyzer);
			indexWriter = new IndexWriter(directory, iwc);
			indexWriter.deleteAll();
			for(int i = 0 ; i < list.size(); i++){
				Document doc = new Document();
				BbsBean bean = list.get(i);
				doc.add(new Field(LuceneType.ALL_TYPE, LuceneType.TYPE_TOPIC,Store.YES, Index.NOT_ANALYZED));
				doc.add(new Field(LuceneType.ALL_ID, bean.getTopicId(), Store.YES,Index.NOT_ANALYZED));
				doc.add(new Field(LuceneType.ALL_TITLE, bean.getTitle(), Store.YES,Index.ANALYZED));
				doc.add(new Field(LuceneType.ALL_CONTENT, bean.getRevertContent().replaceAll("<\\S[^>]+>", "").replaceAll("<p>", ""), Store.YES,Index.ANALYZED));
				doc.add(new Field(LuceneType.ALL_URL, bean.getRevertUrl(), Store.YES,Index.NOT_ANALYZED));
				indexWriter.addDocument(doc); // 将索引关键字添加到文件夹中
			}
		}catch (Exception e) {
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

	/**
	 * 删除索引<br/>
	 * 
	 * @param url
	 *            索引所在文件夹 例：版块为plate<br/>
	 * @param content
	 *            检索内容
	 * @param field
	 *            删除时的检索字段
	 * @return
	 */
	public boolean deleteIndex(String id, String field) {
		IndexWriter writer = null;
		try {
			Directory dir = FSDirectory.open(new File(indexDir + getIndexDir())); // 打开索引库
			writer = new IndexWriter(dir, new IndexWriterConfig(Version.LUCENE_36, analyzer));
			if (id == null && field == null) { // 是否为全部删除操作
				writer.deleteAll();
			} else {
				writer.deleteDocuments(new Term(field, id));
			}
		} catch (Exception e) {
			return false;
		} finally {
			try {
				if (writer != null)
					writer.close();
			} catch (CorruptIndexException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return false;
	}

	/**
	 * 更新索引
	 * 
	 * @param url
	 *            索引所在文件夹 例：版块为plate<br/>
	 * @param id
	 *            更新的具体对象
	 * @param field
	 *            更新时的检索字段
	 * @param doc
	 *            更新内容
	 * @return
	 */
	public boolean updateIndex(String id, String field, Document doc) {
		IndexWriter writer = null;
		Directory dir = null;
		try {
			writer = new IndexWriter(dir, new IndexWriterConfig(Version.LUCENE_36, analyzer));
			dir = FSDirectory.open(new File(indexDir + getIndexDir())); // 打开索引库
			writer.updateDocument(new Term(field, id), doc);
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
		return true;
	}
}
