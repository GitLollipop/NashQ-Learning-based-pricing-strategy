package priv.wjw;
import priv.wjw.random.MersenneTwisterFast;

public class MarketModel {

	private int N_0 = 100; // �г����ڵ�user����
	private int N_max = 10000; // �г��ȶ�ʱ��user����
	private double k = 0.07; // logistic�����еĲ���
	private double d0 = 1; // ����user��ʼ������
	private double c0 = 5.0; // ����provider�ĳ�ʼ�ɱ�
	private double beta = 0.01; // ����cost�Ĺ�ʽ�еĲ���
	private double theta = 0.02; // ����cost�Ĺ�ʽ�еĲ���

	private MersenneTwisterFast random = new MersenneTwisterFast(System.currentTimeMillis());

	/***
	 * ����tʱ�г���user������
	 */
	public int calUserNum(int t) {
		// user�����仯����logistic���߷���
		// int num=(int)(Arith.div(Arith.mul(N_0, N_max), Arith.add(N_0,
		// Arith.mul(Arith.sub(N_max, N_0), Arith.pow(Math.E, Arith.mul(-k, t))))));
		// return num;

		return N_0; // user�����ȶ�����,��ΪN_0
	}
	
	/***
	 * ÿ��ʵ�鿪ʼʱ�����ı��ļ��ж�ȡuserValue��ʹ��ÿ��ʵ���userValue��ͬ
	 */ 
	public void initializeUserValue() {
		UserValue.readAndSaveUserValue(N_0); // �����г��е�user����һֱΪN_0
	}

	/***
	 * ����user jѡ��ĳ��provider���ÿunit��demand�ܹ���õ�utility
	 * 
	 * @param j
	 *            user������,��0��ʼ
	 * @param price
	 *            user jѡ���provider�Ķ���
	 * @param preference
	 *            user j��ĳ��provider��ƫ��
	 * @return user j��utility
	 */
	public double calUserUtility(int j, double price, double preference) {
		double u = UserValue.userValue[j] - price + preference;
		return u;
	}
	

	/***
	 * user jѡ��provider1��provider2
	 * 
	 * @param j
	 *            user����������0��ʼ
	 * @param price1
	 *            provider1�Ķ���
	 * @param price2
	 *            provider2�Ķ���
	 * @return user jѡ���provider������1��2
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
	 * ����tʱuser j������
	 */
	public double calUserDemand(int j, int t) {
		return d0; // ��������user������һ����Ϊd0
	}

	/***
	 * ��������provider������tʱ�յ���������
	 * 
	 * @param userNum
	 *            tʱ�г��е�user��������
	 * @return ����provider������tʱ�յ���������
	 */
	public double[] calReceivedDemand(int t, int userNum, double price1, double price2) {
		double demand1=0;  //provider1��tʱ�յ���������
		double demand2=0;  //provider2��tʱ�յ���������
		for (int j = 0; j < userNum; j++) {
			int i=chooseProvider(j,price1,price2);
			double d=calUserDemand(j, t);  //user j��tʱ������
			demand1=i==1?demand1+d:demand1;
			demand2=i==2?demand2+d:demand2;
		}
		//*********************************************************************
		//System.out.println("received demands of two providers are respectively "+demand1+" "+demand2);
		//*********************************************************************
		return new double[]{demand1,demand2};
	}
	
	/***
	 * ����tʱ����provider��cost
	 */
	public double[] calCost(double[] demand, int t) {
//		double c1 = Arith.mul(Arith.mul(c0, Arith.pow(demand[0], -beta)), Arith.pow(Math.E, Arith.mul(-theta, t)));
//		double c2 = Arith.mul(Arith.mul(c0, Arith.pow(demand[1], -beta)), Arith.pow(Math.E, Arith.mul(-theta, t)));
//		return new double[]{c1,c2};
		return new double[]{c0,c0};
	}

	/***
	 * ����tʱ����provider��immediate reward
	 */
	public double[] calReward(int t,double price1,double price2) {
		int userNum=calUserNum(t);
		double[] demand=calReceivedDemand(t, userNum, price1, price2);  //providers����--usersѡ��provider--provider��������
		double[] cost=calCost(demand, t);  //provider��������--provider����cost
		double r1 = demand[0]*(price1-cost[0]);
		double r2 = demand[1]*(price2-cost[1]);
		return new double[]{r1,r2};
	}
}
