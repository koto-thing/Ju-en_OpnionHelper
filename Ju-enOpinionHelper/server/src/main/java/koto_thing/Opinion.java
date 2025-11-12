package koto_thing;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;

import java.util.HashSet;
import java.util.Set;

@Entity
public class Opinion {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    private String title;
    private String content;
    private int juenCount;

    @ElementCollection
    private Set<String> juenedUsers = new HashSet<>();
    
    @ManyToOne
    @JoinColumn(name = "topic_id")
    @JsonBackReference
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
    
    public Set<String> getJuenedUsers() {
        return juenedUsers;
    }
    
    public void setTopic(Topic topic) {
        this.topic = topic;
    }
    
    public void setId(Long id) {
        this.id = id;
    }
    
    public void setTitle(String title) {
        this.title = title;
    }
    
    public void setContent(String content) {
        this.content = content;
    }
    
    public void setJuenCount(int juenCount) {
        this.juenCount = juenCount;
    }
    
    public void addJuen() {
        juenCount++;
    }
    
    public void setJuenedUsers(Set<String> juenedUsers) {
        this.juenedUsers = juenedUsers;
    }
    
    public void addJuenedUser(String username) {
        juenedUsers.add(username);
    }
    
    @Override
    public String toString() {
        return title + " (Ju-en: " + juenCount + ")";
    }
}
