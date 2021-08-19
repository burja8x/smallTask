import java.io.BufferedReader;
import java.io.InputStreamReader;

public class Main {
    public static void main(String[] args) throws Exception {
        Stats s = new Stats();
        // Get all players names & ids.
        String allPlayersJSON = Stats.getListOfAllPlayers();
        s.collectPlayerIdsFromJSON(allPlayersJSON);

        // Read from console.
        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
        while(true) {
            System.out.print("Enter Player Name:\n");
            String queryPlayerName = br.readLine();

            // Find player ID
            int id = s.findPlayerId(queryPlayerName);

            // If player ID is found GET 3PA info.
            if(id != -1){
                String playerData = Stats.getRegularSeason40min(id);
                String info3PA = Stats.extract3PAFromJSON(playerData);

                System.out.println(info3PA);
            }
        }
    }
}
