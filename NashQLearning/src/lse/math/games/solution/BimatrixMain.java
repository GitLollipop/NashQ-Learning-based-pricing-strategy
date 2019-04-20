package lse.math.games.solution;

import java.util.Iterator;

import lse.math.games.Rational;
import lse.math.games.lrs.LrsAlgorithm;
import lse.math.games.matrix.BimatrixSolver;
import lse.math.games.matrix.Equilibria;
import lse.math.games.matrix.Equilibrium;

public class BimatrixMain {
	public Rational[][] a;
	public Rational[][] b;

	// 返回第order个纳什均衡解,供外部调用
	public Solution getNashEquilibriaInOrder(double[][] A, double[][] B, int order) {
		Equilibria equilibria = getAllEquilibria(A, B);
		Equilibrium equilibrium = equilibria.getEquilibriaInOrder(order);
		return new Solution(equilibrium);
	}

	// 返回两个agent各自payoff最高的纳什均衡（若两个对应的不是同一个纳什均衡，则组成新的纳什均衡解并返回）
	public Solution getTwoBestPayoffEquilibria(double[][] A, double[][] B) {
		// find all NE
		Equilibria equilibria = getAllEquilibria(A, B);

		// intialize probVec1,probVec2,payoff1,payoff2
		Equilibrium e = equilibria.getEquilibriaInOrder(1);
		Rational[] probVec1 = e.probVec1;
		Rational[] probVec2 = e.probVec2;
		double payoff1 = e.payoff1.doubleValue();
		double payoff2 = e.payoff2.doubleValue();

		// best-expected payoff of agent1 and agent2 respectively
		Iterator<Equilibrium> iterator = equilibria.iterator();
		while (iterator.hasNext()) {
			Equilibrium equilibrium = iterator.next();
			double p1 = equilibrium.payoff1.doubleValue();
			Rational[] prob1 = equilibrium.probVec1;
			double p2 = equilibrium.payoff2.doubleValue();
			Rational[] prob2 = equilibrium.probVec2;
			if (p1 > payoff1) {
				payoff1 = p1;
				probVec1 = prob1;
			}
			if (p2 > payoff2) {
				payoff2 = p2;
				probVec2 = prob2;
			}
		}

		return new Solution(transformRationalToDouble(probVec1), transformRationalToDouble(probVec2), payoff1, payoff2);
	}

	// 返回agent1与agent2的payoff相加和最高的纳什均衡解
	public Solution getBestPayOffSumEquilibria(double[][] A, double[][] B) {
		// find all NE
		Equilibria equilibria = getAllEquilibria(A, B);

		// initialize payoff1,payoff2,probVec1,probVec2
		Equilibrium e = equilibria.getEquilibriaInOrder(1);
		double payoff1=e.payoff1.doubleValue();
		double payoff2=  e.payoff2.doubleValue();
		Rational[] probVec1 = e.probVec1;
		Rational[] probVec2 = e.probVec2;

		// sum of best-expected payoff of agent1 and agent2
		Iterator<Equilibrium> iterator = equilibria.iterator();
		while (iterator.hasNext()) {
			Equilibrium equilibrium = iterator.next();
			double p1 = equilibrium.payoff1.doubleValue();
			Rational[] prob1 = equilibrium.probVec1;
			double p2 = equilibrium.payoff2.doubleValue();
			Rational[] prob2 = equilibrium.probVec2;
			if(p1+p2>payoff1+payoff2) {
				payoff1=p1;
				payoff2=p2;
				probVec1=prob1;
				probVec2=prob2;
			}
		}
		
		return new Solution(transformRationalToDouble(probVec1), transformRationalToDouble(probVec2), payoff1, payoff2);
	}

	// 返回所有纳什均衡解
	public Equilibria getAllEquilibria(double[][] A, double[][] B) {
		transformInput(A, B);
		LrsAlgorithm lrs = new LrsAlgorithm();
		BimatrixSolver bs = new BimatrixSolver();
		Equilibria equilibria = bs.findAllEq(lrs, a, b);
		return equilibria;
	}

	// 将double[][]形式的输入矩阵转换为Rational[][]
	public void transformInput(double[][] A, double[][] B) {
		int row = A.length;
		int col = A[0].length;
		a = new Rational[row][col];
		b = new Rational[row][col];
		for (int i = 0; i < row; i++) {
			for (int j = 0; j < col; j++) {
				a[i][j] = Rational.valueOf(A[i][j]);
				b[i][j] = Rational.valueOf(B[i][j]);
			}
		}
	}

	// 将Rational[]形式的数组转换为double[]
	public double[] transformRationalToDouble(Rational[] arr) {
		double[] result = new double[arr.length];
		for (int i = 0; i < arr.length; i++) {
			result[i] = arr[i].doubleValue();
		}
		return result;
	}

	public void printSolution(Solution solution) {
		double[] probVec1 = solution.getProbVec1();
		double[] probVec2 = solution.getProbVec2();
		double payoff1 = solution.getPayoff1();
		double payoff2 = solution.getPayoff2();
		for (int i = 0; i < probVec1.length; i++) {
			System.out.print(probVec1[i] + " ");
		}
		System.out.println("payoff1=" + payoff1);
		for (int i = 0; i < probVec2.length; i++) {
			System.out.print(probVec2[i] + " ");
		}
		System.out.println("payoff2=" + payoff2);
	}

	public static void main(String[] args) {
		double[][] A = new double[10][10];
		double[][] B = new double[10][10];
		String str1 = "0.0,0.0 0.0,0.0 0.0,0.0 0.0,0.0 0.0,0.0 0.0,0.0 5017.703281395293,17659.871792260485 0.0,0.0 0.0,0.0 1847.4563310228073,7338.905879691402";
		String str2 = "0.0,0.0 0.0,0.0 0.0,0.0 0.0,0.0 0.0,0.0 0.0,0.0 0.0,0.0 4475.714980535099,13459.037986158688 0.0,0.0 0.0,0.0";
		String str3 = "1701.7,2412.9 0.0,0.0 0.0,0.0 1300.0,1680.0 1350.0,2070.0 0.0,0.0 0.0,0.0 0.0,0.0 0.0,0.0 0.0,0.0";
		String str4 = "0.0,0.0 0.0,0.0 0.0,0.0 1925.0,1575.0 0.0,0.0 0.0,0.0 0.0,0.0 0.0,0.0 1785.0,4165.0 0.0,0.0";
		String str5 = "6438.9591,7910.9982 0.0,0.0 0.0,0.0 0.0,0.0 0.0,0.0 2160.0,2860.0 2520.0,2860.0 0.0,0.0 0.0,0.0 2295.0,4655.0";
		String str6 = "0.0,0.0 2695.0,765.0 0.0,0.0 0.0,0.0 0.0,0.0 0.0,0.0 0.0,0.0 0.0,0.0 0.0,0.0 0.0,0.0";
		String str7 = "0.0,0.0 6373.07513828134,14246.226607037876 0.0,0.0 0.0,0.0 0.0,0.0 0.0,0.0 0.0,0.0 0.0,0.0 0.0,0.0 5352.810169846107,11396.148210990143";
		String str8 = "0.0,0.0 0.0,0.0 0.0,0.0 2550.0,2310.0 0.0,0.0 0.0,0.0 0.0,0.0 7788.99544275,12381.97015575 4132.65,6965.85 4425.0,3895.0";
		String str9 = "0.0,0.0 0.0,0.0 0.0,0.0 0.0,0.0 0.0,0.0 0.0,0.0 4730.85,7242.95 0.0,0.0 0.0,0.0 0.0,0.0";
		String str10 = "0.0,0.0 3541.85,4469.85 0.0,0.0 0.0,0.0 3610.0,2790.0 0.0,0.0 0.0,0.0 0.0,0.0 0.0,0.0 0.0,0.0";
		String strs[] = { str1, str2, str3, str4, str5, str6, str7, str8, str9, str10 };
		for (int i = 0; i < strs.length; i++) {
			String[] num = strs[i].split(" ");
			for (int j = 0; j < num.length; j++) {
				double d1 = Double.valueOf(num[j].split(",")[0]);
				double d2 = Double.valueOf(num[j].split(",")[1]);
				A[i][j] = d1;
				B[i][j] = d2;
			}
		}
		BimatrixMain bm = new BimatrixMain();

		// print all NE
		Equilibria equilibria = bm.getAllEquilibria(A, B);
		equilibria.print(true);
		System.out.println("******************************************************************");

		// print NE of best-expected payoff of two agent
		Solution solution = bm.getTwoBestPayoffEquilibria(A, B);
		bm.printSolution(solution);
		System.out.println("******************************************************************");
		
		//print NE of best-expected payoff-sum of two agent
		Solution solution1=bm.getBestPayOffSumEquilibria(A, B);
		bm.printSolution(solution1);

		/*
		 * double[][] A = { { 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 295.0, 0.0, 325.0}, {
		 * 0.0, 0.0, 0.0, 0.0, 2685.75, 0.0, 0.0, 0.0, 0.0, 1352.25 }, { 0.0, 1275.0,
		 * 1275.0, 0.0, 0.0, 0.0, 1250.0, 0.0, 0.0, 0.0 }, { 0.0, 0.0, 0.0, 0.0, 0.0,
		 * 0.0, 0.0, 9027.337054978027, 0.0, 0.0 }, { 0.0, 2160.0, 0.0, 0.0,
		 * 5257.716201994619, 0.0, 0.0, 0.0, 2882.25, 0.0 }, { 0.0, 3888.5, 0.0, 0.0,
		 * 0.0, 0.0, 0.0, 0.0, 4557.8591025, 0.0}, { 2925.0, 0.0, 0.0, 0.0, 0.0,
		 * 4404.9275, 0.0, 8249.4903890475, 0.0, 8988.895510078815, }, { 4335.0, 3300.0,
		 * 0.0, 3375.0, 0.0, 0.0, 3450.0, 0.0,4275.0, 4350.0 }, {7170.91116, 3315.0,
		 * 0.0, 0.0, 0.0, 0.0, 3655.0, 0.0,0.0, 0.0 }, { 0.0, 0.0 4085.0, 0.0,0.0
		 * 3800.0,2700.0 0.0,0.0 6332.464225,7703.749825 0.0,0.0 0.0,0.0 0.0,0.0} };
		 * double[][] B = { { 1, 2, 1, 1, 1, 1, 1, 1, 1, 1 }, { 2, 0, 1, 1, 1, 1, 1, 1,
		 * 1, 1 }, { 1, 1, 1, 1, 1, 1, 1, 1, 1, 1 }, { 1, 1, 1, 1, 1, 1, 1, 1, 1, 1 }, {
		 * 1, 1, 1, 1, 1, 1, 1, 1, 1, 1 }, { 1, 1, 1, 1, 1, 1, 1, 1, 1, 1 }, { 1, 1, 1,
		 * 1, 1, 1, 1, 1, 1, 1 }, { 1, 1, 1, 1, 1, 1, 1, 1, 1, 1 }, { 1, 1, 1, 1, 1, 1,
		 * 1, 1, 1, 1 }, { 1, 1, 1, 1, 1, 1, 1, 1, 1, 1 } };
		 */
		// BimatrixMain bm = new BimatrixMain();
		// bm.transformInput(A, B);
		// LrsAlgorithm lrs = new LrsAlgorithm();
		// BimatrixSolver bs = new BimatrixSolver();
		// Equilibria equilibria = bs.findAllEq(lrs, bm.a, bm.b);
		// equilibria.print(true);
	}
}
