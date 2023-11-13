package toys;

public class Pair<U,V> {

	public U left;
	public V right;

	public Pair() {}
	
	public Pair(U left, V right) {
		this.left = left;
		this.right = right;
	}

	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("<").append(left != null ? left.toString() : "")
			.append(":").append(right != null ? right.toString() : "").append(">");
		return builder.toString();
	}
}
