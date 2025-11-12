package koto_thing;

import java.util.ArrayList;
import java.util.List;

/**
 * クライアント側のTopicモデルクラス（JPA非依存）
 */
public class Topic {
    private Long id;
    private String name;
    private List<Opinion> opinions = new ArrayList<>();

    public Topic() {
    }

    public Topic(Long id, String name) {
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

    @Override
    public String toString() {
        return name;
    }
}

