package net.decitrig.sr4.math;

import java.util.Random;

public class SuccessTest implements SR4Test {
	protected int[] results;
	
	private static Random rand = new Random();

	public SuccessTest(int pool){
		results = new int[pool];
		for(int die = 0; die < pool; die++){
			results[die] = rand.nextInt(6)+1;
		}
	}
	
	private int countOnes(){
		int count = 0;
		for(int i : results){
			if(i == 1)
				count++;
		}
		return count;
	}

	public Glitch getGlitch() {
		int ones = countOnes();
		int hits = getHits();
		int poolSize = results.length;
		if(ones >= (poolSize/2.0)){
			if(hits == 0)
				return Glitch.Crit;
			return Glitch.Glitch;
		}
		return Glitch.None;
	}


	public int getHits() {
		int count = 0;
		for (int res : results){
			if (res >= 5)
				count++;
		}
		return count;
	}
}
