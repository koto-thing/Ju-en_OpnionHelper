package koto_thing;

import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/topics")
public class TopicController {
    private final TopicService topicService;
    
    public TopicController(TopicService topicService) {
        this.topicService = topicService;
    }
    
    @GetMapping
    public List<Topic> getAllTopics() {
        return topicService.getAllTopics();
    }
    
    @PostMapping
    public Topic createTopic(@RequestBody Topic topic) {
        return topicService.createTopic(topic);
    }
    
    @GetMapping("/{id}/opinions")
    public List<Opinion> getOpinions(@PathVariable Long id) {
        return topicService.getOpinionsByTopicId(id);
    }
    
    @PostMapping("/{id}/opinions")
    public Opinion createOpinion(@PathVariable Long id, @RequestBody Opinion opinion) {
        return topicService.createOpinion(id, opinion);
    }
}
