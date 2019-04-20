package priv.wjw;
import priv.wjw.random.MersenneTwisterFast;

public class MarketModel {

	private int N_0 = 100; // 市场初期的user数量
	private int N_max = 10000; // 市场稳定时的user数量
	private double k = 0.07; // logistic方程中的参数
	private double d0 = 1; // 所有user初始的需求
	private double c0 = 5.0; // 所有provider的初始成本
	private double beta = 0.01; // 计算cost的公式中的参数
	private double theta = 0.02; // 计算cost的公式中的参数

	private MersenneTwisterFast random = new MersenneTwisterFast(System.currentTimeMillis());

	/***
	 * 计算t时市场中user的数量
	 */
	public int calUserNum(int t) {
		// user数量变化符合logistic曲线方程
		// int num=(int)(Arith.div(Arith.mul(N_0, N_max), Arith.add(N_0,
		// Arith.mul(Arith.sub(N_max, N_0), Arith.pow(Math.E, Arith.mul(-k, t))))));
		// return num;

		return N_0; // user数量稳定不变,且为N_0
	}
	
	/***
	 * 每次实验开始时，从文本文件中读取userValue，使得每次实验的userValue相同
	 */ 
	public void initializeUserValue() {
		UserValue.readAndSaveUserValue(N_0); // 假设市场中的user数量一直为N_0
	}

	/***
	 * 计算user j选择某个provider后对每unit的demand能够获得的utility
	 * 
	 * @param j
	 *            user的索引,从0开始
	 * @param price
	 *            user j选择的provider的定价
	 * @param preference
	 *            user j对某个provider的偏好
	 * @return user j的utility
	 */
	public double calUserUtility(int j, double price, double preference) {
		double u = UserValue.userValue[j] - price + preference;
		return u;
	}
	

	/***
	 * user j选择provider1或provider2
	 * 
	 * @param j
	 *            user的索引，从0开始
	 * @param price1
	 *            provider1的定价
	 * @param price2
	 *            provider2的定价
	 * @return user j选择的provider的索引1或2
	 */
	public int chooseProvider(int j, double price1, double price2) {
		double v1 = UserValue.userValue[j] - price1;
		double v2 = UserValue.userValue[j] - price2;
		double prob1 = 1.0/(1+Math.pow(Math.E, (v2-v1)/180));

		double r = random.nextDouble();
		if (r >= 0 && r < prob1) {
			return 1;
		}
		return 2;
	}
	
	/***
	 * 计算t时user j的需求
	 */
	public double calUserDemand(int j, int t) {
		return d0; // 假设所有user的需求一样，为d0
	}

	/***
	 * 计算两个provider各自在t时收到的总需求
	 * 
	 * @param userNum
	 *            t时市场中的user的总数量
	 * @return 两个provider各自在t时收到的总需求
	 */
	public double[] calReceivedDemand(int t, int userNum, double price1, double price2) {
		double demand1=0;  //provider1在t时收到的总需求
		double demand2=0;  //provider2在t时收到的总需求
		for (int j = 0; j < userNum; j++) {
			int i=chooseProvider(j,price1,price2);
			double d=calUserDemand(j, t);  //user j在t时的需求
			demand1=i==1?demand1+d:demand1;
			demand2=i==2?demand2+d:demand2;
		}
		//*********************************************************************
		//System.out.println("received demands of two providers are respectively "+demand1+" "+demand2);
		//*********************************************************************
		return new double[]{demand1,demand2};
	}
	
	/***
	 * 计算t时两个provider的cost
	 */
	public double[] calCost(double[] demand, int t) {
//		double c1 = Arith.mul(Arith.mul(c0, Arith.pow(demand[0], -beta)), Arith.pow(Math.E, Arith.mul(-theta, t)));
//		double c2 = Arith.mul(Arith.mul(c0, Arith.pow(demand[1], -beta)), Arith.pow(Math.E, Arith.mul(-theta, t)));
//		return new double[]{c1,c2};
		return new double[]{c0,c0};
	}

	/***
	 * 计算t时两个provider的immediate reward
	 */
	public double[] calReward(int t,double price1,double price2) {
		int userNum=calUserNum(t);
		double[] demand=calReceivedDemand(t, userNum, price1, price2);  //providers定价--users选择provider--provider计算需求
		double[] cost=calCost(demand, t);  //provider计算需求--provider计算cost
		double r1 = demand[0]*(price1-cost[0]);
		double r2 = demand[1]*(price2-cost[1]);
		return new double[]{r1,r2};
	}
}
