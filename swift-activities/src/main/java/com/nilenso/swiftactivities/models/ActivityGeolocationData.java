package com.nilenso.swiftactivities.models;

import com.nilenso.swiftactivities.models.dtos.GeolocationDto;
import io.hypersistence.utils.hibernate.type.json.JsonType;
import jakarta.persistence.*;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.Type;

import java.math.BigInteger;
import java.time.Instant;
import java.util.Objects;
import java.util.UUID;

@Entity
@Table(name = "activity_geolocation_data")
@DynamicInsert
public class ActivityGeolocationData {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "geolocation_data_id")
    private UUID geolocationDataId;
    @Column(name = "activity_id")
    private UUID activityId;
    @Type(JsonType.class)
    @Column(name = "data")
    private GeolocationDto data;
    @Column(name = "recorded_at")
    private Instant recordedAt;
    @Column(name = "created_at")
    private Instant createdAt;

    public UUID getGeolocationDataId() {
        return geolocationDataId;
    }

    public void setGeolocationDataId(UUID geolocationDataId) {
        this.geolocationDataId = geolocationDataId;
    }

    public UUID getActivityId() {
        return activityId;
    }

    public void setActivityId(UUID activityId) {
        this.activityId = activityId;
    }

    public GeolocationDto getData() {
        return data;
    }

    public void setData(GeolocationDto data) {
        this.data = data;
    }

    public Instant getRecordedAt() {
        return recordedAt;
    }

    public void setRecordedAt(Instant recordedAt) {
        this.recordedAt = recordedAt;
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
        ActivityGeolocationData that = (ActivityGeolocationData) o;
        return Objects.equals(geolocationDataId, that.geolocationDataId) && Objects.equals(activityId, that.activityId) && Objects.equals(data, that.data) && Objects.equals(recordedAt, that.recordedAt) && Objects.equals(createdAt, that.createdAt);
    }

    @Override
    public int hashCode() {
        return Objects.hash(geolocationDataId, activityId, data, recordedAt, createdAt);
    }

    @Override
    public String toString() {
        return "ActivityGeolocationData{" +
                "geolocationDataId=" + geolocationDataId +
                ", activityId=" + activityId +
                ", data=" + data +
                ", recordedAt=" + recordedAt +
                ", createdAt=" + createdAt +
                '}';
    }
}
