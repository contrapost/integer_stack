import edu.princeton.cs.introcs.*;

public class Measurements {

	private static final int LO = 100;
	private static final int HI = 100000;
	private static final int NUMBER_OF_STEPS = 1000;
	private static final int STEP = (HI - LO) / (NUMBER_OF_STEPS - 1);


//	private static final Out out = new Out("resultsWithoutJIT.csv");
	private static final Out out = new Out("resultsWithJIT.csv");

	public static void main(String[] args) {

		out.printf("%9s;%s;%s;%s;%s;%s;%s;%s;%s\n", "Number of elemnts",
				"Time of pushes (Generic)", "Time/number (Generic)",
				"Time of pops (Generic)", "Time/number (Generic)",
				"Time of pushes (Primitives)", "Time/number (Primitives)",
				"Time of pops (Primitives)", "Time/number (Primitives)");

		for (int size = LO; size <= HI; size += STEP) {
			out.printf(
					"%9d;%s;%s\n",
					size,
					measure(IntegerStack.create(IntegerStack.Type.GENERIC),
							size),
					measure(IntegerStack.create(IntegerStack.Type.PRIMITIVE),
							size));
		}

	}

	private static String measure(IntegerStack stack, int N) {
		long startTime = System.nanoTime();
		for (int i = 0; i < N; i++) {
			stack.push(i);
		}
		long estimatedTimePush = System.nanoTime() - startTime;
		// 1 second = 10^9 nanoseconds
		double timePush = estimatedTimePush / 10e9;

		startTime = System.nanoTime();
		for (int i = 0; i < N; i++) {
			stack.pop();
		}
		long estimatedTimePop = System.nanoTime() - startTime;
		double timePop = estimatedTimePop / 10e9;

		double kPush = timePush / N;
		double kPop = timePop / N;
		return String.format("%.10f;%.15f;%.10f;%.15f", timePush, kPush,
				timePop, kPop);
	}

}
