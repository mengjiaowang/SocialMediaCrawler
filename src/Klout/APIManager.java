package Klout;

import java.util.Vector;

public class APIManager {

	private static APIManager instance;
	private static int current;
	private static int count;
	private static Vector<String> key = new Vector<String>();

	private APIManager() {

		current = 0;
		count = 0;

		key.add("3a6mugq5hkufpbvv7tped3p5");
		key.add("shxrfswfcbvbkffsdh99arp6");
//		key.add("8dgj6gzwjwrpvvtk4hcdede8");
//		key.add("9bpj7zeuuacrz789qxnxcacp");
		key.add("aj8jsqs8cg72rg3esve3689w");

		System.out.println("Klout API manager initialized.");
		for (int i = 0; i < key.size(); i++) {
			System.out.println("KEY(" + (i + 1) + "):"+key.get(i));
		}
	}

	public static APIManager getInstance() {
		if (instance == null) {
			instance = new APIManager();
		}
		return instance;
	}

	public static String getKey() {
		current = count % key.size();
		count++;
		return key.get(current);
	}
}
