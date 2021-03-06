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
package io.datarouter.web.user.session.service;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

import javax.inject.Inject;
import javax.inject.Singleton;

import io.datarouter.web.user.cache.DatarouterUserByIdCache;
import io.datarouter.web.user.cache.DatarouterUserByUserTokenCache;
import io.datarouter.web.user.cache.DatarouterUserByUsernameCache;
import io.datarouter.web.user.databean.DatarouterUser;

@Singleton
public class DatarouterUserInfo implements UserInfo{

	@Inject
	private DatarouterUserByUsernameCache datarouterUserByUsernameCache;
	@Inject
	private DatarouterUserByUserTokenCache datarouterUserByUserTokenCache;
	@Inject
	private DatarouterUserByIdCache datarouterUserByIdCache;

	@Override
	public Optional<DatarouterUser> getUserByUsername(String username){
		return datarouterUserByUsernameCache.get(username);
	}

	@Override
	public Optional<DatarouterUser> getUserByToken(String token){
		return datarouterUserByUserTokenCache.get(token);
	}

	@Override
	public Optional<DatarouterUser> getUserById(Long id){
		return datarouterUserByIdCache.get(id);
	}

	private Set<Role> getRolesFromUser(Optional<? extends DatarouterUser> user){
		return user.map(DatarouterUser::getRoles)
				.map(HashSet::new)
				.orElseGet(HashSet::new);
	}

	@Override
	public Set<Role> getRolesByUsername(String username){
		return getRolesFromUser(getUserByUsername(username));
	}

	@Override
	public Set<Role> getRolesByToken(String token){
		return getRolesFromUser(getUserByToken(token));
	}

	@Override
	public Set<Role> getRolesById(Long id){
		return getRolesFromUser(getUserById(id));
	}

}
