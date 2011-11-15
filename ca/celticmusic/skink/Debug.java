//////////////
//
//  debug output
//
//////////////

package ca.celticmusic.skink;

public class Debug {

	static int debugLevel = 0;

	public static void init() {
		debugLevel = Integer.getInteger("skink.debug.level", 0).intValue();
	}

	public static void output(int level, String outString) {
		if (level <= debugLevel) {
			System.out.println(outString);
		}
	}

	public static void setLevel(int level) {
		debugLevel = level;
	}

	public static void traceback() {
		try {
			doCauseException();
		} catch (Exception e) {
			System.out.println("Debug generated stack trace");
			e.printStackTrace(System.out);
		}
	}

	public static void doCauseException() throws Exception {
		throw new Exception("DEBUG");
	}

	private Debug() {
	}
}