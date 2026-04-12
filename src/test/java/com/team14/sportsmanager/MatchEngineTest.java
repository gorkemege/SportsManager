package com.team14.sportsmanager;

import com.team14.sportsmanager.logic.MatchEngine;
import com.team14.sportsmanager.model.HeadballPlayer;
import com.team14.sportsmanager.model.HeadballSport;
import com.team14.sportsmanager.model.Team;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class MatchEngineTest {

    @Test
    public void testEngineInitialization() {
        MatchEngine engine = new MatchEngine(new HeadballSport(), new Team("T1"), new Team("T2"));
        assertFalse(engine.isMatchFinished());
        assertEquals(0, engine.getMatchEvents().size());
    }

    @Test
    public void testSimulateSinglePeriod() {
        MatchEngine engine = new MatchEngine(new HeadballSport(), new Team("T1"), new Team("T2"));
        engine.simulatePeriod();
        assertFalse(engine.isMatchFinished(), "Match should not finish after 1 period.");
        assertTrue(engine.getMatchEvents().size() > 0, "Events should be recorded.");
    }

    @Test
    public void testMatchFinishesAfterAllPeriods() {
        MatchEngine engine = new MatchEngine(new HeadballSport(), new Team("T1"), new Team("T2"));
        engine.simulateMatch();
        assertTrue(engine.isMatchFinished(), "Match must be finished.");
    }

    @Test
    public void testApplySubstitutionRecordsEvent() {
        MatchEngine engine = new MatchEngine(new HeadballSport(), new Team("T1"), new Team("T2"));
        HeadballPlayer p1 = new HeadballPlayer("OutPlayer", 50, 50);
        HeadballPlayer p2 = new HeadballPlayer("InPlayer", 80, 80);

        engine.applyUserSubstitution(p1, p2);

        boolean eventFound = false;
        for (String event : engine.getMatchEvents()) {
            if (event.contains("InPlayer") && event.contains("OutPlayer")) {
                eventFound = true;
                break;
            }
        }
        assertTrue(eventFound, "Substitution event must be recorded in the match events list.");
    }
}