package priv.wjw;

import java.math.BigDecimal;

import priv.wjw.file.AppendFile;
import priv.wjw.file.CreateFile;
import priv.wjw.file.ReadFileAndSave;
import priv.wjw.file.Save;
import priv.wjw.random.MersenneTwisterFast;

public class UserValue implements Save {	
	public static double[] userValue;  //市场中所有user各自的marginal value
	int index;
	
	public UserValue() {	}
	
	public UserValue(int userNum) {
		userValue=new double[userNum];  //userNum为市场稳定时user的总数量
	}

	public void save(String str) {
		// TODO Auto-generated method stub
		userValue[index++]=new BigDecimal(str).doubleValue();
	}
	
	public static double[] readAndSaveUserValue(int userNum) {
		String fileName="parameters/userValue.txt";
		UserValue uv=new UserValue(userNum);
		ReadFileAndSave.readFileAndSave(fileName, uv);
		return userValue;
	}
	
	//产生m-n之间的随机数作为userValue并保存为文本文件random*(n-m)+m
	public static void produceRandomUserValue(String fileName,int n, int m) {
		CreateFile.createFile(fileName);  //检查文件是否存在或新建文件
		
		MersenneTwisterFast random = new MersenneTwisterFast(System.currentTimeMillis());	
		for (int i = 1; i <= 100; i++) {
			double r = random.nextDouble() * (n - m) + m;
			AppendFile.appendFile(fileName, r + "");  //将产生的随机数追加到文本文件中
			if (i < 100) {
				AppendFile.appendFile(fileName, "\r\n");
			}
		}
	}
	
	public static void main(String[] args) {
		String fileName = "parameters/userValue.txt";
		produceRandomUserValue(fileName,50,150); 
		System.out.println("success");
	}
}
