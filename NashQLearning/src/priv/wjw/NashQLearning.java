package priv.wjw;

import lse.math.games.solution.BimatrixMain;
import lse.math.games.solution.Solution;
import priv.wjw.file.AppendFile;
import priv.wjw.file.Log;
import priv.wjw.random.MersenneTwisterFast;

public class NashQLearning {
	private static double[] A1 = { 10, 20, 30, 40, 50, 60, 70, 80, 90, 100 };
	private static double[] A2 = { 10, 20, 30, 40, 50, 60, 70, 80, 90, 100 };
	private static State[] S = new State[A1.length * A2.length];
	private static double[] Q1 = new double[S.length * A1.length * A2.length];
	private static double[] Q2 = new double[S.length * A1.length * A2.length];
	private static double[] Pi1 = new double[S.length * A1.length];
	private static double[] Pi2 = new double[S.length * A2.length];
	private static int[] timesOfUpdateQ = new int[Q1.length];
	private static int[] timesOfVisitedState = new int[S.length];
	private double beta = 0.9; // 更新Q的公式中的参数
	private static MersenneTwisterFast random = new MersenneTwisterFast(System.currentTimeMillis());

	public static void main(String[] args) {
		long start = System.currentTimeMillis();

//		Log.log("result/NashQLearning/log.log");

		NashQLearning nql = new NashQLearning();
		MarketModel mm = new MarketModel();
		// initialize
		mm.initializeUserValue();
		nql.initialize();

		// learnig
		nql.learn();

		// print final Q and Pi
		Print p = new Print();
		String fileName = "result/NashQLearning/nashQLearning.txt";

		AppendFile.appendFile(fileName, "Q1 table as follows:\r\n");
		p.printQ(Q1, S.length, A1.length, A2.length, fileName);
		AppendFile.appendFile(fileName, "Q2 table as follows:\r\n");
		p.printQ(Q2, S.length, A1.length, A2.length, fileName);
		
		AppendFile.appendFile(fileName, "updatedQ table as follows:\r\n");
		p.printQ(timesOfUpdateQ, S.length, A1.length, A2.length, fileName);

		AppendFile.appendFile(fileName, "Pi1 table as follows:\r\n");
		p.printPi(Pi1, S.length, A1, fileName);
		AppendFile.appendFile(fileName, "Pi2 table as follows:\r\n");
		p.printPi(Pi2, S.length, A2, fileName);

		long end = System.currentTimeMillis();
		System.out.println("This experiment spends " + (end - start) + "ms");
	}

	public void initialize() {
		int len1 = A1.length;
		int len2 = A2.length;
		for (int i = 0; i < len1; i++) {
			for (int j = 0; j < len2; j++) {
				S[i * len2 + j] = new State(A1[i], A2[j]);
			}
		}

		double p1 = 1.0 / len1;
		double p2 = 1.0 / len2;
		for (int i = 0; i < len1 * len2; i++) { // 第i个state
			for (int j = 0; j < len1; j++) {
				Pi1[i * len1 + j] = p1;
			}
			for (int j = 0; j < len2; j++) {
				Pi2[i * len2 + j] = p2;
			}
		}
	}

	// 返回选择的动作的索引
	public double chooseAction(double[] Action, double[] probs, double explor) {
		double r = random.nextDouble();
		int indexOfAction = -1;
		//********************************************************
//		System.out.print("explor="+explor+" r="+r+" ");
		//********************************************************
		if (r <= explor) { // exploration
			indexOfAction = random.nextInt(Action.length);
		} else { // exploitation
			double p1 = 0.0, p2 = 0.0;
			double r1 = random.nextDouble();
			// **********************************
//			 System.out.println("exploitation r1="+r1);
//			 for (int i = 0; i < probs.length; i++) {
//				System.out.print(probs[i]+" ");
//			}
//			 System.out.println();
			// **********************************
			for (int i = 0; i < probs.length; i++) {
				p2 += probs[i];
				if (r1 >= p1 && r1 < p2) {
					indexOfAction = i;
					break;
				}
				p1 = p2;
			}
		}
		return indexOfAction==-1?Action[random.nextInt(Action.length)]:Action[indexOfAction];
	}

	public void learn() {
		State s = S[0];
		// **********************************
		// System.out.println("s0=("+s.toString()+")");
		// **********************************
		State nextState;
		double a1, a2, r1, r2, maxDeltaQ = 0;
		int convergeTime = 0, t = 0;
		int index1, index2; // 构成当前状态s的两个动作的索引
		int index_a1, index_a2; // 当前s下选择的两个动作的索引
		int index_s, index_nexts;
		double[] probs1 = new double[A1.length];
		double[] probs2 = new double[A2.length];
		MarketModel mm = new MarketModel();
		do {			
			index1 = getIndexOfAction(A1, s.getA1());
			index2 = getIndexOfAction(A2, s.getA2());
			index_s = index1 * A2.length + index2; // s在S中的索引

			int times = timesOfVisitedState[index_s];
			 //double explor = times == 0 ? 1 : 1.0 / (times+1);
			double explor = 1.0;
			for (int i = 0; i < probs1.length; i++) {
				probs1[i] = Pi1[index_s * A1.length + i];
			}
			for (int i = 0; i < probs2.length; i++) {
				probs2[i] = Pi2[index_s * A2.length + i];
			}
			a1 = chooseAction(A1, probs1, explor);
			a2 = chooseAction(A2, probs2, explor);
			timesOfVisitedState[index_s] = times + 1; // 更新对应state被访问的次数

			// *****************************************************************************************
			//System.out.println("\nt=" + t + " s=(" + s.toString() + ") a1,a2=(" + a1 + "," + a2 + ")");
			// *****************************************************************************************

			double[] reward = mm.calReward(t, a1, a2);
			r1 = reward[0];
			r2 = reward[1];

			// *****************************************************************************************
			//System.out.println("r1=" + r1 + " r2=" + r2);
			// *****************************************************************************************

			index_a1 = getIndexOfAction(A1, a1); // 当前选择的动作a1在A1中的索引
			index_a2 = getIndexOfAction(A2, a2);
			index_nexts = index_a1 * A2.length + index_a2; // s'在S中的索引
			nextState = S[index_nexts];

			long start=System.currentTimeMillis();
			Solution NE = nashQ(nextState);
			long end=System.currentTimeMillis();			
			int indexOfQ = index_s * A1.length * A2.length + index_a1 * A2.length + index_a2;
			int visitedQ = timesOfUpdateQ[indexOfQ];
			double alpha = visitedQ == 0 ? 1 : 1.0 / (visitedQ + 1);
			
			//*****************************************************************************************
			System.out.println("slove nash equilibria spends "+(end-start)+"ms");
			System.out.println("t=" + t+" alpha="+alpha);
			//*****************************************************************************************
			
			// *****************************************************************************************
//			System.out.println("visitedQ=" + visitedQ + " alpha=" + alpha);
//			System.out.println("index Of s=" + index_s + ", index of s'=" + index_nexts + ", index of Q=" + indexOfQ);
//			System.out.println("\npayoff matrix is as follows:");
//			for (int i = 0; i < A1.length; i++) {
//				for (int j = 0; j < A2.length; j++) {
//					int index = index_nexts * A1.length * A2.length + i * A2.length + j;
//					System.out.print(Q1[index] + "," + Q2[index] + " ");
//				}
//				System.out.println();
//			}
			// *****************************************************************************************

			double oldQ1 = Q1[indexOfQ]; // 更新前Q1的值
			double oldQ2 = Q2[indexOfQ]; // 更新前Q2的值
			double updatedQ1 = updateQ(r1, alpha, Q1, indexOfQ, NE.getPayoff1()); // 更新后Q2的值
			double updatedQ2 = updateQ(r2, alpha, Q2, indexOfQ, NE.getPayoff2()); // 更新后Q2的值
			double tempDeltaQ = Math.max(Math.abs(updatedQ1 - oldQ1), Math.abs(updatedQ2 - oldQ2)); // 找出deltaQ1和deltaQ2中的较大者
			maxDeltaQ = maxDeltaQ < tempDeltaQ ? tempDeltaQ : maxDeltaQ; // 更新maxDelta

			timesOfUpdateQ[indexOfQ] = visitedQ + 1; // 更新对应Q值被访问的次数

			double[] strategy1 = NE.getProbVec1();
			updatePi(index_nexts, A1, strategy1, Pi1);
			double[] strategy2 = NE.getProbVec2();
			updatePi(index_nexts, A2, strategy2, Pi2);

			// *****************************************************************************************
//			System.out.print("strategy1=(");
//			for (int i = 0; i < strategy1.length; i++) {
//				System.out.print(strategy1[i] + " ");
//			}
//			System.out.println(") payoff1=" + NE.getPayoff1());
//			System.out.print("strategy2=(");
//			for (int i = 0; i < strategy2.length; i++) {
//				System.out.print(strategy2[i] + " ");
//			}
//			System.out.println(") payoff2=" + NE.getPayoff2());
//			System.out.println("oldQ1=" + oldQ1 + " Q1=" + updatedQ1);
//			System.out.println("oldQ2=" + oldQ2 + " Q2=" + updatedQ2);
			// *******************************************************************************************

			s = nextState;
			t++;
			
			//每10万个t打印一次Q,Pi,maxDeltaQ
			if(t%100000==0){
				Print p = new Print();				
				String fileName="result/NashQLearning/middleQandPi.txt";
				
				AppendFile.appendFile(fileName, "t="+t+", maxDeltaQ="+maxDeltaQ+" convergeTime="+convergeTime+"\r\n");
				AppendFile.appendFile(fileName, "updatedQ table as follows:\r\n");
				p.printQ(timesOfUpdateQ, S.length, A1.length, A2.length, fileName);
				
				AppendFile.appendFile(fileName, "t="+t+", Q1 table as follows:\r\n");
				p.printQ(Q1, S.length, A1.length, A2.length, fileName);

				AppendFile.appendFile(fileName, "t="+t+", Q2 table follows:\r\n");
				p.printQ(Q2, S.length, A1.length, A2.length, fileName);

				AppendFile.appendFile(fileName, "t="+t+",Pi1 table as follows:\r\n");
				p.printPi(Pi1, S.length, A1, fileName);
				
				AppendFile.appendFile(fileName, "t="+t+",Pi2 table as follows:\r\n");
				p.printPi(Pi2, S.length, A2, fileName);
			}

			if (t % 10000 == 0 && maxDeltaQ <= 10000000) { // 每10000个t判断10000次更新Q1和100次更新Q2中前后差值最大的deltaQ,即maxDeltaQ是否小于收敛值
				convergeTime++;
				// **********************************
				System.out.println("convergeTime=" + convergeTime + " maxDeltaQ=" + maxDeltaQ);
				// **********************************
				maxDeltaQ = 0;
			}
			
			//1000万个t时强制停止
			if(t==10000000) break;

			// 每100个t判断deltaQ是否小于指定值，是则sameTime++，当sameTime达到20时，收敛
		} while (convergeTime!=1000);
		// **********************************
		System.out.println("This experiment experienced" + t + "t");
		// **********************************
	}

	public Solution nashQ(State s) {
		int row = A1.length; // state为s时，provider1可采取的动作个数
		int col = A2.length; // state为s时，provider2可采取的动作个数
		double[][] A = new double[row][col];
		double[][] B = new double[row][col];
		int indexOfa1_s = getIndexOfAction(A1, s.getA1());
		int indexOfa2_s = getIndexOfAction(A2, s.getA2());
		int indexOfs = indexOfa1_s * A2.length + indexOfa2_s; // s在S中的索引
		for (int i = 0; i < row; i++) {
			for (int j = 0; j < col; j++) {
				A[i][j] = Q1[indexOfs * row * col + i * col + j];
				B[i][j] = Q2[indexOfs * row * col + i * col + j];
			}
		}

//		FindOneNashEquilibrium f=new FindOneNashEquilibrium();
//		Solution solution=f.getOneNashEquilibrium(A, B);  //只求一个纳什均衡
		
		BimatrixMain bm=new BimatrixMain();
		Solution solution=bm.getNashEquilibriaInOrder(A, B, 2);  //求第二纳什均衡
		
//		BimatrixMain bm=new BimatrixMain();
//		Solution solution=bm.getTwoBestPayoffEquilibria(A, B);  //求各自payoff最大的纳什均衡（两个解可能不匹配）

//		BimatrixMain bm=new BimatrixMain();
//		Solution solution=bm.getBestPayOffSumEquilibria(A, B);  //求两个agent的payoff的和最大的纳什均衡
		
		return solution;
	}

	// 更新Q和alpha
	public double updateQ(double r, double alpha, double[] Q, int index, double nashQValue) {
		double value = (1 - alpha) * Q[index] + alpha * (r + beta * nashQValue);
		// double value=Arith.add(Arith.mul(Arith.sub(1, alpha), Q.get(key)),
		// Arith.mul(alpha, Arith.add(r, Arith.mul(beta, nashQValue))));
		Q[index] = value;
		return value;
	}

	// 更新的是Pi(s')
	public void updatePi(int indexOfs, double[] Action, double[] strategy, double[] Pi) {
		for (int i = 0; i < strategy.length; i++) {
			Pi[indexOfs * Action.length + i] = strategy[i];
		}
	}

	public int getIndexOfAction(double[] Action, double a) {
		for (int i = 0; i < Action.length; i++) {
			if (Action[i] == a) {
				return i;
			}
		}
		return -1;
	}
}
