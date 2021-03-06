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
package io.datarouter.web.handler.documentation;

public class DocumentedParameter{

	public String name;
	public String type;
	public String example;
	public Boolean required;
	public Boolean requestBody;
	public String description;

	public String getName(){
		return name;
	}

	public String getType(){
		return type;
	}

	public String getExample(){
		return example;
	}

	public Boolean getRequired(){
		return required;
	}

	public Boolean getRequestBody(){
		return requestBody;
	}

	public String getDescription(){
		return description;
	}

}