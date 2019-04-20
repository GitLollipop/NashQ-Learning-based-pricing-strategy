package priv.wjw;

public class State {
	
	//joint prices of two providers
	private double a1;
	private double a2;
	
	public State() {
		
	}
	
	public State(double a1, double a2) {
		super();
		this.a1 = a1;
		this.a2 = a2;
	}

	public double getA1() {
		return a1;
	}

	public double getA2() {
		return a2;
	}		
	
	public String toString() {
		return ""+this.a1+this.a2;
	}
	public boolean equals(State s) {
		if(this.getA1()==s.getA1()&&this.getA2()==s.getA2()) {
			return true;
		}
		return false;
	}
	public State findState(double a1,double a2,State[] S) {
		try {
			for (int i = 0; i < S.length; i++) {
				State s=S[i];
				if(s.getA1()==a1&&s.getA2()==a2)
					return S[i];
			}
		}catch(NullPointerException e){
			System.out.println("no such state exists!");
		} 
		return null;
	}
}
