import java.io.*;
import java.util.HashSet;

public class IDProcessor {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		try {

			BufferedReader br = new BufferedReader(new FileReader(
					"C:\\Users\\i016411\\Dropbox\\Ted\\Twitter IDs.csv"));

			HashSet<String> set = new HashSet<String>();

			while (br.ready()) {
				String line = br.readLine();
				String name[] = line.split(",");
				for (int i = 0; i < name.length; i++) {
					set.add(name[i].toLowerCase());
				}
			}
			br.close();
			
			br = new BufferedReader(new FileReader(
					"C:\\Users\\i016411\\Dropbox\\Ted\\id_crawled.txt"));

			while (br.ready()) {
				String line = br.readLine().toLowerCase();
				set.remove(line);
			}
			br.close();
			
			
			BufferedWriter bw = new BufferedWriter(new FileWriter(
					"C:\\Users\\i016411\\Dropbox\\Ted\\ids.txt"));
			for (String name : set) {
				bw.write(name);
				bw.newLine();
			}
			bw.close();
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

}
