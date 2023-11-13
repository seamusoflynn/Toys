package toys;

import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.lang.reflect.InvocationTargetException;

public class Thingy<T extends Comparable<T>> implements Externalizable {

	private T theThing;
	transient protected Thingy<T> left;
	transient protected Thingy<T> right;
	private int size;

	public Thingy() {
		this.size = 0;
	}

	public Thingy(T t) {
		this();
		this.theThing = t;
	}

	public void setThingy(T t) {
		this.theThing = t;
	}
	
	public T getThingy() {
		return theThing;
	}

	public int compareTo(Thingy<T> other) {
		return this.theThing.compareTo(other.theThing);
	}

	public String toString() {
		StringBuilder stringbuilder = new StringBuilder();
		stringbuilder.append("(")
			.append((left != null ? "L<-" : "N<-"))
			.append(theThing != null ? theThing.toString() : "null").append("[").append(size).append("]")
			.append((right != null ? "->R" : "->N"))
			.append(")");
		return stringbuilder.toString();
	}

	protected int resize() {
		int leftSize = (this.left != null ? this.left.size : 0);
		int rightSize = (this.right != null ? this.right.size : 0);
		this.size = leftSize + rightSize + 1;
		return this.size;
	}

	protected int size() {
		return this.size;
	}

	protected int weight() {
		resize();
		return this.size + 1;
	}

	@Override
	public void writeExternal(ObjectOutput objectOutput) throws IOException {
		if (theThing instanceof Externalizable) {
			String theName = theThing.getClass().getCanonicalName();
			objectOutput.writeUTF(theName);
			objectOutput.writeInt(size);
			((Externalizable)theThing).writeExternal(objectOutput);
		} else {
			throw new IOException(theThing.getClass().getName() + " is not externalizable");
		}
	}

	@Override
	public void readExternal(ObjectInput objectInput) throws IOException, ClassNotFoundException {
		Class classT = Class.forName(objectInput.readUTF());
		if (classT != null) {
			try {
				this.size = objectInput.readInt();
				this.theThing = (T) classT.getConstructor().newInstance();
				((Externalizable)this.theThing).readExternal(objectInput);
			}
			catch (InvocationTargetException | InstantiationException
					| IllegalAccessException | ClassCastException | NoSuchMethodException e) {
				throw new IOException(classT.getName() + " is not externalizable");
			}
		} else {
			throw new IOException(classT.getName() + "is not externalizable");
		}
	}
}
