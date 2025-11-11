package koto_thing;

import jakarta.persistence.*;

@Entity
public class Opinion {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    private String title;
    private String content;
    private int juenCount;
    
    @ManyToOne
    @JoinColumn(name = "topic_id")
    private Topic topic;
    
    public Opinion() { }
    
    public Opinion(String title, String content) {
        this.title = title;
        this.content = content;
        this.juenCount = 0;
    }
    
    public Long getId() {
        return id;
    }
    
    public String getTitle() {
        return title;
    }
    
    public String getContent() {
        return content;
    }
    
    public int getJuenCount() {
        return juenCount;
    }
    
    public Topic getTopic() {
        return topic;
    }
    
    public void setTopic(Topic topic) {
        this.topic = topic;
    }
    
    public void addJuen() {
        juenCount++;
    }
    
    @Override
    public String toString() {
        return title + " (Ju-en: " + juenCount + ")";
    }
}
