package toys;

import java.io.*;
import java.util.*;

public class ThingyTree<T extends Comparable<T>> implements Externalizable {

    private Thingy<T> head;
    private float alpha = 0.2f;
    public int length;

    public ThingyTree() {
    }

    public ThingyTree(float alpha) {
        if (0.182f <= alpha && alpha <= 0.292f) {
            this.alpha = alpha;
        }
    }

    public String toString() {
        return new StringBuilder().append(alpha)
                .append(":").append(head != null ? head.toString() : "")
                .toString();
    }

    public int length() {
        return length;
    }

    ///////////////////////////////////////////////////////////////////////////

    public void add(Thingy<T> aThingy) {
        if (head == null) {
            head = aThingy;
        } else {
            head = descendInsert(head, aThingy);
        }
    }

    private Thingy<T> descendInsert(Thingy<T> cursor, Thingy<T> aThingy) {
        if (cursor.compareTo(aThingy) == 0) {
            return cursor;
        } else if (aThingy.compareTo(cursor) < 0) {
            if (cursor.left == null) {
                cursor.left = aThingy;
                length++;
            } else {
                cursor.left = descendInsert(cursor.left, aThingy);
            }
        } else if (aThingy.compareTo(cursor) > 0) {
            if (cursor.right == null) {
                cursor.right = aThingy;
                length++;
            } else {
                cursor.right = descendInsert(cursor.right, aThingy);
            }
        }
        cursor.resize();
        if (!isBalanced(cursor)) {
            cursor = rebalance(cursor);
        }
        return cursor;
    }

    ///////////////////////////////////////////////////////////////////////////

    public void remove(Thingy<T> aThingy) {
        if (head == null) {
            return;
        }
        head = descendRemove(head, aThingy);
    }

    private Thingy<T> descendRemove(Thingy<T> cursor, Thingy<T> aThingy) {
        if (aThingy.compareTo(cursor) < 0) {
            if (cursor.left != null) {
                cursor.left = descendRemove(cursor.left, aThingy);
                cursor.resize();
            }
        } else if (aThingy.compareTo(cursor) > 0) {
            if (cursor.right != null) {
                cursor.right = descendRemove(cursor.right, aThingy);
                cursor.resize();
            }
        } else if (aThingy.compareTo(cursor) == 0) {
            if (cursor.left != null) {
                if (cursor.right != null) {
                    cursor.right = descendInsert(cursor.right, cursor.left);
                    cursor.left = null;
                    cursor = cursor.right;
                } else {
                    cursor = cursor.left;
                }
            } else if (cursor.right != null) {
                cursor = cursor.right;
            } else { // if (cursor.left == null && cursor.right == null) {
                cursor = null;
            }
            length--;
        }
        return cursor;
    }

    ///////////////////////////////////////////////////////////////////////////

    private boolean isBalanced(Thingy<T> cursor) {
        if (cursor.left == null && cursor.right == null) {
            return true;
        }
        if ((cursor.left != null && cursor.size() < 2 && cursor.right == null)
                || (cursor.right != null && cursor.size() < 2 && cursor.left == null)) {
            return true;
        }
        if (cursor.left != null && cursor.right != null) {
            int diff = cursor.right.weight() - cursor.left.weight();
            if (Math.abs(diff) < alpha * cursor.right.weight()) {
                return true;
            }
        }
		/*
		if (cursor.left != null && cursor.left.weight() >= weight
				&& cursor.right != null && cursor.right.weight() >= weight) {
			return true;
		}
		*/
        return false;
    }

    private Thingy<T> rebalance(Thingy<T> cursor) {
        if (cursor.right == null || cursor.left != null && cursor.left.size() > cursor.right.size()) {
            // Rotate Right
            Thingy<T> node = cursor.left;
            cursor.left = node.right;
            cursor.resize();
            node.right = cursor;
            cursor = node;
            cursor.resize();
        } else if (cursor.left == null || cursor.right != null && cursor.right.size() > cursor.left.size()) {
            // Rotate Left
            Thingy<T> node = cursor.right;
            cursor.right = node.left;
            cursor.resize();
            node.left = cursor;
            cursor = node;
            cursor.resize();
        }
        return cursor;
    }

    ///////////////////////////////////////////////////////////////////////////

    public class TraversIterator implements Iterator<T> {
        Stack<Thingy<T>> stack;
        Thingy<T> cursor;

        TraversIterator() {
            stack = new Stack<>();
            if (head != null) {
                stack.push(head);
            }
        }

        @Override
        public boolean hasNext() {
            if (stack.size() > 0) {
                Thingy<T> thingy = stack.peek();
                if (thingy.left != null && thingy.left != cursor && thingy.right != cursor) {
                    thingy = thingy.left;
                    while (thingy != null) {
                        stack.push(thingy);
                        thingy = thingy.left;
                    }
                    return true;
                }
                if (thingy.right != null && thingy.right != cursor) {
                    stack.push(thingy.right);
                    thingy = thingy.right;
                    if (thingy.left != null) {
                        thingy = thingy.left;
                        do {
                            stack.push(thingy);
                            thingy = thingy.left;
                        } while (thingy != null);
                        return true;
                    }
                }
                return true;
            }
            return false;
        }

        @Override
        public T next() {
            cursor = stack.peek();
            return stack.pop().getThingy();
        }

    }

    public Iterator<T> iterator() {
        return new TraversIterator();
    }

    ///////////////////////////////////////////////////////////////////////////


    @Override
    public void writeExternal(ObjectOutput objectOutput) throws IOException {
        Visitor<Thingy<T>> wrapper = new Visitor<Thingy<T>>() {
            @Override
            public void visit(Thingy<T> thingy) throws Exception {
                thingy.writeExternal(objectOutput);
            }
        };
        try {
            traverse(head, wrapper);
        }
        catch (Exception e) {
            throw new IOException(e.getMessage());
        }
    }

    @Override
    public void readExternal(ObjectInput objectInput) throws IOException, ClassNotFoundException {
        head = new Thingy<>();
        head.readExternal(objectInput);
        while (objectInput.available() > 0) {
            Thingy<T> element = new Thingy<>();
            element.readExternal(objectInput);
            rebuild(head, element);
        }
    }

    private void rebuild(Thingy<T> cursor, Thingy<T> element) {
        if (element.compareTo(cursor) < 0) {
            if (cursor.left == null) {
                cursor.left = element;
                return;
            }
            rebuild(cursor.left, element);
        }
        else if (element.compareTo(cursor) > 0) {
            if (cursor.right == null) {
                cursor.right = element;
                return;
            }
            rebuild(cursor.right, element);
        }
    }

    ///////////////////////////////////////////////////////////////////////////

    public List<Thingy<T>> flatten() {
        List<Thingy<T>> list = new ArrayList<>();
        Visitor<Thingy<T>> wrapper = new Visitor<Thingy<T>>() {
            public void visit(Thingy<T> thingy) {
                list.add(thingy);
            }
        };
        try {
            traverse(head, wrapper);
        }
        catch (Exception ignore) {}
        return list;
    }

    private Visitor<Thingy<T>> traverse(Thingy<T> cursor, Visitor<Thingy<T>> visitor) throws Exception {
        visitor.visit(cursor);
        if (cursor.left != null) {
            traverse(cursor.left, visitor);
        }
        if (cursor.right != null) {
            traverse(cursor.right, visitor);
        }
        return visitor;
    }

    public List<Thingy<T>> lhrFlatten() {
        List<Thingy<T>> list = new ArrayList<>();
        Visitor<Thingy<T>> wrapper = new Visitor<Thingy<T>>() {
            public void visit(Thingy<T> thingy) {
                list.add(thingy);
            }
        };
        try {
            lhrTraverse(head, wrapper);
        }
        catch (Exception ignore) {}
        return list;
    }

    private Visitor<Thingy<T>> lhrTraverse(Thingy<T> cursor, Visitor<Thingy<T>> visitor) throws Exception {
        if (cursor.left != null) {
            lhrTraverse(cursor.left, visitor);
        }
        if (cursor.right != null) {
            lhrTraverse(cursor.right, visitor);
        }
        visitor.visit(cursor);
        return visitor;
    }

    public List<Thingy<T>> dfFlatten() {
        List<Thingy<T>> list = new ArrayList<>();
        Visitor<Thingy<T>> wrapper = new Visitor<Thingy<T>>() {
            public void visit(Thingy<T> thingy) {
                list.add(thingy);
            }
        };
         try {
             dfTraverse(head, wrapper);
         }
         catch(Exception ignore){}
        return list;
    }

    private Visitor<Thingy<T>> dfTraverse(Thingy<T> cursor, Visitor<Thingy<T>> visitor) throws Exception {
        visitor.visit(cursor);
        while (cursor.left != null) {
            cursor = cursor.left;
            visitor.visit(cursor);
        }
        if (cursor.right != null) {
            dfTraverse(cursor.right, visitor);
        }
        return visitor;
    }

    public void dump(PrintStream output) {
        Visitor<Thingy<T>> wrapper = new Visitor<Thingy<T>>() {
            public void visit(Thingy<T> thingy) {
                output.print(thingy.toString());
                output.print(", ");
            }
        };
        try {
            lhrTraverse(head, wrapper);
        }
        catch (Exception ignore) {}
    }
}
