import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.math.BigDecimal;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.json.*;

public class Stats {

    ArrayList<Player> players = new ArrayList<Player>();
    private static final String URL_LIST_OF_ALL_PLAYERS = "https://nba.com/stats/js/data/ptsd/stats_ptsd.js";
    private static final String URL_REG_40MIN = "https://stats.nba.com/stats/playerdashboardbyyearoveryear?DateFrom=&DateTo=&GameSegment=&LastNGames=0&LeagueID=00&Location=&MeasureType=Base&Month=0&OpponentTeamID=0&Outcome=&PORound=0&PaceAdjust=N&PerMode=PerGame&Period=0&PlusMinus=N&Rank=N&Season=2020-21&SeasonSegment=&SeasonType=Regular+Season&ShotClockRange=&Split=yoy&VsConference=&VsDivision=&PlayerID=";

    static String getRegularSeason40min(int id) throws Exception {
        return getHTML(URL_REG_40MIN + id);
    }

    static String extract3PAFromJSON(String jsonData) throws Exception {
        StringBuilder outStr = new StringBuilder();

        JSONObject obj1 = new JSONObject(jsonData);
        JSONArray allData = obj1.getJSONArray("resultSets");
        System.out.println("3PA data:");
        for(int i = 0; i < allData.length(); i++){
            JSONObject o = allData.getJSONObject(i);
            //System.out.println(o.getString("name"));
            if(o.getString("name").equals("ByYearPlayerDashboard")){

                int positionPA3 = o.getJSONArray("headers").toList().indexOf("FG3A");
                int positionYear = o.getJSONArray("headers").toList().indexOf("GROUP_VALUE");

                if(positionPA3 < 0 || positionYear < 0){
                    throw new Exception("Not Found required fields");
                }

                JSONArray rowSet = o.getJSONArray("rowSet");
                if(rowSet.length() == 0){
                    throw new Exception("No data.");
                }

                for(int j = 0; j < rowSet.length(); j++){
                    String year = rowSet.getJSONArray(j).getString(positionYear);
                    BigDecimal score3PA = rowSet.getJSONArray(j).getBigDecimal(positionPA3);

                    outStr.append(String.format("%s %.1f\n", year, score3PA));
                }
            }
        }
        return outStr.toString();
    }

    int findPlayerId(String queryPlayerName){
        List<Player> results = players.stream().filter(u -> u.isThis(queryPlayerName.trim().toLowerCase())).collect(Collectors.toList());

        if(results.size() == 1){
            System.out.println("Player ID: " + results.get(0).id);
            return results.get(0).id;
        }
        System.out.println("Player:     " + queryPlayerName + " NOT Found !");
        return -1;
    }

    static String getListOfAllPlayers() throws Exception {
        String allPlayersJSON = getHTML(URL_LIST_OF_ALL_PLAYERS);
        allPlayersJSON = allPlayersJSON.replace("var stats_ptsd = ", "");
        return allPlayersJSON;
    }

    void collectPlayerIdsFromJSON(String allPlayersJSON){
        JSONObject obj = new JSONObject(allPlayersJSON);
        JSONArray allPlayers = obj.getJSONObject("data").getJSONArray("players");

        for (int i = 0; i < allPlayers.length(); i++)
        {
            JSONArray playerInfo = allPlayers.getJSONArray(i);
            String[] playerName = playerInfo.getString(1).split(", ");
            if(playerName.length == 2) {
                players.add(new Player(playerInfo.getInt(0), playerName[1].toLowerCase(), playerName[0].toLowerCase()));
            }
        }
    }

    static String getHTML(String urlToRead) throws Exception {
        StringBuilder result = new StringBuilder();

        URLConnection conn = new URL(urlToRead).openConnection();
        conn.setRequestProperty("User-Agent", "Mozilla/5.0 (X11; Linux x86_64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/79.0.3945.130 Safari/537.36");
        conn.setRequestProperty("Origin", "https://www.nba.com");
        conn.setRequestProperty("Referer", "https://www.nba.com/stats/");
        conn.setRequestProperty("Sec-Fetch-Mode", "cors");
        conn.setRequestProperty("Sec-Fetch-Site", "same-site");
        conn.setRequestProperty("Accept", "application/json, text/plain, */*");
        conn.setRequestProperty("Accept-Encoding", "deflate, br");

        try{
            BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            for(String line; (line = reader.readLine())!=null;) {
                result.append(line);
            }
        } catch (Exception e) {
            // Not found
            e.printStackTrace();
        }
        return result.toString();
    }
}
