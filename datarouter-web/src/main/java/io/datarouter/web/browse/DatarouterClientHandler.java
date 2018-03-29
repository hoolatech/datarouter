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
package io.datarouter.web.browse;

import javax.inject.Inject;

import io.datarouter.web.handler.BaseHandler;
import io.datarouter.web.handler.mav.Mav;
import io.datarouter.web.handler.mav.imp.MessageMav;

public class DatarouterClientHandler extends BaseHandler{

	@Inject
	private DatarouterClientWebInspectorRegistry datarouterClientWebInspectorRegistry;

	@Handler
	public Mav inspectClient(String clientType){
		return datarouterClientWebInspectorRegistry.get(clientType)
				.map(inspector -> inspector.inspectClient(params))
				.orElseGet(() -> new MessageMav("Can't inspect " + clientType + ". Make sure it registers a "
						+ DatarouterClientWebInspector.class.getSimpleName() + " in "
						+ DatarouterClientWebInspectorRegistry.class.getSimpleName() + "."));
	}

}
