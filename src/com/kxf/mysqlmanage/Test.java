package com.kxf.mysqlmanage;

import java.util.List;

import com.kxf.mysqlmanage.LogUtils.LogListener;
import com.kxf.mysqlmanage.LogUtils.LogType;

public class Test {
	public static void main(String[] args) {
		LogUtils.setLogType(LogType.DEBUG);
		LogUtils.setListener(new LogListener() {
			
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
		});
		SimpleDBManage db = new SimpleDBManage();
		TestObject to = new TestObject();
//		to.setId(100);
//		to.setName("lili3");
//		to.setShenggao(165.55);
//		System.out.println(to.getClass().getSimpleName());
//		db.save(to);
		
//		to.setId(119);
//		to.setName("haha119");
//		to.setShenggao(119.01);
//		DBWhereBuilder dbw1 = new DBWhereBuilder("id", "=", "119");
//		db.update(to, dbw1 );
//		
//		List<TestObject> ls = db.findAll(TestObject.class);
//		System.out.println("ls=" + ls);
//		
//		DBWhereBuilder dbw = new DBWhereBuilder("id", ">", "103");
//		dbw.or("shenggao", "=", "175.55");
//		List<TestObject> ls1 = db.find(TestObject.class, dbw, false, "shenggao", "id");
//		System.out.println("ls1=" + ls1);
		
		List<String> keys = db.getPrimaryKey(TestObject.class);
		System.out.println("keys=" + keys);
		
		db.getAllColumnDB(TestObject.class);
	}
}
