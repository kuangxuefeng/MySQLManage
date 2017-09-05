package com.kxf.mysqlmanage;

import java.util.ArrayList;
import java.util.List;

public class DBWhereBuilder {
	private List<DBWhere> dbws;
	
	public List<DBWhere> value(){
		return dbws;
	}
	
	public DBWhereBuilder(String column, String symbol, Object value) {
		dbws = new ArrayList<DBWhereBuilder.DBWhere>();
		DBWhere dbw = new DBWhere(column, symbol, value, "");
		dbws.add(dbw);
	}
	
	public DBWhereBuilder and(String column, String symbol, Object value){
		DBWhere dbw = new DBWhere(column, symbol, value, "and");
		dbws.add(dbw);
		return this;
	}
	
	public DBWhereBuilder or(String column, String symbol, Object value){
		DBWhere dbw = new DBWhere(column, symbol, value, "or");
		dbws.add(dbw);
		return this;
	}
	
	public String getWhereSql() {
		String re = " where";
		for(DBWhere dbw : dbws){
			if (dbw.value instanceof String) {
				re = re + " " + dbw.Relationship + " " + dbw.column + dbw.symbol + "\'" + dbw.value + "\'";
			}else {
				re = re + " " + dbw.Relationship + " " + dbw.column + dbw.symbol + dbw.value;
			}
		}
		return re;
	}
	
	public class DBWhere{
		public String column;
		public String symbol;
		public Object value;
		public String Relationship;//与前一个条件的关系是与还是或
		public DBWhere(String column, String symbol, Object value, String Relationship) {
			this.column = column;
			this.symbol = symbol;
			this.value = value;
			this.Relationship = Relationship;
		}
	}
}
