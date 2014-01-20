import java.io.*;
import java.util.Date;
import java.util.TreeMap;
import java.util.Vector;

import twitter4j.User;

import Klout.KloutAPI;
import Twitter.TwitterAPI;

public class Crawler {

	/**
	 * @param args
	 */

	public static void main(String[] args) {

		System.setProperty("http.proxyHost", "proxy.pal.sap.corp");
		System.setProperty("http.proxyPort", "8080");

		crawlTwitterKloutID();

	}

	public static void crawTwitter() {
		TwitterAPI twitter = new TwitterAPI();
		try {
			BufferedReader br = new BufferedReader(new FileReader(
					"acefitness.csv"));
			BufferedWriter bw = new BufferedWriter(new FileWriter(
					"acefitness_klout.csv"));
			String header = "twitter_id,user_name,display_name,count_tweets,count_follower,count_friend,start_date,days,klout_score,day_change,week_change,month_change";
			bw.write(header);
			bw.newLine();

			int count1 = 0, count2 = 0;

			while (br.ready()) {

				count1++;
				String line = br.readLine();
				long id = Long.parseLong(line.substring(1,
						line.indexOf(',') - 1));
				User user = twitter.getUser(id);
				if (user != null) {
					count2++;
					print(user, line.replaceFirst("\"" + id + "\",", ""), bw);
				}

				if (count1 % 10 == 0) {
					System.out.println(count1);
				}
			}
			bw.close();
			br.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static void crawKlout() {
		TwitterAPI twitter = new TwitterAPI();
		KloutAPI klout = new KloutAPI();

		Vector<String> usersToCrawl = new Vector<String>();
		usersToCrawl.add("acefitness");

		for (String userToCrawl : usersToCrawl) {

			try {

				String fileName = userToCrawl + ".csv";
				BufferedWriter bw = new BufferedWriter(new FileWriter(fileName));

				System.out.println("File: " + fileName + " has created.");

				Long[] users = twitter.getFollowersIDs(userToCrawl);

				System.out.println("Followers (init): " + users.length);

				int count = 0;

				for (Long user : users) {

					long klout_id = klout.getKloutID(user);
					if (klout_id != -1) {
						TreeMap<String, Double> scores = klout
								.getKloutScore(klout_id);
						print(user, scores, bw);
						count++;
						if (count % 10 == 0)
							bw.flush();
					}
				}

				bw.close();
				System.out.println("Followers (crawled): " + count);

			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		System.out.println("Done.");
	}

	public static void crawlTwitterKloutID() {

		Vector<String> names = new Vector<String>();

		try {
			System.out.println("Start to load user list ...");

			BufferedReader br = new BufferedReader(new FileReader(
					"C:\\Users\\i016411\\Dropbox\\Ted\\id.txt"));

			while (br.ready()) {
				String line = br.readLine();
				names.add(line);
			}
			br.close();

			System.out.println("loading down ... there are " + names.size()
					+ " users.");

			String path = "C:\\Users\\i016411\\Dropbox\\Ted\\";
			String fileName = "ted.csv";
			BufferedWriter bw = new BufferedWriter(new FileWriter(path
					+ fileName));

			System.out.println("File: " + fileName + " has been created.");

			String header = "twitter_id,user_name,display_name,count_tweets,count_follower,count_friend,start_date,days,klout_score,day_change,week_change,month_change";
			bw.write(header);
			bw.newLine();

			int count = 0;
			int count1 = 0, count2 = 0;

			while (count < names.size()) {

				int tmpcount = 100;
				if (names.size() - count < 100)
					tmpcount = names.size() - count;

				String tmp[] = new String[tmpcount];

				for (int i = count + 1; i <= count + tmpcount; i++) {
					tmp[i - count - 1] = names.elementAt(i - 1);
				}

				TwitterAPI twitter = new TwitterAPI();
				KloutAPI klout = new KloutAPI();

				Vector<User> users = twitter.getUsers(tmp);
				
				System.out.println("number of users get:" + users.size());
	

				for (User user : users) {
					count2++;
					long klout_id = klout.getKloutID(user.getId());
					if (klout_id != -1) {
						TreeMap<String, Double> scores = klout
								.getKloutScore(klout_id);
						print(user, scores, bw);
						count1++;
						if (count1 % 10 == 0)
							bw.flush();
						if (count1 % 10 == 0)
							System.out.println(count1 + "/" + count2);
					}
				}
				count += tmpcount;

			}

			bw.close();
			System.out.println("done.");

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static void crawlTwitterKlout() {
		TwitterAPI twitter = new TwitterAPI();
		KloutAPI klout = new KloutAPI();
		Vector<String> usersToCrawl = new Vector<String>();

		// usersToCrawl.add("tedvickey");
		// usersToCrawl.add("spri");
		// usersToCrawl.add("SocialFitPhD");

		usersToCrawl.add("acefitness");

		for (String userToCrawl : usersToCrawl) {

			try {

				String fileName = userToCrawl + ".csv";
				BufferedWriter bw = new BufferedWriter(new FileWriter(fileName));
				String header = "twitter_id,user_name,display_name,count_tweets,count_follower,count_friend,start_date,days,klout_score,day_change,week_change,month_change";
				bw.write(header);
				bw.newLine();

				System.out.println("File: " + fileName + " has created.");

				// Vector<User> users =
				// twitter.getFollowersInBatch(userToCrawl);
				Vector<User> users = twitter.getFollowers(userToCrawl);

				System.out.println("Followers (init): " + users.size());

				int count1 = 0, count2 = 0;

				for (User user : users) {
					count2++;
					long klout_id = klout.getKloutID(user.getId());
					if (klout_id != -1) {
						TreeMap<String, Double> scores = klout
								.getKloutScore(klout_id);
						print(user, scores, bw);
						count1++;
						if (count1 % 10 == 0)
							bw.flush();
						if (count1 % 100 == 0)
							System.out.println(count1 + "/" + count2);
					}
				}

				bw.close();
				System.out.println("Followers (crawled): " + count1);

			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		System.out.println("Done.");
	}

	public static void print(User user, String klout, BufferedWriter bw) {
		StringBuilder line = new StringBuilder();

		Long diff = Date.parse((new Date()).toGMTString())
				- user.getCreatedAt().parse(user.getCreatedAt().toGMTString());
		line.append("\"");
		line.append(user.getId());
		line.append("\",");
		line.append(user.getScreenName());
		line.append(",\"");
		line.append(user.getName());
		line.append("\",");
		line.append(user.getStatusesCount());
		line.append(",");
		line.append(user.getFollowersCount());
		line.append(",");
		line.append(user.getFriendsCount());
		line.append(",");
		line.append(user.getCreatedAt().toGMTString());
		line.append(",");
		line.append(diff / (24 * 3600000));
		line.append(",");
		line.append(klout);
		try {
			bw.write(line.toString());
			bw.newLine();
			System.out.println(line.toString());
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public static void print(Long user, TreeMap<String, Double> scores,
			BufferedWriter bw) {

		StringBuilder line = new StringBuilder();

		line.append("\"");
		line.append(user);
		line.append("\",");
		line.append(scores.get("kloutScore"));
		line.append(",");
		line.append(scores.get("dayChange"));
		line.append(",");
		line.append(scores.get("weekChange"));
		line.append(",");
		line.append(scores.get("monthChange"));

		try {
			bw.write(line.toString());
			bw.newLine();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public static void print(User user, TreeMap<String, Double> scores,
			BufferedWriter bw) {

		StringBuilder line = new StringBuilder();

		Long diff = Date.parse((new Date()).toGMTString())
				- user.getCreatedAt().parse(user.getCreatedAt().toGMTString());
		line.append("\"");
		line.append(user.getId());
		line.append("\",");
		line.append(user.getScreenName());
		line.append(",\"");
		line.append(user.getName());
		line.append("\",");
		line.append(user.getStatusesCount());
		line.append(",");
		line.append(user.getFollowersCount());
		line.append(",");
		line.append(user.getFriendsCount());
		line.append(",");
		line.append(user.getCreatedAt().toGMTString());
		line.append(",");
		line.append(diff / (24 * 3600000));
		line.append(",");
		line.append(scores.get("kloutScore"));
		line.append(",");
		line.append(scores.get("dayChange"));
		line.append(",");
		line.append(scores.get("weekChange"));
		line.append(",");
		line.append(scores.get("monthChange"));

		try {
			bw.write(line.toString());
			bw.newLine();
		} catch (IOException e) {
			e.printStackTrace();
		}

	}
}
