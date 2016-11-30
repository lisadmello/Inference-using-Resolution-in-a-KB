package resolution;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

public class homework {

	public static void main(String[] args) throws IOException {
		long startTime = System.currentTimeMillis();
		int n = 0, i;
		// read input.txt path
		String input_file = "input.txt";
		String line;
		FileReader a = new FileReader(input_file);
		BufferedReader b = new BufferedReader(a);
		// till you do not count all the lines
		while ((line = b.readLine()) != null)
			// count lines
			n = n + 1;
		b.close();
		// create new array for line storage
		String[] resolip = new String[n];
		FileReader c = new FileReader(input_file);
		BufferedReader d = new BufferedReader(c);
		for (i = 0; i < n; i++)
			resolip[i] = d.readLine().trim();
		ConvertCnf c1 = new ConvertCnf(resolip, n);
		c1.computeCnf();
		long endTime = System.currentTimeMillis();
		long totalTime = endTime - startTime;
//		System.out.println(totalTime);
	}

}
