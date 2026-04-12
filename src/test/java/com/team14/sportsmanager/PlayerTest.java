package com.team14.sportsmanager;
import com.team14.sportsmanager.model.HeadballPlayer;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class PlayerTest {

    @Test
    public void testInitialInjuryStatusIsFalse() {
        HeadballPlayer player = new HeadballPlayer("Burak", 85, 90);
        assertFalse(player.isInjured(), "Player should not be injured initially.");
    }

    @Test
    public void testSetInjuryChangesStatusAndDuration() {
        HeadballPlayer player = new HeadballPlayer("Burak", 85, 90);
        player.setInjury(3);
        assertTrue(player.isInjured(), "Player should be injured after setInjury.");
        assertEquals(3, player.getRemainingInjuryDuration(), "Injury duration should be 3.");
    }

    @Test
    public void testDynamicAttributeAddition() {
        HeadballPlayer player = new HeadballPlayer("Burak", 85, 90);
        assertEquals(2, player.getAttributes().size());

        player.updateAttribute("Speed", 75);

        assertEquals(75, player.getAttributes().get("Speed"));
        assertEquals(3, player.getAttributes().size());
    }

    @Test
    public void testUpdateExistingAttribute() {
        HeadballPlayer player = new HeadballPlayer("Burak", 85, 90);
        player.updateAttribute("HeadPower", 95);

        assertEquals(95, player.getAttributes().get("HeadPower"));
        assertEquals(2, player.getAttributes().size());
    }
}