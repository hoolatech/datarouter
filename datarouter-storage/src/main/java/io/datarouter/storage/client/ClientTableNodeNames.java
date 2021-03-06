/**
 * Copyright © 2009 HotPads (admin@hotpads.com)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package io.datarouter.storage.client;

public class ClientTableNodeNames{

	private final String clientName;
	private final String tableName;
	private final String nodeName;

	public ClientTableNodeNames(String clientName, String tableName, String nodeName){
		this.clientName = clientName;
		this.tableName = tableName;
		this.nodeName = nodeName;
	}

	public String getClientName(){
		return clientName;
	}

	public String getTableName(){
		return tableName;
	}

	public String getNodeName(){
		return nodeName;
	}

	@Override
	public int hashCode(){
		final int prime = 31;
		int result = 1;
		result = prime * result + ((clientName == null) ? 0 : clientName.hashCode());
		result = prime * result + ((nodeName == null) ? 0 : nodeName.hashCode());
		result = prime * result + ((tableName == null) ? 0 : tableName.hashCode());
		return result;
	}


	@Override
	public boolean equals(Object obj){
		if(this == obj){
			return true;
		}
		if(obj == null){
			return false;
		}
		if(getClass() != obj.getClass()){
			return false;
		}
		ClientTableNodeNames other = (ClientTableNodeNames)obj;
		if(clientName == null){
			if(other.clientName != null){
				return false;
			}
		}else if(!clientName.equals(other.clientName)){
			return false;
		}
		if(nodeName == null){
			if(other.nodeName != null){
				return false;
			}
		}else if(!nodeName.equals(other.nodeName)){
			return false;
		}
		if(tableName == null){
			if(other.tableName != null){
				return false;
			}
		}else if(!tableName.equals(other.tableName)){
			return false;
		}
		return true;
	}


	@Override
	public String toString(){
		return "ClientTableNodeNames [clientName=" + clientName + ", tableName=" + tableName + ", nodeName=" + nodeName
				+ "]";
	}

}
