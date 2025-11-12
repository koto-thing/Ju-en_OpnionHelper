package koto_thing;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;

import java.util.ArrayList;
import java.util.List;

@Entity
public class Topic {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    private String name;
    
    @JsonBackReference
    @OneToMany(mappedBy = "topic", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Opinion> opinions = new ArrayList<>();
    
    public Topic() {
        
    }
    
    public Topic(Long id, String name){
        this.id = id;
        this.name = name;
    }
    
    public Long getId() {
        return id;
    }
    
    public void setId(Long id) {
        this.id = id;
    }
    
    public String getName() {
        return name;
    }
    
    public void setName(String name) {
        this.name = name;
    }
    
    public List<Opinion> getOpinions() {
        return opinions;
    }
    
    public void setOpinions(List<Opinion> opinions) {
        this.opinions = opinions;
    }
    
    public void addOpinion(Opinion opinion) {
        opinions.add(opinion);
        opinion.setTopic(this);
    }
    
    @Override
    public String toString() {
        return name;
    }
}
