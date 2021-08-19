import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

@DisplayName("A test")
class TestingStats {

    Stats s = new Stats();

    @Test
    void allPlayers() throws Exception {
        String allPlayersJSON = Stats.getListOfAllPlayers();

        assertTrue(allPlayersJSON.contains("Doncic, Luka"));
        assertTrue(allPlayersJSON.contains("1629029"));
    }

    private void getAllPlayers() throws Exception {
        if(s.players.size() == 0){
            String allPlayersJSON = Stats.getListOfAllPlayers();
            s.collectPlayerIdsFromJSON(allPlayersJSON);
        }
    }

    @Test
    void isPlayerInList() throws Exception {
        getAllPlayers();

        assertTrue(s.players.size() > 4000);
        assertTrue(s.players.stream().filter(u -> u.firstName.equals("luka")).count() > 0);
    }

    @Test
    void findId() throws Exception {
        getAllPlayers();

        assertEquals(1629029, s.findPlayerId("Luka Doncic"));
        assertEquals(1629029, s.findPlayerId("LUKA doncic"));
        assertEquals(1629029, s.findPlayerId("Doncic lUKA"));
    }

    @Test
    void playerInfo() throws Exception {
        String playerData = Stats.getRegularSeason40min(1629029);

        String info3PA = Stats.extract3PAFromJSON(playerData);

        assertTrue(info3PA.contains("2018-19 7.1"));
        assertTrue(info3PA.contains("2019-20 8.9"));
        assertTrue(info3PA.contains("2020-21"));
    }

    @Test
    void playerInfoNotFound() throws Exception {
        String playerData = Stats.getRegularSeason40min(9999999);

        Exception exception = assertThrows(Exception.class, () ->
                Stats.extract3PAFromJSON(playerData));
        assertEquals("No data.", exception.getMessage());
    }

    @Test
    void unknownPlayerId() throws Exception {
        assertTrue(Stats.getRegularSeason40min(99999999).contains("\"rowSet\":[]"));
    }

    @Test
    void correctPlayerId() throws Exception {
        String outStr = Stats.getRegularSeason40min(1629029);

        assertTrue(outStr.contains("\"2018-19\""));
        assertTrue(outStr.contains("\"2019-20\""));
    }
}
