package net.decitrig.sr4.tests;

public abstract class SR4Test {
	private String name;
	protected int pool, thres;


	public String getName() {
		return name;
	}


	public int getPool() {
		return pool;
	}
	

	public int getThres() {
		return thres;
	}
	public void setThres(int thres) {
		this.thres = thres;
	}


	public void setName(String name) {
		this.name = name;
	}


	public abstract void rollTest();
	public abstract Glitch getGlitch();
	public abstract int getHits();


	public abstract SR4Test copyAndReroll();

}
