package koto_thing;

import java.util.HashSet;
import java.util.Set;

/**
 * クライアント側のOpinionモデルクラス（JPA非依存）
 */
public class Opinion {
    private Long id;
    private String title;
    private String content;
    private int juenCount;
    private Set<String> juenedUsers = new HashSet<>();
    private Long topicId;

    public Opinion() {
    }

    public Opinion(String title, String content) {
        this.title = title;
        this.content = content;
        this.juenCount = 0;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public int getJuenCount() {
        return juenCount;
    }

    public void setJuenCount(int juenCount) {
        this.juenCount = juenCount;
    }

    public Set<String> getJuenedUsers() {
        return juenedUsers;
    }

    public void setJuenedUsers(Set<String> juenedUsers) {
        this.juenedUsers = juenedUsers;
    }

    public Long getTopicId() {
        return topicId;
    }

    public void setTopicId(Long topicId) {
        this.topicId = topicId;
    }

    @Override
    public String toString() {
        return title + " (Ju-en: " + juenCount + ")";
    }
}

