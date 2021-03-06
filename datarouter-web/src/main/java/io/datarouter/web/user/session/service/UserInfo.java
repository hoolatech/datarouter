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

import java.util.Optional;
import java.util.Set;

public interface UserInfo{

	Optional<? extends SessionBasedUser> getUserByUsername(String username);
	Optional<? extends SessionBasedUser> getUserByToken(String token);
	Optional<? extends SessionBasedUser> getUserById(Long id);

	Set<Role> getRolesByUsername(String username);
	Set<Role> getRolesByToken(String token);
	Set<Role> getRolesById(Long id);

	default Boolean hasRoleByUsername(String username, Role role){
		return getRolesByUsername(username).contains(role);
	}

	default Boolean hasRoleByToken(String token, Role role){
		return getRolesByToken(token).contains(role);
	}

	default Boolean hasRoleById(Long id, Role role){
		return getRolesById(id).contains(role);
	}

}
