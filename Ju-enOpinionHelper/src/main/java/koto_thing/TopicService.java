package koto_thing;

import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class TopicService {
    private final TopicRepository topicRepository;
    private final OpinionRepository opinionRepository;

    public TopicService(TopicRepository topicRepository, OpinionRepository opinionRepository) {
        this.topicRepository = topicRepository;
        this.opinionRepository = opinionRepository;
    }

    public List<Topic> getAllTopics() {
        return topicRepository.findAll();
    }

    public Topic createTopic(Topic topic) {
        return topicRepository.save(topic);
    }

    public List<Opinion> getOpinionsByTopicId(Long topicId) {
        Topic topic = topicRepository.findById(topicId)
                .orElseThrow(() -> new RuntimeException("Topic not found"));
        return topic.getOpinions();
    }

    public Opinion createOpinion(Long topicId, Opinion opinion) {
        Topic topic = topicRepository.findById(topicId)
                .orElseThrow(() -> new RuntimeException("Topic not found"));
        opinion.setTopic(topic);
        return opinionRepository.save(opinion);
    }
}
