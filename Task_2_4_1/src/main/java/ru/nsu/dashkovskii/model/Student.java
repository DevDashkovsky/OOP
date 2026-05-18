package ru.nsu.dashkovskii.model;

/**
 * Студент с github-аккаунтом и ссылкой на репозиторий.
 */
public final class Student {
    private final String githubNick;
    private String fullName;
    private String repoUrl;

    public Student(String githubNick) {
        this.githubNick = githubNick;
    }

    public String getGithubNick() {
        return githubNick;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getRepoUrl() {
        return repoUrl;
    }

    public void setRepoUrl(String repoUrl) {
        this.repoUrl = repoUrl;
    }
}
