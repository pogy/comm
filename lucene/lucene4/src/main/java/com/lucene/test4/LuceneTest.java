package com.lucene.test4;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.lucene.utils.BbsBean;
import com.lucene.utils.LuceneType;
import com.lucene.utils.Page;


public class LuceneTest {
	public static void main(String[] args) {
		String[] field = new String[] {LuceneType.ALL_TYPE,LuceneType.ALL_ID,LuceneType.ALL_TITLE,LuceneType.ALL_CONTENT};
		LuceneUtils lu = new LuceneUtils();
		List<BbsBean> lbean = new ArrayList<BbsBean>();
		try{
			for(int i = 1 ; i <= 100; i++){
				BbsBean beans = new BbsBean();
				beans.setTopicId(String.valueOf((1000+i)));
				beans.setTitle("这是标题:"+i);
				beans.setRevertContent("这是内容曾维龙" + i);
				beans.setRevertUrl("http://www.baidu.com");
				lbean.add(beans);
			}
			lu.createrIndex(lbean);
			Page<Map<String, String>> page = lu.search(new String[]{"99"},field,1,10);
			List<Map<String,String>> list2 = page.getPageList();
			for(int i = 0;i < list2.size(); i++){
				System.out.println("ID:"+list2.get(i).get(LuceneType.ALL_ID) +"；标题：" + list2.get(i).get(LuceneType.ALL_TITLE) + "；内容："+ list2.get(i).get(LuceneType.ALL_CONTENT));
			}
		}catch(Exception e){
			e.printStackTrace();
		}
	}
}
