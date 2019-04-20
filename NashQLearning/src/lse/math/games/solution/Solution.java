package lse.math.games.solution;

import lse.math.games.Rational;
import lse.math.games.matrix.Equilibrium;

public class Solution {
	private double[] probVec1;
	private double[] probVec2;
	private double payoff1;
	private double payoff2;
	
	public Solution(double[] probVec1,double[] probVec2,double payoff1,double payoff2) {
		this.probVec1=probVec1;
		this.probVec2=probVec2;
		this.payoff1=payoff1;
		this.payoff2=payoff2;
	}
	
	public Solution(Equilibrium equilibrium){
		payoff1=equilibrium.payoff1.doubleValue();
		payoff2=equilibrium.payoff2.doubleValue();
		
		probVec1=new double[equilibrium.probVec1.length];
		probVec2=new double[equilibrium.probVec2.length];
		int index=0;
		for(Rational coord:equilibrium.probVec1) {
			probVec1[index++]=coord.doubleValue();
		}
		index=0;
		for(Rational coord:equilibrium.probVec2) {
			probVec2[index++]=coord.doubleValue();
		}
	}

	public double[] getProbVec1() {
		return probVec1;
	}

	public double[] getProbVec2() {
		return probVec2;
	}

	public double getPayoff1() {
		return payoff1;
	}

	public double getPayoff2() {
		return payoff2;
	}
}
