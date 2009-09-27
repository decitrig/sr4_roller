package net.decitrig.sr4.tests;

import java.util.Random;

public class SuccessTest extends SR4Test {
	private int[] results;

	private static Random rand = new Random();

	public SuccessTest(int pool) {
		this.pool = pool;
		rollTest();
	}
	
	public SuccessTest(SuccessTest test){
		this.pool = test.getPool();
		this.thres = test.getThres();
		rollTest();
	}

	public SuccessTest(int pool, int thres) {
		this(pool);
		this.thres = thres;
	}

	@Override
	public void rollTest() {
		results = new int[pool];
		for (int die = 0; die < pool; die++) {
			results[die] = rand.nextInt(6) + 1;
		}
	}

	private int countOnes() {
		int count = 0;
		for (int i : results) {
			if (i == 1)
				count++;
		}
		return count;
	}

	public Glitch getGlitch() {
		int ones = countOnes();
		int hits = getHits();
		int poolSize = results.length;
		if (ones >= (poolSize / 2.0)) {
			if (hits == 0)
				return Glitch.Crit;
			return Glitch.Glitch;
		}
		return Glitch.None;
	}

	public int getHits() {
		int count = 0;
		for (int res : results) {
			if (res >= 5)
				count++;
		}
		return count;
	}

	@Override
	public SR4Test copyAndReroll() {
		return (SR4Test)(new SuccessTest(this.pool, this.thres));
	}
}
