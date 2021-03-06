package net.decitrig.sr4.tests;
/*
BSD License for sliderule's dice roller

Copyright (c) 2009 Ryan W Sims. All rights reserved.

Redistribution and use in source and binary forms, with or without 
modification, are permitted provided that the following conditions are met:

 o Redistributions of source code must retain the above copyright notice, 
   this list of conditions and the following disclaimer. 
    
 o Redistributions in binary form must reproduce the above copyright notice, 
   this list of conditions and the following disclaimer in the documentation 
   and/or other materials provided with the distribution. 
    
 o Neither the name of sliderule's dice roller nor the names of 
   its contributors may be used to endorse or promote products derived 
   from this software without specific prior written permission. 
    
THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" 
AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, 
THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR 
PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR 
CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, 
EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, 
PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; 
OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, 
WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR 
OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, 
EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */


import java.util.ArrayList;

/**
 * Extended test class
 */
public class ExtendedTest extends SR4Test {
	ArrayList<SuccessTest> rollList = new ArrayList<SuccessTest>();
	Glitch glitch = Glitch.None;
	int hits;

	public ExtendedTest(int pool, int thres) {
		this.pool = pool;
		this.thres = thres;
		rollTest();
	}
	
	public ExtendedTest(ExtendedTest test){
		this.pool = test.getPool();
		this.thres = test.getThres();
		rollTest();
	}

	@Override
	public void rollTest() {
		rollList.clear();
		int poolSize = this.pool;
		int accumHits = 0;
		while (accumHits < thres) {
			if (poolSize == 0)
				break;
			SuccessTest t = new SuccessTest(poolSize);
			Glitch tGlitch = t.getGlitch();
			if (tGlitch.ordinal() > glitch.ordinal())
				glitch = tGlitch;
			rollList.add(t);
			if (tGlitch == Glitch.Crit)
				break;
			accumHits += t.getHits();
			poolSize--;
		}
		this.hits = accumHits;
	}

	@Override
	public Glitch getGlitch() {
		return glitch;
	}

	@Override
	public int getHits() {
		return this.hits;
	}

	/**
	 * Count how many rolls the extended test took. The test is limited by the
	 * number of dice in the pool, and could be stopped early either by success
	 * or a critical glitch.
	 * 
	 * @return number of rolls
	 */
	public int getRolls() {
		return rollList.size();
	}

	@Override
	public SR4Test copyAndReroll() {
		return (SR4Test)(new ExtendedTest(this.pool, this.thres));
	}
}
