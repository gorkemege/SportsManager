package com.team14.sportsmanager.ui; 

import com.team14.sportsmanager.core.ISport;
import com.team14.sportsmanager.core.ITeam;
import com.team14.sportsmanager.logic.MatchEngine; 
import com.team14.sportsmanager.logic.SportFactory; 

import java.util.List;

public class Launcher { 
    public static void main(String[] args) {
        System.out.println("==================================================");
        System.out.println("       SPORTS MANAGER SIMULATION STARTED          ");
        System.out.println("==================================================\n");

        ISport headball = SportFactory.createSport("HeadBall");
        System.out.println("[SYSTEM] Sport initialized: " + headball.getSportName());

        List<ITeam> league = SportFactory.createLeagueTeams("HeadBall");
        System.out.println("[SYSTEM] League created with " + league.size() + " teams.\n");

        ITeam team1 = league.get(0);
        ITeam team2 = league.get(1);

        System.out.println("Starting Match: " + team1.getTeamName() + " vs " + team2.getTeamName());
        System.out.println("--------------------------------------------------");

        MatchEngine engine = new MatchEngine(headball, team1, team2);
        engine.simulateMatch();

        for (String event : engine.getMatchEvents()) {
            System.out.println(" > " + event);
        }

        System.out.println("--------------------------------------------------");
        System.out.println("Post-Match League Standings Update:");
        System.out.println("- " + team1.getTeamName() + " Total Points: " + team1.getTotalPoints());
        System.out.println("- " + team2.getTeamName() + " Total Points: " + team2.getTotalPoints());
        System.out.println("==================================================");
    }
}
