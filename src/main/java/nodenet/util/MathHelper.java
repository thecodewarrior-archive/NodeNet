package nodenet.util;

public class MathHelper {

	public static boolean between(double a, double check, double b) {
		return
			(a <= check && b >= check) ||
			(b <= check && a >= check)
		;
	}
	
}
