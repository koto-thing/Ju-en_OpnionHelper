package koto_thing;

import jakarta.persistence.*;

import java.util.ArrayList;
import java.util.List;

@Entity
public class Topic {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    private String name;
    
    @OneToMany(mappedBy = "topic", cascade = CascadeType.ALL)
    private List<Opinion> opinions = new ArrayList<>();
    
    public Topic() {
        
    }
    
    public Topic(String name){
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
}
