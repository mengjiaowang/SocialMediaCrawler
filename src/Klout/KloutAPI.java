package Klout;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.TreeMap;

import org.json.simple.JSONObject;
import org.json.simple.JSONValue;

public class KloutAPI {


	private APIManager api_manage = APIManager.getInstance();
	private String url_twitter_identity = "http://api.klout.com/v2/identity.json/tw/";
	private String url_klout_score = "http://api.klout.com/v2/user.json/";

	public long getKloutID(long twitter_id) {

		long klout_id = -1;
		try {
			StringBuilder content = new StringBuilder();
			StringBuilder url = new StringBuilder();
			url.append(url_twitter_identity);
			url.append(twitter_id);
			url.append("?key=");
			url.append(api_manage.getKey());

			URL json = new URL(url.toString());
			BufferedReader in = new BufferedReader(new InputStreamReader(
					json.openStream()));

			String inputLine;
			while ((inputLine = in.readLine()) != null)
				content.append(inputLine);
			in.close();

			Object obj = JSONValue.parse(content.toString());
			JSONObject jsonObject = (JSONObject) obj;

			klout_id = Long.parseLong(jsonObject.get("id").toString());

		} catch (IOException e) {

		}
		return klout_id;
	}

	public TreeMap<String,Double> getKloutScore(long klout_id) {
		TreeMap<String,Double> score = new TreeMap<String,Double>();
		
		score.put("kloutScore", 0.0);
		score.put("dayChange", 0.0);
		score.put("weekChange", 0.0);
		score.put("monthChange", 0.0);
		
		try {
			StringBuilder content = new StringBuilder();
			StringBuilder url = new StringBuilder();
				
			url.append(url_klout_score);
			url.append(klout_id);
			url.append("/score?key=");
			url.append(api_manage.getKey());

			URL json = new URL(url.toString());
			BufferedReader in = new BufferedReader(new InputStreamReader(
					json.openStream()));

			String inputLine;
			while ((inputLine = in.readLine()) != null)
				content.append(inputLine);
			in.close();

//			System.out.println(content.toString());
			
			Object obj = JSONValue.parse(content.toString());
			JSONObject jsonObject = (JSONObject) obj;
			
			score.put("kloutScore",  Double.parseDouble(jsonObject.get("score").toString()));
			JSONObject delta = (JSONObject)jsonObject.get("scoreDelta");
			score.put("dayChange", Double.parseDouble(delta.get("dayChange").toString()));
			score.put("weekChange", Double.parseDouble(delta.get("weekChange").toString()));
			score.put("monthChange", Double.parseDouble(delta.get("monthChange").toString()));

		} catch (IOException e) {
			e.printStackTrace();
		}

		return score;
	}

}
