package toys;

import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;

public class ExtInteger implements Comparable<ExtInteger>, Externalizable {

    Integer value;
    public ExtInteger() {
        value = 0;
    }

    public ExtInteger(Integer value) {
        this.value = value;
    }

    public int intValue() {
        return value.intValue();
    }

    @Override
    public void writeExternal(ObjectOutput objectOutput) throws IOException {
        objectOutput.writeInt(value.intValue());
    }

    @Override
    public void readExternal(ObjectInput objectInput) throws IOException, ClassNotFoundException {
        value = objectInput.readInt();
    }

    @Override
    public int compareTo(ExtInteger other) {
        return this.value.compareTo(other.value);
    }

    public String toString() { return value.toString(); }
}
