package com.team14.sportsmanager.model;

import com.team14.sportsmanager.core.ISport;
import com.team14.sportsmanager.core.ITeam;

package com.team14.sportsmanager.model;
import com.team14.sportsmanager.core.*;
import java.util.*;

public class HeadballSport implements ISport {
    private int headPowerRule = 100;

    @Override public String getSportName() { return "Headball"; }
    @Override public int getPeriodCount() { return 4; }
    
    @Override
    public Map<String, Integer> getScoringRules() {
        Map<String, Integer> rules = new HashMap<>();
        rules.put("Win", 2);
        rules.put("Draw", 1);
        rules.put("Loss", 0);
        return rules;
    }

    @Override
    public Comparator<ITeam> getStandingLogic() {
        return (t1, t2) -> {
            int pointCompare = Integer.compare(t2.getTotalPoints(), t1.getTotalPoints());
            if (pointCompare != 0) return pointCompare;

            int h2hCompare = Integer.compare(t2.getHeadToHeadPoints(t1), t1.getHeadToHeadPoints(t2));
            if (h2hCompare != 0) return h2hCompare;

            int goalCompare = Integer.compare(t2.getGoalDifference(), t1.getGoalDifference());
            if (goalCompare != 0) return goalCompare;

            return new Random().nextBoolean() ? 1 : -1;
        };
    }

    public void jumpLogic() { System.out.println("Headball jump executed."); }
}
