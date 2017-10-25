package com.kxf.mysqlmanage.test;

import java.sql.SQLException;
import java.util.List;

import com.kxf.mysqlmanage.DBWhereBuilder;
import com.kxf.mysqlmanage.LogUtils;
import com.kxf.mysqlmanage.MySqlManagerException;
import com.kxf.mysqlmanage.SimpleDBManage;
import com.kxf.mysqlmanage.LogUtils.LogListener;
import com.kxf.mysqlmanage.LogUtils.LogType;

public class Test {
	public static void main(String[] args) throws SQLException, MySqlManagerException {
//		LogUtils.setLogType(LogType.DEBUG);
//		LogUtils.setListener(new LogListener() {
//			
//			@Override
//			public void i(String i) {
//				System.out.println(i);
//			}
//			
//			@Override
//			public void e(String e) {
//				System.err.println(e);
//			}
//
//			@Override
//			public void d(String d) {
//				System.out.println(d);
//			}
//
//			@Override
//			public void w(String w) {
//				System.err.println(w);
//			}
//		});
		SimpleDBManage db = new SimpleDBManage();
		db.setLogUtils(new LogUtils(new LogListener() {
			
			@Override
			public void i(String i) {
				System.out.println(i);
			}
			
			@Override
			public void e(String e) {
				System.err.println(e);
			}

			@Override
			public void d(String d) {
				System.out.println(d);
			}

			@Override
			public void w(String w) {
				System.err.println(w);
			}
		}));
		TestObject to = new TestObject();
		to.setId(102);
		to.setNameSub("lili511111111111111111111111111111111");
//		to.setShenggao(175.55);
		to.setShg(175.55);
		to.setAge(19);
		System.out.println(to.getClass().getSimpleName());
		db.save(to);
//		db.saveOrUpdate(to);
//		System.out.println("====" + db.delete(TestObject.class, null));
//		System.out.println("----" + db.dropTable(TestObject.class));
//		System.out.println("----" + db.dropTable(TbColumnInfo.class));
		
//		to.setId(119);
//		to.setName("haha119");
//		to.setShenggao(119.01);
//		DBWhereBuilder dbw1 = new DBWhereBuilder("id", "=", "119");
//		db.update(to, dbw1 );
//		
//		db.dropTable(TestObject.class);
//		
		DBWhereBuilder dbw = new DBWhereBuilder("id", "<", "106");
		dbw.or("shg", "=", "175.55");
		List<TestObject> ls1 = db.find(TestObject.class, dbw, false, "shg", "id");
		System.out.println("ls1=" + ls1);
		
//		List<String> keys = db.getPrimaryKey(TestObject.class);
//		System.out.println("keys=" + keys);
//		
//		db.getAllColumnDB(TestObject.class);
//		db.getAllColumnObj(to);
		List<TestObject> ls = db.findAll(TestObject.class);
		System.out.println("ls["+ls.size()+"]=" + ls);
	}
}
