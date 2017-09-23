package com.kxf.mysqlmanage.test;

import com.kxf.mysqlmanage.annotations.DBAnnotation;

public class TestObject {
	@DBAnnotation(isKey = true)
	private int id;

	@DBAnnotation(length = 20)
	private String name;
	
	private String nameSub;

	// private double shenggao;

	private double shg;

	private int age;

	public double getShg() {
		return shg;
	}

	public void setShg(double shg) {
		this.shg = shg;
	}

	public int getAge() {
		return age;
	}

	public void setAge(int age) {
		this.age = age;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getNameSub() {
		return nameSub;
	}

	public void setNameSub(String nameSub) {
		this.nameSub = nameSub;
	}

	@Override
	public String toString() {
		return "TestObject [id=" + id + ", name=" + name + ", nameSub="
				+ nameSub + ", shg=" + shg + ", age=" + age + "]";
	}
}
