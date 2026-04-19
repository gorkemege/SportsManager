package com.team14.sportsmanager;

import com.team14.sportsmanager.model.HeadballCoach;
import com.team14.sportsmanager.model.HeadballPlayer;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class CoachTest {

    @Test
    void trainingIncreasesMatchingAttributes() {
        HeadballPlayer player = new HeadballPlayer("Test Player", 50, 60);
        HeadballCoach coach = new HeadballCoach("Test Coach", 80, 80);

        int beforeHead = player.getAttributes().get("HeadPower");
        int beforeJump = player.getAttributes().get("JumpHeight");

        coach.train(player);

        assertTrue(player.getAttributes().get("HeadPower") > beforeHead);
        assertTrue(player.getAttributes().get("JumpHeight") > beforeJump);
    }

    @Test
    void trainingCapsAt100() {
        HeadballPlayer player = new HeadballPlayer("Maxed Player", 100, 100);
        HeadballCoach coach = new HeadballCoach("Test Coach", 100, 100);

        coach.train(player);

        assertEquals(100, player.getAttributes().get("HeadPower"));
        assertEquals(100, player.getAttributes().get("JumpHeight"));
    }

    @Test
    void trainingIgnoresAttributesNotInSpecialties() {
        HeadballPlayer player = new HeadballPlayer("Test Player", 50, 50);
        player.updateAttribute("UnrelatedSkill", 40);

        HeadballCoach coach = new HeadballCoach("Test Coach", 80, 80);
        coach.train(player);

        assertEquals(40, player.getAttributes().get("UnrelatedSkill"));
    }

    @Test
    void coachSpecialtiesExposed() {
        HeadballCoach coach = new HeadballCoach("Expert", 90, 70);

        assertEquals(90, coach.getSpecialties().get("HeadPower"));
        assertEquals(70, coach.getSpecialties().get("JumpHeight"));
    }
}