package com.nilenso.swiftactivities.services;

import com.nilenso.swiftactivities.models.Activity;
import com.nilenso.swiftactivities.models.dtos.StartActivityDto;
import com.nilenso.swiftactivities.repositories.ActivityRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

import static java.util.UUID.randomUUID;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ActivityServiceImplTest {
    @InjectMocks
    ActivityServiceImpl activityService;

    ActivityRepository activityRepository = mock(ActivityRepository.class);

    @Captor
    ArgumentCaptor<Activity> addActivityCaptor, endActivityCaptor;
    @Captor
    ArgumentCaptor<UUID> findByIdCaptor;

    @Test
    void addActivity() {
        var now = Instant.now();

        var startActivityDto = new StartActivityDto("", Activity.ActivityType.Running);
        when(activityRepository.save(any())).thenReturn(new Activity());

        try (MockedStatic<Instant> mockedStatic = mockStatic(Instant.class)) {
            mockedStatic.when(Instant::now).thenReturn(now);
            activityService.addActivity(startActivityDto);
        }

        Mockito.verify(activityRepository).save(addActivityCaptor.capture());
        Activity capturedActivity = addActivityCaptor.getValue();
        assertEquals(startActivityDto.type(), capturedActivity.getType());
        assertEquals(startActivityDto.name(), capturedActivity.getName());
        assertEquals(now, capturedActivity.getStartTime());
    }

    @Test
    void endActivity() {
        var now = Instant.now();
        var activityId = randomUUID();

        when(activityRepository.findById(any())).thenReturn(Optional.of(new Activity()));

        when(activityRepository.save(any())).then(invocationOnMock ->
                invocationOnMock.getArgument(0));

        try (MockedStatic<Instant> mockedStatic = mockStatic(Instant.class)) {
            mockedStatic.when(Instant::now).thenReturn(now);
            activityService.endActivity(activityId);
        }

        Mockito.verify(activityRepository).findById(findByIdCaptor.capture());
        UUID capturedActivityId = findByIdCaptor.getValue();
        assertEquals(activityId, capturedActivityId);

        Mockito.verify(activityRepository).save(endActivityCaptor.capture());
        Activity capturedActivity = endActivityCaptor.getValue();
        assertEquals(now, capturedActivity.getEndTime());
    }
}