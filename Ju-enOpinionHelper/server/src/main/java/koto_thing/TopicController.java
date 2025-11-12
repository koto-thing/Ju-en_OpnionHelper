package koto_thing;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/topics")
public class TopicController {
    private final TopicService topicService;
    private final TopicRepository topicRepository;
    private final OpinionRepository opinionRepository;

    public TopicController(TopicService topicService, TopicRepository topicRepository, OpinionRepository opinionRepository) {
        this.topicService = topicService;
        this.topicRepository = topicRepository;
        this.opinionRepository = opinionRepository;
    }
    
    @GetMapping
    public List<Topic> getAllTopics() {
        return topicService.getAllTopics();
    }
    
    @PostMapping
    public Topic createTopic(@RequestBody Map<String, String> payload) {
        Topic topic = new Topic();
        topic.setName(payload.get("name"));
        return topicRepository.save(topic);
    }
    
    @DeleteMapping("/{topicId}")
    public ResponseEntity<?> deleteTopic(@PathVariable Long topicId) {
        topicRepository.deleteById(topicId);
        return ResponseEntity.ok().build();
    }
    
    @GetMapping("/{id}/opinions")
    public List<Opinion> getOpinions(@PathVariable Long id) {
        return topicService.getOpinionsByTopicId(id);
    }
    
    @PostMapping("/{id}/opinions")
    public Opinion createOpinion(@PathVariable Long id, @RequestBody Opinion opinion) {
        return topicService.createOpinion(id, opinion);
    }
    
    @DeleteMapping("/{topicId}/opinions/{opinionId}")
    public ResponseEntity<?> deleteOpinion(@PathVariable Long topicId, @PathVariable Long opinionId) {
        opinionRepository.deleteById(opinionId);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/topics/{topicId}/opinions/{opinionId}/juen")
    public ResponseEntity<?> addJuen(@PathVariable Long topicId,
                                     @PathVariable Long opinionId,
                                     @RequestParam String userId) {
        Opinion opinion = opinionRepository.findById(opinionId)
                .orElseThrow(() -> new RuntimeException("Opinion not found"));

        if (opinion.getJuenedUsers().contains(userId)) {
            return ResponseEntity.badRequest().body("既にJu-enを押しています。");
        }

        opinion.getJuenedUsers().add(userId);
        opinion.setJuenCount(opinion.getJuenCount() + 1);
        opinionRepository.save(opinion);

        return ResponseEntity.ok(opinion);
    }
}
