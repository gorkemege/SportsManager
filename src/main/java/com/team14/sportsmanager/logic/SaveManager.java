package com.team14.sportsmanager.logic;

import com.team14.sportsmanager.core.ICoach;
import com.team14.sportsmanager.core.IPlayer;
import com.team14.sportsmanager.core.ITeam;
import com.team14.sportsmanager.model.HandballCoach;
import com.team14.sportsmanager.model.HandballPlayer;
import com.team14.sportsmanager.model.HeadballCoach;
import com.team14.sportsmanager.model.HeadballPlayer;
import com.team14.sportsmanager.model.Team;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class SaveManager {

    private static final String DB_URL = "jdbc:sqlite:sports_manager_save.db";

    public static void saveGame(League league, String sportType, String managerTeamName, String managerTactic) {
        try (Connection conn = DriverManager.getConnection(DB_URL)) {
            conn.setAutoCommit(false);
            clearDatabase(conn);
            createTables(conn);

            for (ITeam team : league.getStandings()) {
                long teamId = saveTeam(conn, team);
                savePlayers(conn, teamId, team.getRoster(), sportType);
                saveCoaches(conn, teamId, team.getCoachingStaff(), sportType);

                for (ITeam opponent : league.getStandings()) {
                    if (!opponent.getTeamName().equals(team.getTeamName())) {
                        saveH2H(conn, team.getTeamName(), opponent.getTeamName(), team.getHeadToHeadPoints(opponent));
                    }
                }
            }

            conn.commit();
            System.out.println("Game saved successfully.");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static League loadGame() {
        try (Connection conn = DriverManager.getConnection(DB_URL)) {
            String sportType = loadSportType(conn);
            int currentWeek = loadCurrentWeek(conn);

            List<ITeam> teams = loadTeams(conn, sportType);
            loadH2H(conn, teams);

            League league = new League(SportFactory.createSport(sportType), teams);
            league.generateFixtures();

            for (int i = 0; i < currentWeek; i++) {
                league.advanceWeekForLoad();
            }

            System.out.println("Game loaded successfully.");
            return league;

        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }

    private static void createTables(Connection conn) throws SQLException {
        Statement stmt = conn.createStatement();
        stmt.execute("""
            CREATE TABLE IF NOT EXISTS game_state (
                id INTEGER PRIMARY KEY,
                sport_type TEXT,
                current_week INTEGER
                manager_team_name TEXT
                manager_tactic TEXT                                  
            )
        """);
        stmt.execute("""
            CREATE TABLE IF NOT EXISTS teams (
                id INTEGER PRIMARY KEY AUTOINCREMENT,
                team_name TEXT,
                total_points INTEGER,
                goals_scored INTEGER,
                goals_conceded INTEGER
            )
        """);
        stmt.execute("""
            CREATE TABLE IF NOT EXISTS players (
                id INTEGER PRIMARY KEY AUTOINCREMENT,
                team_id INTEGER,
                name TEXT,
                attr1_name TEXT, attr1_value INTEGER,
                attr2_name TEXT, attr2_value INTEGER,
                attr3_name TEXT, attr3_value INTEGER,
                attr4_name TEXT, attr4_value INTEGER,
                is_injured INTEGER,
                injury_duration INTEGER
            )
        """);
        stmt.execute("""
            CREATE TABLE IF NOT EXISTS coaches (
                id INTEGER PRIMARY KEY AUTOINCREMENT,
                team_id INTEGER,
                name TEXT,
                spec1_name TEXT, spec1_value INTEGER,
                spec2_name TEXT, spec2_value INTEGER,
                spec3_name TEXT, spec3_value INTEGER
            )
        """);
        stmt.execute("""
            CREATE TABLE IF NOT EXISTS h2h_records (
                team_name TEXT,
                opponent_name TEXT,
                points INTEGER
            )
        """);
    }

    private static void clearDatabase(Connection conn) throws SQLException {
        Statement stmt = conn.createStatement();
        stmt.execute("DROP TABLE IF EXISTS game_state");
        stmt.execute("DROP TABLE IF EXISTS teams");
        stmt.execute("DROP TABLE IF EXISTS players");
        stmt.execute("DROP TABLE IF EXISTS coaches");
        stmt.execute("DROP TABLE IF EXISTS h2h_records");
    }

    private static void saveGameState(Connection conn, String sportType, int currentWeek, String managerTeamName, String managerTactic) throws SQLException {
        PreparedStatement ps = conn.prepareStatement(
                "INSERT INTO game_state (id, sport_type, current_week, manager_team_name, manager_tactic) VALUES (1, ?, ?, ?, ?)"
        );
        ps.setString(1, sportType);
        ps.setInt(2, currentWeek);
        ps.setString(3, managerTeamName);
        ps.setString(4, managerTactic);
        ps.executeUpdate();
    }
    private static long saveTeam(Connection conn, ITeam team) throws SQLException {
        PreparedStatement ps = conn.prepareStatement(
            "INSERT INTO teams (team_name, total_points, goals_scored, goals_conceded) VALUES (?, ?, ?, ?)",
            Statement.RETURN_GENERATED_KEYS
        );
        ps.setString(1, team.getTeamName());
        ps.setInt(2, team.getTotalPoints());
        ps.setInt(3, team.getGoalsScored());
        ps.setInt(4, team.getGoalsConceded());
        ps.executeUpdate();
        ResultSet rs = ps.getGeneratedKeys();
        rs.next();
        return rs.getLong(1);
    }

    private static void savePlayers(Connection conn, long teamId, List<IPlayer> players, String sportType) throws SQLException {
        for (IPlayer player : players) {
            Map<String, Integer> attrs = player.getAttributes();
            List<String> keys = new ArrayList<>(attrs.keySet());
            while (keys.size() < 4) keys.add(null);

            PreparedStatement ps = conn.prepareStatement(
                "INSERT INTO players (team_id, name, attr1_name, attr1_value, attr2_name, attr2_value, attr3_name, attr3_value, attr4_name, attr4_value, is_injured, injury_duration) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)"
            );
            ps.setLong(1, teamId);
            ps.setString(2, player.getName());
            for (int i = 0; i < 4; i++) {
                String key = keys.get(i);
                if (key != null) {
                    ps.setString(3 + i * 2, key);
                    ps.setInt(4 + i * 2, attrs.get(key));
                } else {
                    ps.setNull(3 + i * 2, Types.VARCHAR);
                    ps.setNull(4 + i * 2, Types.INTEGER);
                }
            }
            ps.setInt(11, player.isInjured() ? 1 : 0);
            ps.setInt(12, player.getRemainingInjuryDuration());
            ps.executeUpdate();
        }
    }

    private static void saveCoaches(Connection conn, long teamId, List<ICoach> coaches, String sportType) throws SQLException {
        for (ICoach coach : coaches) {
            Map<String, Integer> specs = coach.getSpecialties();
            List<String> keys = new ArrayList<>(specs.keySet());
            while (keys.size() < 3) keys.add(null);

            PreparedStatement ps = conn.prepareStatement(
                "INSERT INTO coaches (team_id, name, spec1_name, spec1_value, spec2_name, spec2_value, spec3_name, spec3_value) VALUES (?, ?, ?, ?, ?, ?, ?, ?)"
            );
            ps.setLong(1, teamId);
            ps.setString(2, coach.getName());
            for (int i = 0; i < 3; i++) {
                String key = keys.get(i);
                if (key != null) {
                    ps.setString(3 + i * 2, key);
                    ps.setInt(4 + i * 2, specs.get(key));
                } else {
                    ps.setNull(3 + i * 2, Types.VARCHAR);
                    ps.setNull(4 + i * 2, Types.INTEGER);
                }
            }
            ps.executeUpdate();
        }
    }

    private static void saveH2H(Connection conn, String teamName, String opponentName, int points) throws SQLException {
        PreparedStatement ps = conn.prepareStatement(
            "INSERT INTO h2h_records (team_name, opponent_name, points) VALUES (?, ?, ?)"
        );
        ps.setString(1, teamName);
        ps.setString(2, opponentName);
        ps.setInt(3, points);
        ps.executeUpdate();
    }

    private static String loadSportType(Connection conn) throws SQLException {
        ResultSet rs = conn.createStatement().executeQuery("SELECT sport_type FROM game_state WHERE id = 1");
        return rs.getString("sport_type");
    }

    private static int loadCurrentWeek(Connection conn) throws SQLException {
        ResultSet rs = conn.createStatement().executeQuery("SELECT current_week FROM game_state WHERE id = 1");
        return rs.getInt("current_week");
    }

    public static String loadManagerTeamName() {
        try (Connection conn = DriverManager.getConnection(DB_URL)) {
            ResultSet rs = conn.createStatement().executeQuery("SELECT manager_team_name FROM game_state WHERE id = 1");
            if (rs.next()) {
                return rs.getString("manager_team_name");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return null;
    }

    public static String loadManagerTactic() {
        try (Connection conn = DriverManager.getConnection(DB_URL)) {
            ResultSet rs = conn.createStatement().executeQuery("SELECT manager_tactic FROM game_state WHERE id = 1");
            if (rs.next()) {
                return rs.getString("manager_tactic");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return null;
    }

    private static List<ITeam> loadTeams(Connection conn, String sportType) throws SQLException {
        List<ITeam> teams = new ArrayList<>();
        ResultSet rs = conn.createStatement().executeQuery("SELECT * FROM teams");

        while (rs.next()) {
            long teamId = rs.getLong("id");
            String teamName = rs.getString("team_name");
            int totalPoints = rs.getInt("total_points");
            int goalsScored = rs.getInt("goals_scored");
            int goalsConceded = rs.getInt("goals_conceded");

            Team team = new Team(teamName, totalPoints, goalsScored, goalsConceded);

            loadPlayers(conn, teamId, team, sportType);
            loadCoaches(conn, teamId, team, sportType);
            teams.add(team);
        }
        return teams;
    }

    private static void loadPlayers(Connection conn, long teamId, Team team, String sportType) throws SQLException {
        PreparedStatement ps = conn.prepareStatement("SELECT * FROM players WHERE team_id = ?");
        ps.setLong(1, teamId);
        ResultSet rs = ps.executeQuery();

        while (rs.next()) {
            String name = rs.getString("name");
            boolean isInjured = rs.getInt("is_injured") == 1;
            int injuryDuration = rs.getInt("injury_duration");

            Map<String, Integer> attrs = new java.util.HashMap<>();

            for (int i = 1; i <= 4; i++) {
                String attrName = rs.getString("attr" + i + "_name");

                if (attrName != null) {
                    attrs.put(attrName, rs.getInt("attr" + i + "_value"));
                }
            }

            IPlayer player;
            if ("Handball".equalsIgnoreCase(sportType)) {
                int throwPower = attrs.getOrDefault("ThrowPower", 50);
                int speed = attrs.getOrDefault("Speed", 50);
                int agility = attrs.getOrDefault("Agility", 50);
                int goalkeeping = attrs.getOrDefault("GoalkeepingSkill", 50);
                player = new HandballPlayer(name, throwPower, speed, agility, goalkeeping);
            } else {
                int headPower = attrs.getOrDefault("HeadPower", 50);
                int jumpHeight = attrs.getOrDefault("JumpHeight", 50);
                player = new HeadballPlayer(name, headPower, jumpHeight);
            }

            if (isInjured) player.setInjury(injuryDuration);
            team.addPlayer(player);
        }
    }

    private static void loadCoaches(Connection conn, long teamId, Team team, String sportType) throws SQLException {
        PreparedStatement ps = conn.prepareStatement("SELECT * FROM coaches WHERE team_id = ?");
        ps.setLong(1, teamId);
        ResultSet rs = ps.executeQuery();

        while (rs.next()) {
            String name = rs.getString("name");
            Map<String, Integer> specs = new java.util.HashMap<>();

            for (int i = 1; i <= 3; i++) {
                String specName = rs.getString("spec" + i + "_name");

                if (specName != null) {
                    specs.put(specName, rs.getInt("spec" + i + "_value"));
                }
            }

            ICoach coach;
            if ("Handball".equalsIgnoreCase(sportType)) {
                int throwSkill = specs.getOrDefault("ThrowPower", 50);
                int speedSkill = specs.getOrDefault("Speed", 50);
                int agilitySkill = specs.getOrDefault("Agility", 50);
                coach = new HandballCoach(name, throwSkill, speedSkill, agilitySkill);
            } else {
                int headSkill = specs.getOrDefault("HeadPower", 50);
                int jumpSkill = specs.getOrDefault("JumpHeight", 50);
                coach = new HeadballCoach(name, headSkill, jumpSkill);
            }
            team.addCoach(coach);
        }
    }

    private static void loadH2H(Connection conn, List<ITeam> teams) throws SQLException {
        ResultSet rs = conn.createStatement().executeQuery("SELECT * FROM h2h_records");
        while (rs.next()) {
            String teamName = rs.getString("team_name");
            String opponentName = rs.getString("opponent_name");
            int points = rs.getInt("points");

            Team team = (Team) teams.stream()
                .filter(t -> t.getTeamName().equals(teamName))
                .findFirst().orElse(null);
            Team opponent = (Team) teams.stream()
                .filter(t -> t.getTeamName().equals(opponentName))
                .findFirst().orElse(null);

            if (team != null && opponent != null) {
                team.recordHeadToHead(opponent, points);
            }
        }
    }
}
