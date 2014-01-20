package Twitter;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.Vector;

import twitter4j.IDs;
import twitter4j.RateLimitStatus;
import twitter4j.ResponseList;
import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.TwitterFactory;
import twitter4j.User;
import twitter4j.auth.AccessToken;

public class TwitterAPI {

	public static Twitter crawler = null;

	public static RateLimitStatus getRateLimit(String str) {
		try {
			Map<String, RateLimitStatus> limits = crawler.getRateLimitStatus();
			RateLimitStatus status = limits.get(str);
			// System.out.println(limits);
			return status;
		} catch (TwitterException e) {
			e.printStackTrace();
		}
		return null;
	}

	public TwitterAPI() {
		try {
			crawler = new TwitterFactory().getInstance();
			crawler.setOAuthConsumer("j13VhN0iOdXNkGZdqZYpA",
					"0Qr6rxAJ8wHlxN1zFn2H3sm2oHxBX1NDkHP4iZ7M7M");
			AccessToken accessToken = new AccessToken(
					"112433156-j6Qh6gZ5yCNtJkGuuBX7hhOnus89kkdUpiwX1VtU",
					"7yd8qV3hF6FkG3JT5biiPbJ0iOk6pEuWkwrcPndc7zQ");
			crawler.setOAuthAccessToken(accessToken);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static void printRateLimit() {
		try {
			System.out.println(crawler.getRateLimitStatus().toString());
		} catch (TwitterException e) {
			e.printStackTrace();
		}
	}

	public Vector<User> getFriends(String user) {

		Vector<User> friends = new Vector<User>();
		Long[] ids = getFriendsIDs(user);
		int cur = 0;
		while (cur < ids.length) {
			if (ids.length - cur > 100) {
				long[] tmp = new long[100];
				for (int i = cur; i < cur + 100; i++) {
					tmp[i - cur] = ids[i];
				}
				try {
					ResponseList<User> users = crawler.lookupUsers(tmp);

					for (User u : users) {
						friends.add(u);
					}
					cur += 100;
				} catch (TwitterException e) {
					e.printStackTrace();
				}
			} else {

				long[] tmp = new long[ids.length - cur];
				for (int i = cur; i < ids.length; i++) {
					tmp[i - cur] = ids[i];
				}
				try {
					ResponseList<User> users = crawler.lookupUsers(tmp);

					for (User u : users) {
						friends.add(u);
					}
					cur = ids.length;
				} catch (TwitterException e) {
					e.printStackTrace();
				}
			}
		}
		return friends;
	}

	public Long[] getFriendsIDs(String username) {

		long cur = -1;
		Vector<Long> friends = new Vector<Long>();
		int page_size = 1;

		while (page_size != 0) {
			try {

				IDs ids = crawler.getFriendsIDs(username, cur);

				long[] userIds = ids.getIDs();
				if (userIds.length == 0)
					break;
				for (Long id : userIds) {
					friends.add(id);
				}

				if (cur != ids.getNextCursor()) {
					cur = ids.getNextCursor();
				} else {
					break;
				}
			} catch (TwitterException e) {
				e.printStackTrace();
			}
		}

		Long[] allIds = new Long[friends.size()];

		for (int i = 0; i < friends.size(); i++) {
			allIds[i] = friends.elementAt(i);
		}

		return allIds;
	}

	public Vector<User> getUsers(String names[]) {
		Vector<User> users = new Vector<User>();

		RateLimitStatus status = getRateLimit("/users/lookup");
		int remaining = status.getRemaining();
		int reset = status.getSecondsUntilReset();

		try {

			if (remaining == 0) {
				System.out.println("Rate Limit Hited - Will sleep " + reset
						+ " seconds.");
				try {
					Thread.sleep(reset * 1000);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				status = getRateLimit("/users/lookup");
				remaining = status.getRemaining();
				System.out.println("Remaining Hits: " + remaining);
			}

			ResponseList<User> rusers = crawler.lookupUsers(names);

			for (User u : rusers) {
				users.add(u);
			}
		} catch (TwitterException e) {
			e.printStackTrace();
		}

		return users;
	}

	public Vector<User> getFollowers(String user) {

		Vector<User> followers = new Vector<User>();
		Long[] ids = getFollowersIDs(user);

		System.out.println("Get " + ids.length + " follower IDs ...");
		RateLimitStatus status = getRateLimit("/users/lookup");
		int remaining = status.getRemaining();
		int reset = status.getSecondsUntilReset();
		System.out.println("Crawling Followers of " + user + " ...");
		System.out.println("Remaining Hits: " + remaining);

		int cur = 0;
		while (cur < ids.length) {

			if (remaining == 0) {
				System.out.println("Rate Limit Hited - Will sleep " + reset
						+ " seconds.");
				try {
					Thread.sleep(reset * 1000);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				status = getRateLimit("/users/lookup");
				remaining = status.getRemaining();
				System.out.println("Remaining Hits: " + remaining);
			}

			if (ids.length - cur > 100) {
				long[] tmp = new long[100];
				for (int i = cur; i < cur + 100; i++) {
					tmp[i - cur] = ids[i];
				}
				try {

					remaining--;
					ResponseList<User> users = crawler.lookupUsers(tmp);

					for (User u : users) {

						followers.add(u);

					}
					cur += 100;
				} catch (TwitterException e) {
					if (e.getErrorCode() == 88) {
						printRateLimit();
					} else {
						e.printStackTrace();
					}
				}
			} else {

				long[] tmp = new long[ids.length - cur];
				for (int i = cur; i < ids.length; i++) {
					tmp[i - cur] = ids[i];
				}
				try {

					remaining--;
					ResponseList<User> users = crawler.lookupUsers(tmp);

					for (User u : users) {
						followers.add(u);
					}
					cur = ids.length;
				} catch (TwitterException e) {
					if (e.getErrorCode() == 88) {
						printRateLimit();
					} else {
						e.printStackTrace();
					}
				}
			}
		}
		return followers;
	}

	public Long[] getFollowersIDs(String username) {

		long cur = -1;
		Vector<Long> followers = new Vector<Long>();
		int page_size = 1;

		HashSet<Long> set = new HashSet<Long>();

		try {
			BufferedReader br = new BufferedReader(new FileReader("id.txt"));
			while (br.ready()) {
				String line = br.readLine();
				Long id = Long.parseLong(line);
				set.add(id);
			}
			br.close();
		} catch (IOException e1) {
			e1.printStackTrace();
		}

		RateLimitStatus status = getRateLimit("/followers/ids");
		int remaining = status.getRemaining();
		int reset = status.getSecondsUntilReset();
		System.out.println("Crawling Follower IDs of " + username + " ...");
		System.out.println("Remaining Hits: " + remaining);

		while (page_size != 0) {
			try {

				if (remaining == 0) {
					System.out.println("Rate Limit Hited - Will sleep " + reset
							+ " seconds.");
					try {
						Thread.sleep(reset * 1000);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
					status = getRateLimit("/followers/ids");
					remaining = status.getRemaining();
					System.out.println("Remaining Hits: " + remaining);
				}

				remaining--;
				IDs ids = crawler.getFollowersIDs(username, cur);

				long[] userIds = ids.getIDs();
				if (userIds.length == 0)
					break;
				for (Long id : userIds) {
					if (!set.contains(id))
						followers.add(id);
				}

				if (cur != ids.getNextCursor()) {
					cur = ids.getNextCursor();
				} else {
					break;
				}
			} catch (TwitterException e) {
				e.printStackTrace();
			}
		}

		Long[] allIds = new Long[followers.size()];

		for (int i = 0; i < followers.size(); i++) {
			allIds[i] = followers.elementAt(i);
		}

		return allIds;
	}

	public static User getUser(long id) {
		try {
			return crawler.showUser(id);
		} catch (TwitterException e) {
			if (e.getErrorCode() == 88) {
				printRateLimit();
				System.out.println(id);
				try {
					Thread.sleep(1000);
				} catch (InterruptedException e1) {
					e1.printStackTrace();
				}
			} else {
				e.printStackTrace();
			}
		}
		return null;
	}

	public static User getUser(String screenName) {

		try {
			return crawler.showUser(screenName);
		} catch (TwitterException e) {
			if (e.getErrorCode() == 88) {
				printRateLimit();
			}
		}
		return null;
	}
}