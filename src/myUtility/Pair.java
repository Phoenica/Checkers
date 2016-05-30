package myUtility;

public class Pair<A, B> {
	public A first;
	public B second;

	public Pair(A a, B b) {
		first = a;
		second = b;
	}

	public void set(A a, B b) {
		first = a;
		second = b;
	}

	public String ToString() {
		return "(" + first.toString() + " , " + second.toString() + ")";
	}


}
