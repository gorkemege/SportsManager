package com.team14.sportsmanager;

import com.team14.sportsmanager.model.HeadballPlayer;
import com.team14.sportsmanager.model.Team;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class TeamTest {

    @Test
    public void testTeamInitialization() {
        Team team = new Team("Izmir FC");
        assertEquals("Izmir FC", team.getTeamName());
        assertEquals(0, team.getTotalPoints());
    }

    @Test
    public void testAddPlayerIncreasesRosterSize() {
        Team team = new Team("Izmir FC");
        team.addPlayer(new HeadballPlayer("Ege", 80, 80));
        assertEquals(1, team.getRoster().size(), "Roster size should be 1.");
    }

    @Test
    public void testAddMatchResultIncreasesTotalPoints() {
        Team team = new Team("Izmir FC");
        team.addMatchResult(3, 2, 0); // Win, 2 goals for, 0 against
        assertEquals(3, team.getTotalPoints(), "Total points should be 3.");
    }

    @Test
    public void testGoalDifferenceCalculation() {
        Team team = new Team("Izmir FC");
        team.addMatchResult(0, 1, 4); // Lost 1-4
        assertEquals(-3, team.getGoalDifference(), "Goal difference should be -3.");
    }

    @Test
    public void testAccumulatedMatchResults() {
        Team team = new Team("Izmir FC");
        team.addMatchResult(3, 2, 0);
        team.addMatchResult(1, 1, 1);
        assertEquals(4, team.getTotalPoints(), "Total points should be 4.");
        assertEquals(2, team.getGoalDifference(), "Overall GD should be +2.");
    }

    @Test
    public void testRecordHeadToHeadPoints() {
        Team home = new Team("Home");
        Team away = new Team("Away");
        home.recordHeadToHead(away, 3); // Home beat Away
        assertEquals(3, home.getHeadToHeadPoints(away));
        assertEquals(0, away.getHeadToHeadPoints(home));
    }

    @Test
    public void testMultipleHeadToHeadOpponents() {
        Team mainTeam = new Team("Main");
        Team opp1 = new Team("Opp1");
        Team opp2 = new Team("Opp2");
        mainTeam.recordHeadToHead(opp1, 1);
        mainTeam.recordHeadToHead(opp2, 3);
        assertEquals(1, mainTeam.getHeadToHeadPoints(opp1));
        assertEquals(3, mainTeam.getHeadToHeadPoints(opp2));
    }
}