import java.util.ArrayList; // To be used in implementation  of IntegerStackGeneric
import java.util.Iterator;
import java.util.NoSuchElementException;

public abstract class IntegerStack implements Iterable<Integer> {

	public static enum Type {
		GENERIC, PRIMITIVE
	};

	/* Abstract methods */

	/**
	 * Adds a new integer to the stack.
	 *
	 * EXAMPLE: ------------- stack.push(1); stack.push(2); stack.push(3);
	 * System.out.println(stack);
	 *
	 * OUTPUT: --------------- [3, 2, 1]
	 */
	public abstract void push(int x);

	/**
	 * Removes the most recent integer on the stack, and returns it to the
	 * caller.
	 *
	 * EXAMPLE: ------------- stack.push(1); stack.push(2); stack.push(3);
	 * System.out.println(stack); System.out.println(stack.pop());
	 * System.out.println(stack);
	 *
	 * OUTPUT: --------------- [3, 2, 1] 3 [2, 1]
	 *
	 * @throws NoSuchElementException
	 *             if the stack is empty
	 */
	public abstract int pop() throws NoSuchElementException;

	/**
	 * Returns the number of elements on the stack.
	 *
	 * EXAMPLE: ---------------------- for(int i = 0; i < 10; i++)
	 * stack.push(i); System.out.println(stack.size())
	 *
	 * OUTPUT: ------------------- 10
	 */
	public abstract int size();

	/**
	 * Returns true if the integer x is on the stack, otherwise it returns
	 * false.
	 *
	 * EXAMPLE: ------------------- System.out.println(stack.contains(0));
	 * stack.push(0); System.out.println(stack.contains(0));
	 *
	 * OUTPUT: ----------------------- false true
	 */
	public abstract boolean contains(int x);

	/**
	 * Reverses the order of the stack
	 *
	 * EXAMPLE: --------------- stack.push(1); stack.push(2);
	 *
	 * System.out.println(stack); stack.reverse(); System.out.println(stack);
	 *
	 * OUTPUT: ------------------- [2, 1] [1, 2]
	 */
	public abstract void reverse();

	/**
	 * Returns an iterator that allows the caller to iterate through the
	 * elements on the stack.
	 *
	 * The iterator visits the more recent elements before the less recent
	 * element.
	 *
	 * EXAMPLE: ----------------- stack.push(1); stack.push(2);
	 *
	 * for(Integer i: stack) System.out.print(i);
	 *
	 * OUTPUT: ------------------ 21
	 */
	public abstract Iterator<Integer> iterator();

	/*
	 * Some useful methods
	 */

	/* Depends on push() */
	public void pushAll(Iterable<Integer> iterable) {
		for (Integer i : iterable)
			push(i);
	}

	/* Depends on size() */
	public boolean isEmpty() {
		return !(size() > 0);
	}

	/* Depends on iterator() */
	public String toString() {
		ArrayList<Integer> elements = new ArrayList<>();
		for (Integer i : this)
			elements.add(i);
		return elements.toString();
	}

	/* Factory method: Depends on constructors */
	public static IntegerStack create(Type type) {
		IntegerStack stack = null;
		switch (type) {
		case GENERIC:
			stack = new IntegerStackGeneric();
			break;
		case PRIMITIVE:
			stack = new IntegerStackPrimitive();
			break;
		}
		return stack;
	}

	/* Main method: Runs some simple tests */
	public static void main(String[] args) {
		IntegerStack generic = create(Type.GENERIC);
		IntegerStack primitive = create(Type.PRIMITIVE);

		test(generic);
		test(primitive);
	}

	private static void test(IntegerStack stack) {
		System.out.println(stack);
		stack.push(1);
		System.out.println(stack);
		try {
			System.out.printf("POP -> %d%n", stack.pop());
		} catch (NoSuchElementException e) {
			System.out.printf("POP -> %s%n", e);
		}
		System.out.println(stack);
	}

}

class IntegerStackGeneric extends IntegerStack {

	ArrayList<Integer> stack;

	IntegerStackGeneric() {
		stack = new ArrayList<>();
	}

	/**
	 * In according to ArrayList documentation add operation has amortized
	 * constant time
	 * (http://hg.openjdk.java.net/jdk7/jdk7/jdk/file/9b8c96f96a0f/
	 * src/share/classes/java/util/ArrayList.java). When the program calls the
	 * add operation the ArrayList object first calls the method
	 * ensureCapacityInternal (time << 1), that checks if the array that store
	 * the elements of ArrayList is big enough or should be resized. If it's
	 * necessary to increase the array size the ensureCapacityInternal method
	 * calls the grow method (time << 1), that makes a new array of particular
	 * size and uses Arrays.copyOf method (time << 1) to copy the elements of
	 * ArrayList to a new array. Arrays.copyOf is using the System.arraycopy
	 * method that is a native method and studying it is out of our scope so we
	 * assume that the time that this method uses is a constant.
	 */
	public void push(int x) {
		stack.add(x); // time << 1
	} // time << 1

	/**
	 * When the program calls the remove operation the ArrayList object first
	 * calls the method rangeCheck (time << 1), that checks that the index of
	 * the element that should be removed isn't out of bounds. Then the remove
	 * method shifts any subsequent elements to the left using System.arraycopy
	 * method (time << 1 - ?, see comment to the previous method).
	 */
	public int pop() throws NoSuchElementException {
		if (!stack.isEmpty()) {
			return stack.remove(stack.size() - 1); // time << 1
		} else
			throw new NoSuchElementException("IntegerStackGeneric: pop()"); // time
																			// <<
																			// 1
	} // time << 1

	public int size() {
		return stack.size();
	}

	public boolean contains(int x) {
		return stack.contains(x);
	}

	public void reverse() {
		for (int i = 0, j = stack.size() - 1; i < j; i++) {
			stack.add(i, stack.remove(j));
		}
	}

	public Iterator<Integer> iterator() {
		return new ReverseIterator();
	}

	private class ReverseIterator implements Iterator<Integer> {
		private int i = stack.size();

		public boolean hasNext() {
			return i > 0;
		}

		public Integer next() {
			return stack.get(--i);
		}

		public void remove() {
			throw new UnsupportedOperationException("remove");
		}
	}
}

class IntegerStackPrimitive extends IntegerStack {

	int[] stack;
	int size = 0;

	IntegerStackPrimitive() {
		stack = new int[1];
	}

	public void push(int x) {
		if (size == stack.length)
			resize(2 * stack.length); // time << 1 
			/*The push methods calls resize when number of pushes is 2, 3, 5, 9, 17, 33
			  and so on 
			 (i.e. 1 + (2+1) +(2^2 + 1) + (2^3 + 1) + ... + (2^k + 1) = 
			 2^0 + 2^1 + 2^2 + 2^3 + ... +2^k + k - 1).
			 2^0 + 2^1 + 2^2 + 2^3 + ... +2^k = 2^(k+1) - 1
			 The number of calls of resize method is k when the number of pushes is
			 2^(k+1) + k  - 2. 
			*/
		stack[size++] = x; // time << 1
	}

	private void resize(int length) {
		int[] temp = new int[length]; // time << 1
		for (int i = 0; i < size; i++) { // Frequency = size
			temp[i] = stack[i]; // time << 1
		} // time << size
		stack = temp; // time << 1
	} // time << size

	public int pop() throws NoSuchElementException {
		if (!(size == 0)) {
			int element = stack[--size]; // time << 1
			if (size > 0 && size == stack.length / 4)
				resize(stack.length / 2); // time << 1
			return element; // time << 1
		} else
			throw new NoSuchElementException("IntegerStackPrimitive: pop()"); // time
																				// <<
																				// 1
	} // time << 1

	public int size() {
		return size;
	}

	public boolean contains(int x) {
		for (int i : stack)
			if (i == x)
				return true;
		return false;
	}

	public void reverse() {
		for (int i = 0; i < size / 2; i++) {
			int temp = stack[i];
			stack[i] = stack[size - 1 - i];
			stack[size - 1 - i] = temp;
		}
	}

	public Iterator<Integer> iterator() {
		return new Itr();
	}

	private class Itr implements Iterator<Integer> {
		private Itr() {
		}

		private int i = size;

		public boolean hasNext() {
			return i > 0;
		}

		public Integer next() {
			return stack[--i];
		}

		public void remove() {
			throw new UnsupportedOperationException("remove");
		}
	}
}
