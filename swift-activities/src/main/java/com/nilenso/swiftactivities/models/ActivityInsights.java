package com.nilenso.swiftactivities.models;

import com.nilenso.swiftactivities.models.dtos.ActivityInsightsDto;
import io.hypersistence.utils.hibernate.type.json.JsonType;
import jakarta.persistence.*;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.Type;

import java.time.Instant;
import java.util.Objects;
import java.util.UUID;

@Entity
@Table(name = "activity_insights")
@DynamicInsert
public class ActivityInsights {
    @Id
    @Column(name = "activity_id")
    private UUID activityId;

    @Type(JsonType.class)
    @Column(name = "stats")
    private ActivityInsightsDto stats;

    @Column(name = "created_at")
    private Instant createdAt;

    public UUID getActivityId() {
        return activityId;
    }

    public void setActivityId(UUID activityId) {
        this.activityId = activityId;
    }

    public ActivityInsightsDto getStats() {
        return stats;
    }

    public void setStats(ActivityInsightsDto stats) {
        this.stats = stats;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Instant createdAt) {
        this.createdAt = createdAt;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ActivityInsights that = (ActivityInsights) o;
        return Objects.equals(activityId, that.activityId) && Objects.equals(stats, that.stats) && Objects.equals(createdAt, that.createdAt);
    }

    @Override
    public int hashCode() {
        return Objects.hash(activityId, stats, createdAt);
    }

    @Override
    public String toString() {
        return "ActivityInsights{" +
                "activityId=" + activityId +
                ", stats=" + stats +
                ", createdAt=" + createdAt +
                '}';
    }
}
