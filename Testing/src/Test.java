import java.io.IOException;

public class Test {
	public static void main(String[] args){
		try {
			Runtime.getRuntime().exec("Rundll32.exe powrprof.dll,SetSuspendState Sleep");
		} catch (IOException exception) {
			// TODO Auto-generated catch-block stub.
			exception.printStackTrace();
		}
	}
}
