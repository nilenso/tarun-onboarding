package com.nilenso.swiftactivities.services;

import com.nilenso.swiftactivities.models.Activity;
import com.nilenso.swiftactivities.models.dtos.StartActivityDto;
import com.nilenso.swiftactivities.repositories.ActivityRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Instant;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ActivityServiceImplTest {
    @InjectMocks
    ActivityServiceImpl activityService;

    ActivityRepository activityRepository = mock(ActivityRepository.class);

    @Captor
    ArgumentCaptor<Activity> captor;

    @Test
    void addActivity() {
        var now = Instant.now();

        var startActivityDto = new StartActivityDto("", Activity.ActivityType.Running);
        when(activityRepository.save(any())).thenReturn(new Activity());

        try (MockedStatic<Instant> mockedStatic = mockStatic(Instant.class)) {
           mockedStatic.when(Instant::now).thenReturn(now);
           activityService.addActivity(startActivityDto);
        }

        Mockito.verify(activityRepository).save(captor.capture());
        Activity capturedActivity = captor.getValue();
        assertEquals(startActivityDto.type(), capturedActivity.getType());
        assertEquals(startActivityDto.name(), capturedActivity.getName());
        assertEquals(now, capturedActivity.getStartTime());
    }
}