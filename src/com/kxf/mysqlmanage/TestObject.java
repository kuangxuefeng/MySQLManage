package com.kxf.mysqlmanage;

public class TestObject {
	private int id;
	private String name;
	private double shenggao;
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
	public double getShenggao() {
		return shenggao;
	}
	public void setShenggao(double shenggao) {
		this.shenggao = shenggao;
	}
	@Override
	public String toString() {
		return "TestObject [id=" + id + ", name=" + name + ", shenggao="
				+ shenggao + "]";
	}
}
