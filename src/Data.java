import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.*;
import java.util.ArrayList;

/**
 * Created by kimjisub on 2017. 6. 22..
 *
 */

class Data {

	final static String GraphDataURL = "GraphData.json";
	final static String LearnDataURL = "LearnData.json";


	static class GraphData {
		static void loadGraph(AIGraph.Vertex1[] vertex1, AIGraph.Vertex2[] vertex2) {
			try {
				BufferedReader in = new BufferedReader(new FileReader(GraphDataURL));
				StringBuilder stringBuilder = new StringBuilder();
				while (true) {
					String readLine = in.readLine();
					if (readLine != null)
						stringBuilder.append(readLine);
					else break;
				}

				JSONParser parser = new JSONParser();
				JSONArray JsonArray1 = (JSONArray) parser.parse(stringBuilder.toString());

				in.close();


				{
					JSONArray JsonArray2 = (JSONArray) JsonArray1.get(0);

					for (int i1 = 0; i1 < JsonArray2.size(); i1++) {

						JSONArray JsonArray3 = (JSONArray) JsonArray2.get(i1);
						for (int i2 = 0; i2 < JsonArray3.size(); i2++)
							vertex1[i1].edge[i2].weight = ((Double) JsonArray3.get(i2)).floatValue();
					}
				}

				{
					JSONArray JsonArray2 = (JSONArray) JsonArray1.get(1);

					for (int i1 = 0; i1 < JsonArray2.size(); i1++) {

						JSONArray JsonArray3 = (JSONArray) JsonArray2.get(i1);
						for (int i2 = 0; i2 < JsonArray3.size(); i2++)
							vertex2[i1].edge[i2].weight = ((Double) JsonArray3.get(i2)).floatValue();
					}
				}

			} catch (IOException | ParseException e) {
				//e.printStackTrace();
				System.err.println(GraphDataURL + " not Founded");
			}
		}

		static void saveGraph(AIGraph.Vertex1[] vertex1, AIGraph.Vertex2[] vertex2) {


			try {
				JSONArray JsonArray1 = new JSONArray();

				{
					JSONArray JsonArray2 = new JSONArray();

					int i1_size = vertex1.length;
					for (int i1 = 0; i1 < i1_size; i1++) {

						JSONArray JsonArray3 = new JSONArray();
						int i2_size = vertex1[i1].edge.length;
						for (int i2 = 0; i2 < i2_size; i2++)
							JsonArray3.add(vertex1[i1].edge[i2].weight);

						JsonArray2.add(JsonArray3);
					}
					JsonArray1.add(JsonArray2);
				}
				{
					JSONArray JsonArray2 = new JSONArray();

					int i1_size = vertex2.length;
					for (int i1 = 0; i1 < i1_size; i1++) {

						JSONArray JsonArray3 = new JSONArray();
						int i2_size = vertex2[i1].edge.length;
						for (int i2 = 0; i2 < i2_size; i2++)
							JsonArray3.add(vertex2[i1].edge[i2].weight);

						JsonArray2.add(JsonArray3);
					}
					JsonArray1.add(JsonArray2);
				}


				BufferedWriter out = new BufferedWriter(new FileWriter(GraphDataURL));

				out.write(JsonArray1.toJSONString());
				out.close();

			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}


	static class LearnData {
		float[][] map = new float[AIGraph.SIZE][AIGraph.SIZE];
		int num = -1;

		public LearnData(int num, float[][] map) {
			this.num = num;
			for (int x = 0; x < AIGraph.SIZE; x++) {
				for (int y = 0; y < AIGraph.SIZE; y++) {
					this.map[x][y] = map[x][y];
				}
			}
		}


		static ArrayList<LearnData> loadData() {
			ArrayList<LearnData> arrayList = new ArrayList<>();

			try {
				BufferedReader in = new BufferedReader(new FileReader(LearnDataURL));

				StringBuilder stringBuilder = new StringBuilder();
				while (true) {
					String readLine = in.readLine();
					if (readLine != null)
						stringBuilder.append(readLine);
					else break;
				}

				JSONParser parser = new JSONParser();
				JSONArray JsonArray = (JSONArray) parser.parse(stringBuilder.toString());

				in.close();


				for (int i = 0; i < JsonArray.size(); i++) {
					JSONObject JsonObject = (JSONObject) JsonArray.get(i);
					int num = ((Long) JsonObject.get("num")).intValue();
					float[][] map = new float[AIGraph.SIZE][AIGraph.SIZE];
					JSONArray JsonArray1 = (JSONArray) JsonObject.get("map");
					for (int x = 0; x < AIGraph.SIZE; x++) {
						JSONArray JsonArray2 = (JSONArray) JsonArray1.get(x);
						for (int y = 0; y < AIGraph.SIZE; y++) {
							map[x][y] = ((Double) JsonArray2.get(y)).floatValue();
						}
					}

					arrayList.add(new LearnData(num, map));
				}
			} catch (IOException | ParseException e) {
				//e.printStackTrace();
				System.err.println(LearnDataURL + " not Founded");
			}
			return arrayList;
		}

		static void saveData(ArrayList<LearnData> arrayList) {
			JSONArray JsonArray = new JSONArray();

			for (LearnData learnData : arrayList) {
				JSONObject JsonObject = new JSONObject();

				float[][] map = learnData.map;
				JSONArray JsonArray1 = new JSONArray();
				for (int i1 = 0; i1 < AIGraph.SIZE; i1++) {
					JSONArray JsonArray2 = new JSONArray();
					for (int i2 = 0; i2 < AIGraph.SIZE; i2++) {
						JsonArray2.add(map[i1][i2]);
					}
					JsonArray1.add(JsonArray2);
				}
				JsonObject.put("num", learnData.num);
				JsonObject.put("map", JsonArray1);
				JsonArray.add(JsonObject);
			}


			try {
				BufferedWriter out = new BufferedWriter(new FileWriter(LearnDataURL));

				out.write(JsonArray.toJSONString());
				out.close();

			} catch (IOException e) {
				e.printStackTrace();
			}
		}


	}
}
