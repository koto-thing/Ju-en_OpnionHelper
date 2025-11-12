package koto_thing;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class TopicManager {
    private static TopicManager instance;
    private Map<String, ArrayList<Opinion>> topicOpinions;
    
    private TopicManager() {
        topicOpinions = new HashMap<>();
    }
    
    public static TopicManager getInstance() {
        if (instance == null) {
            instance = new TopicManager();
        }
        
        return instance;
    }
    
    public ArrayList<Opinion> getOpinions(String topicName) {
        topicOpinions.putIfAbsent(topicName, new ArrayList<>());
        return topicOpinions.get(topicName);
    }
    
    public void addOpinion(String topicName, Opinion opinion) {
        ArrayList<Opinion> opinions = getOpinions(topicName);
        opinions.add(opinion);
    }
}
