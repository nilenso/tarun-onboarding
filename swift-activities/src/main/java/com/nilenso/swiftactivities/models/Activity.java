package com.nilenso.swiftactivities.models;

import jakarta.persistence.*;
import org.hibernate.annotations.DynamicInsert;

import java.time.Instant;
import java.util.Objects;
import java.util.UUID;

@Entity
@Table(name = "activity")
@DynamicInsert
public class Activity {
    public static enum ActivityType {
        Running,
        Swimming,
        Cycling
    }

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "activity_id")
    private UUID activityId;
    @Column(name = "user_id")
    private UUID userId;
    @Column(name = "name")
    private String name;
    @Column(name = "type", columnDefinition = "activity_type")
    @Enumerated(EnumType.STRING)
    private ActivityType type;
    @Column(name = "start_time")
    private Instant startTime;
    @Column(name = "end_time")
    private Instant endTime;
    @Column(name = "created_at")
    private Instant createdAt;
    @Column(name = "updated_at")
    private Instant updatedAt;

    public UUID getActivityId() {
        return activityId;
    }

    public void setActivityId(UUID activityId) {
        this.activityId = activityId;
    }

    public UUID getUserId() {
        return userId;
    }

    public void setUserId(UUID userId) {
        this.userId = userId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public ActivityType getType() {
        return type;
    }

    public void setType(ActivityType type) {
        this.type = type;
    }

    public Instant getStartTime() {
        return startTime;
    }

    public void setStartTime(Instant startTime) {
        this.startTime = startTime;
    }

    public Instant getEndTime() {
        return endTime;
    }

    public void setEndTime(Instant endTime) {
        this.endTime = endTime;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Instant createdAt) {
        this.createdAt = createdAt;
    }

    public Instant getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(Instant updatedAt) {
        this.updatedAt = updatedAt;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Activity activity = (Activity) o;
        return Objects.equals(activityId, activity.activityId)
                && Objects.equals(userId, activity.userId)
                && Objects.equals(name, activity.name)
                && type == activity.type
                && Objects.equals(startTime, activity.startTime)
                && Objects.equals(endTime, activity.endTime)
                && Objects.equals(createdAt, activity.createdAt)
                && Objects.equals(updatedAt, activity.updatedAt);
    }

    @Override
    public int hashCode() {
        return Objects.hash(activityId, userId, name, type, startTime, endTime, createdAt, updatedAt);
    }

    @Override
    public String toString() {
        return "Activity{" +
                "activityId=" + activityId +
                ", userId=" + userId +
                ", name='" + name + '\'' +
                ", type=" + type +
                ", startTime=" + startTime +
                ", endTime=" + endTime +
                ", createdAt=" + createdAt +
                ", updatedAt=" + updatedAt +
                '}';
    }
}
