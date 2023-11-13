package toys;

public interface Visitor<T> {

    public void visit(T t) throws Exception;
}
