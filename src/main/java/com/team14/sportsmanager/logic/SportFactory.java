package com.team14.sportsmanager.logic; 

import com.team14.sportsmanager.core.ISport;
import com.team14.sportsmanager.core.ITeam;
import com.team14.sportsmanager.model.HandballCoach;
import com.team14.sportsmanager.model.HandballPlayer;
import com.team14.sportsmanager.model.HandballSport;
import com.team14.sportsmanager.model.HeadballPlayer;
import com.team14.sportsmanager.model.HeadballSport;
import com.team14.sportsmanager.model.HeadballCoach;
import com.team14.sportsmanager.model.Team;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class SportFactory {

    public static ISport createSport(String sportType) {
        if ("Headball".equalsIgnoreCase(sportType)) {
            return new HeadballSport();
        }
        else if ("Handball".equalsIgnoreCase(sportType)) {
            return new HandballSport();
        }
        throw new IllegalArgumentException("Unknown sport type: " + sportType);
    }

    public static List<ITeam> createLeagueTeams(String sportType) {
        List<ITeam> leagueTeams = new ArrayList<>();
        Random rand = new Random();

        List<String> teamNames = NameGenerator.getTeamNames(sportType);

        if ("Headball".equalsIgnoreCase(sportType)) {
            for (int i = 0; i < 20; i++) {
                String teamName = (teamNames.size() > i) ? teamNames.get(i) : "Headball FC " + (i + 1);
                Team team = new Team(teamName);
           
                for (int j = 0; j < 14; j++) {
                    int headPower = 50 + rand.nextInt(5);
                    int jumpHeight = 50 + rand.nextInt(5);
                    team.addPlayer(new HeadballPlayer(NameGenerator.randomName(), headPower, jumpHeight));
                }

                for (int c = 0; c < 3; c++) {
                    int headPowerSkill = 50 + rand.nextInt(5);
                    int jumpSkill = 50 + rand.nextInt(5);
                    team.addCoach(new HeadballCoach(NameGenerator.randomName(), headPowerSkill, jumpSkill));
                }

                leagueTeams.add(team);
            }
        } 
        else if ("Handball".equalsIgnoreCase(sportType)) {
            for (int i = 0; i < 20; i++) {
                String teamName = (teamNames.size() > i) ? teamNames.get(i) : "Handball FC " + (i + 1);
                Team team = new Team(teamName);

                for (int j = 0; j < 14; j++) {
                    int throwPower = 50 + rand.nextInt(5);
                    int speed = 50 + rand.nextInt(5);
                    int agility = 50 + rand.nextInt(5);
                    int goalkeeping = 50 + rand.nextInt(5);
                    team.addPlayer(new HandballPlayer(NameGenerator.randomName(), throwPower, speed, agility, goalkeeping));
                }

                for (int c = 0; c < 3; c++) {
                    int throwSkill = 50 + rand.nextInt(5);
                    int speedSkill = 50 + rand.nextInt(5);
                    int agilitySkill = 50 + rand.nextInt(5);
                    team.addCoach(new HandballCoach(NameGenerator.randomName(), throwSkill, speedSkill, agilitySkill));
                }

                leagueTeams.add(team);
            }
        } 
        else {
            throw new IllegalArgumentException("Unknown sport type: " + sportType);
        }

        return leagueTeams;
    }
    public static League createLeague(String sportType) {
        ISport sport = createSport(sportType);
        List<ITeam> teams = createLeagueTeams(sportType);
        League league = new League(sport, teams);
        league.generateFixtures();
        return league;
    }
}
