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
package net.decitrig.sr4.tests;


/** Contains static functions for calculating dice pool probability.
 *
 * @author sliderule
 */
public class RollerMath {
    private static final double HIT_PROB = 1.0/3.0;

    private static double choose(long n, long k){
        if (k > n)
            return 0;
        if (k > n/2)
            k = n-k;

        double accum = 1;
        for (int i = 1; i <= k; i++)
            accum = accum * (n - k + i) / i;
        return accum;
    }

    /** Compute binomial distribution for independent trials. If cumulative
     * mode is indicated, compute probability of 0 to <code>success</code>
     * successes.
     * @param success number of successes to compute.
     * @param trials number of independent trials.
     * @param p probability of success on a given trial.
     * @param accum if <code>true</code>, compute cumulative probability.
     * @return computed probability.
     */
    private static double binomial(int success, int trials, 
            double p, boolean accum){
        if (!accum){
            double c = choose(trials, success);
            double prob =  c * Math.pow(p, success);
            prob *= Math.pow(1-p, trials-success);
            return prob;
        }
        else {
            double prob = 0.0;
            for(int i = 0; i < success; i++){
                prob += binomial(i, trials, p, false);
            }
            return 1 - prob;
        }
    }

    /** Find the probability of meeting or exceeding a given threshold with a
     * given dice pool.
     * @param dice number of dice in pool.
     * @param thres threshold to beat.
     * @return probability of sucess
     */
    public static double successProb(int dice, int thres){
        return binomial(thres, dice, HIT_PROB, true);
    }

    /**Find the largest threshold that has a > 80% chance of success
     *
     * @param dice number of dice in the pool
     * @return threshold
     */
    public static int findEighty(int dice){
        int i;
        for(i = 1; i <= dice; i++){
            double prob = successProb(dice, i);
            if(prob < .8)
                break;
        }
        return i-1;
    }

    /**Find expected # of hits for a given dice pool by computing
     * probability-weighted sum of hits.
     *
     * @param dice number of dice in the pool.
     * @return expected number of hits.
     */
    public static int expectedHits(int dice){
        double prob = 0;
        for (int i = 1; i <= dice; i++){
            prob += i * binomial(i, dice, HIT_PROB, false);
        }
        return (int)prob;
    }

    /**Compute probabiltity of a non-critical glitch.
     *
     * @param dice number of dice in pool.
     * @return probability of glitch.
     */
    public static double glitchProb(int dice){
        int ones = (int)Math.ceil(dice/2.0);
        double prob = 0.;
        for (int i = ones; i <= dice; i++){
            prob += choose(dice, i) * Math.pow(1./6., i)*Math.pow(5./6.,dice-i);
        }
        return prob;
    }

    /**Compute probability of a critical glitch.
     *
     * @param dice number of dice in pool.
     * @return probability of critical glitch.
     */
    public static double critProb(int dice){
        int ones = (int)Math.ceil(dice/2.0);
        double prob = 0.;
        for (int i = ones; i <= dice; i++){
            prob += choose(dice, i) * Math.pow(1./6., i)*Math.pow(0.5, dice-i);
        }
        return prob;
    }
}
