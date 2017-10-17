package ch.fhnw.util;

public class Pair<L, R> {
	public L left;
	public R right;
	
	public Pair(L left, R  right) {
		this.left  = left;
		this.right = right;
	}
	
	@Override
	public boolean equals(Object obj) {
		if(!(obj instanceof Pair)) return false;
		Pair<?,?> other = (Pair<?,?>)obj;
		return left.equals(other.left) && right.equals(other.right);
	}
	
	@Override
	public int hashCode() {
		return left.hashCode();
	}
	
	@Override
	public String toString() {
		return "(" + left + ',' + right + ')';
	}
}
