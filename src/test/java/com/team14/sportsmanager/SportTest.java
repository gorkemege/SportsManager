package com.team14.sportsmanager;

import com.team14.sportsmanager.core.ITeam;
import com.team14.sportsmanager.model.HeadballSport;
import com.team14.sportsmanager.model.Team;
import org.junit.jupiter.api.Test;
import java.util.Comparator;
import static org.junit.jupiter.api.Assertions.*;

public class SportTest {

    @Test
    public void testSportNameAndPeriods() {
        HeadballSport sport = new HeadballSport();
        assertEquals("Headball", sport.getSportName());
        assertEquals(4, sport.getPeriodCount());
    }

    @Test
    public void testScoringRulesMap() {
        HeadballSport sport = new HeadballSport();
        assertEquals(2, sport.getScoringRules().get("Win"));
        assertEquals(1, sport.getScoringRules().get("Draw"));
    }

    @Test
    public void testTieBreakerByTotalPoints() {
        HeadballSport sport = new HeadballSport();
        Comparator<ITeam> logic = sport.getStandingLogic();
        Team t1 = new Team("T1"); t1.addMatchResult(10, 5, 2);
        Team t2 = new Team("T2"); t2.addMatchResult(8, 5, 2);
        
        assertTrue(logic.compare(t1, t2) < 0, "T1 should be ranked higher due to total points.");
    }

    @Test
    public void testTieBreakerByHeadToHead() {
        HeadballSport sport = new HeadballSport();
        Comparator<ITeam> logic = sport.getStandingLogic();
        Team t1 = new Team("T1"); 
        Team t2 = new Team("T2"); 

        t1.addMatchResult(10, 5, 5);
        t2.addMatchResult(10, 5, 5);

        t1.recordHeadToHead(t2, 3);
        t2.recordHeadToHead(t1, 0);

        assertTrue(logic.compare(t1, t2) < 0, "T1 should be ranked higher due to Head-to-Head.");
    }

    @Test
    public void testTieBreakerByGoalDifference() {
        HeadballSport sport = new HeadballSport();
        Comparator<ITeam> logic = sport.getStandingLogic();
        Team t1 = new Team("T1"); 
        Team t2 = new Team("T2"); 
        
        t1.addMatchResult(10, 10, 2); 
        t2.addMatchResult(10, 5, 2);  
        t1.recordHeadToHead(t2, 1);
        t2.recordHeadToHead(t1, 1);

        assertTrue(logic.compare(t1, t2) < 0, "T1 should be ranked higher due to Goal Difference.");
    }
}
