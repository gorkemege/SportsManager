package com.team14.sportsmanager.logic; 

import com.team14.sportsmanager.core.ISport;
import com.team14.sportsmanager.core.ITeam;
import com.team14.sportsmanager.model.HeadballPlayer;
import com.team14.sportsmanager.model.HeadballSport;
import com.team14.sportsmanager.model.HeadballCoach;
import com.team14.sportsmanager.model.Team;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class SportFactory {

    public static ISport createSport(String sportType) {
        if ("HeadBall".equalsIgnoreCase(sportType) || "Headball".equalsIgnoreCase(sportType)) {
            return new HeadballSport();
        }
        throw new IllegalArgumentException("Unknown sport type: " + sportType);
    }

    public static List<ITeam> createLeagueTeams(String sportType) {
        List<ITeam> leagueTeams = new ArrayList<>();
        Random rand = new Random();

        if ("HeadBall".equalsIgnoreCase(sportType) || "Headball".equalsIgnoreCase(sportType)) {
            for (int i = 1; i <= 20; i++) {
                Team team = new Team("HeadBall FC " + i);
           
                for (int j = 1; j <= 10; j++) {
                    int randomHeadPower = 50 + rand.nextInt(50); 
                    int randomJumpHeight = 50 + rand.nextInt(50);
                    
                    HeadballPlayer player = new HeadballPlayer("Player " + i + "-" + j, randomHeadPower, randomJumpHeight);
                    team.addPlayer(player);
                }

                for (int c = 1; c <= 3; c++) {
                    int headPowerSkill = 50 + rand.nextInt(50);
                    int jumpSkill = 50 + rand.nextInt(50);
                    HeadballCoach coach = new HeadballCoach("Coach " + i + "-" + c, headPowerSkill, jumpSkill);
                    team.addCoach(coach);
                }

                leagueTeams.add(team);
            }
        } else {
            throw new IllegalArgumentException("Unknown sport type for teams: " + sportType);
        }

        return leagueTeams;
    }
}
