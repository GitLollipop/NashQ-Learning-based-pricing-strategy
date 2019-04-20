package priv.wjw;

import java.util.Map;

import priv.wjw.file.AppendFile;
import priv.wjw.file.CreateFile;

public class Print {
	public void printQ(double[] Q, int lenS,int len1,int len2,String fileName) {
		CreateFile.createFile(fileName);
		for (int i = 0; i < lenS; i++) {
			for (int j = 0; j < len1; j++) {
				for (int k = 0; k < len2; k++) {
					int index=i*len1*len2+j*len2+k;
					AppendFile.appendFile(fileName, Q[index] + " ");
				}
				AppendFile.appendFile(fileName, "\r\n");
			}
			AppendFile.appendFile(fileName, "\r\n");
		}
	}
	
	public void printQ(int[] Q, int lenS,int len1,int len2,String fileName) {
		CreateFile.createFile(fileName);
		for (int i = 0; i < lenS; i++) {
			for (int j = 0; j < len1; j++) {
				for (int k = 0; k < len2; k++) {
					int index=i*len1*len2+j*len2+k;
					AppendFile.appendFile(fileName, Q[index] + " ");
				}
				AppendFile.appendFile(fileName, "\r\n");
			}
			AppendFile.appendFile(fileName, "\r\n");
		}
	}

	public void printPi(double[] Pi, int lenS,double[] Action, String fileName) {
		CreateFile.createFile(fileName);
		for (int i = 0; i < lenS; i++) {
			for (int j = 0; j < Action.length; j++) {
				int index=i*Action.length+j;
				AppendFile.appendFile(fileName, Pi[index] + " ");
			}
			AppendFile.appendFile(fileName, "\r\n");
		}
	}
}
