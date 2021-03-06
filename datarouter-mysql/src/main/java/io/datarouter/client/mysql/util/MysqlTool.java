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
package io.datarouter.client.mysql.util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

import io.datarouter.client.mysql.connection.MysqlConnectionPoolFactory.MysqlConnectionPool;
import io.datarouter.client.mysql.field.MysqlFieldCodec;
import io.datarouter.client.mysql.field.codec.factory.MysqlFieldCodecFactory;
import io.datarouter.model.databean.Databean;
import io.datarouter.model.exception.DataAccessException;
import io.datarouter.model.field.Field;
import io.datarouter.model.index.IndexEntry;
import io.datarouter.model.key.primary.PrimaryKey;
import io.datarouter.model.serialize.fielder.DatabeanFielder;
import io.datarouter.storage.serialize.fieldcache.DatabeanFieldInfo;
import io.datarouter.util.lang.ReflectionTool;
import io.datarouter.util.string.StringTool;

public class MysqlTool{

	private static final String TABLE_CATALOG = "TABLE_CAT";

	public static Connection openConnection(String hostname, int port, String database, String user, String password){
		try{
			String url = "jdbc:mysql://" + hostname + ":" + port + "/" + StringTool.nullSafe(database) + "?user="
					+ user + "&password=" + password;
			return DriverManager.getConnection(url);
		}catch(Exception e){
			throw new RuntimeException("failed to connect hostname=" + hostname + " port=" + port + " database="
					+ database + " user=" + user, e);
		}
	}

	public static List<String> showTables(Connection connection, String schemaName){
		try{
			List<String> tableNames = new ArrayList<>();
			ResultSet rs = connection.getMetaData().getTables(schemaName, schemaName, "%", null);
			while(rs.next()){
				tableNames.add(rs.getString(3));
			}
			return tableNames;
		}catch(SQLException e){
			throw new RuntimeException(e);
		}
	}

	public static List<String> showDatabases(Connection connection){
		try{
			ResultSet rs = connection.getMetaData().getCatalogs();
			List<String> catalogs = new ArrayList<>();
			while(rs.next()){
				catalogs.add(rs.getString(TABLE_CATALOG));
			}
			return catalogs;
		}catch(SQLException e){
			throw new RuntimeException(e);
		}
	}

	public static <PK extends PrimaryKey<PK>,D extends Databean<PK,D>,F extends DatabeanFielder<PK,D>>
			List<PK> selectPrimaryKeys(MysqlFieldCodecFactory fieldCodecFactory, DatabeanFieldInfo<PK,D,F> fieldInfo,
					PreparedStatement ps){
		try{
			ps.execute();
			ResultSet rs = ps.getResultSet();
			List<PK> primaryKeys = new ArrayList<>();
			while(rs.next()){
				PK primaryKey = fieldSetFromMysqlResultSetUsingReflection(fieldCodecFactory, ReflectionTool
						.supplier(fieldInfo.getPrimaryKeyClass()), fieldInfo.getPrimaryKeyFields(), rs);
				primaryKeys.add(primaryKey);
			}
			return primaryKeys;
		}catch(Exception e){
			throw new DataAccessException(ps.toString(), e);
		}
	}

	public static <PK extends PrimaryKey<PK>,D extends Databean<PK,D>,F extends DatabeanFielder<PK,D>>
			List<D> selectDatabeans(MysqlFieldCodecFactory fieldCodecFactory, DatabeanFieldInfo<PK,D,F> fieldInfo,
					PreparedStatement ps){
		try{
			ps.execute();
			ResultSet rs = ps.getResultSet();
			List<D> databeans = new ArrayList<>();
			while(rs.next()){
				D databean = fieldSetFromMysqlResultSetUsingReflection(fieldCodecFactory,
						fieldInfo.getDatabeanSupplier(), fieldInfo.getFields(), rs);
				databeans.add(databean);
			}
			return databeans;
		}catch(Exception e){
			String message = "error executing sql:" + ps;
			throw new DataAccessException(message, e);
		}
	}

	public static <PK extends PrimaryKey<PK>,
			D extends Databean<PK,D>,
			IK extends PrimaryKey<IK>,
			IE extends IndexEntry<IK,IE,PK,D>,
			IF extends DatabeanFielder<IK,IE>>
	List<IK> selectIndexEntryKeys(MysqlFieldCodecFactory fieldCodecFactory, DatabeanFieldInfo<IK,IE,IF> fieldInfo,
			PreparedStatement ps){
		try{
			ps.execute();
			ResultSet rs = ps.getResultSet();
			List<IK> keys = new ArrayList<>();
			while(rs.next()){
				IK key = fieldSetFromMysqlResultSetUsingReflection(fieldCodecFactory, ReflectionTool.supplier(
						fieldInfo.getPrimaryKeyClass()), fieldInfo.getPrimaryKeyFields(), rs);
				keys.add(key);
			}
			return keys;
		}catch(Exception e){
			String message = "error executing sql:" + ps;
			throw new DataAccessException(message, e);
		}
	}

	public static int update(PreparedStatement statement){
		try{
			return statement.executeUpdate();
		}catch(SQLException e){
			String message = "error executing sql:" + statement;
			throw new DataAccessException(message, e);
		}
	}

	public static <F> F fieldSetFromMysqlResultSetUsingReflection(MysqlFieldCodecFactory fieldCodecFactory,
			Supplier<F> supplier, List<Field<?>> fields, ResultSet rs){
		F targetFieldSet = supplier.get();
		for(MysqlFieldCodec<?> field : fieldCodecFactory.createCodecs(fields)){
			field.fromMysqlResultSetUsingReflection(targetFieldSet, rs);
		}
		return targetFieldSet;
	}

	public static void execute(MysqlConnectionPool connectionPool, String sql){
		try(Connection connection = connectionPool.checkOut()){
			connection.createStatement().execute(sql);
		}catch(SQLException e){
			throw new RuntimeException(sql, e);
		}
	}

}
