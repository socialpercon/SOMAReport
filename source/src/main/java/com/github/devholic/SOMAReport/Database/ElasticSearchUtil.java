package com.github.devholic.SOMAReport.Database;

import java.util.Map;

import org.elasticsearch.action.delete.DeleteResponse;
import org.elasticsearch.action.get.GetResponse;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.client.Client;
import org.elasticsearch.node.Node;
import org.elasticsearch.node.NodeBuilder;
import org.junit.Assert;
import org.junit.Test;

public class ElasticSearchUtil {
	
	Node node;
	Client client;
	
	/**************************************************************************************
	 * ElasticSearch를 local에서 돌릴 때 사용하는 index,search,delete 이다.
	 * 하지만, 우리는 elastic search demon 을 따로 돌리기 때문에 transport를 이용하기로 하여
	 * 이 유틸은 현재 사용하지 않는다
	 **************************************************************************************/
	
	public ElasticSearchUtil() {
		try {
			
			node = NodeBuilder.nodeBuilder().node();
			client = node.client();
			
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Test
	public void index(Map<String, Object> jsonobject,String _id){

		//indexing
		IndexResponse response = client.prepareIndex("somareport", "report", _id)
		        .setSource(jsonobject)
		        .execute()
		        .actionGet();
		Assert.assertNotNull(response);
	}
	
	@Test
	public Map<String, Object> search(String _id){
		
		//search
		GetResponse response = client.prepareGet("somareport", "report", _id)
		        .execute()
		        .actionGet();
		Assert.assertNotNull(response.getId());
		System.out.println(response.getId());
		
		//출력한다 
		Map<String, Object> result_json = response.getSource();
//		System.out.println(result_json.toString());
		
		return result_json;
	}
	
	@Test
	public void delete(String _id){
		
		DeleteResponse response = client.prepareDelete("somareport", "report", _id)
		        .execute()
		        .actionGet();
		Assert.assertNotNull(response);
		
	}
}
