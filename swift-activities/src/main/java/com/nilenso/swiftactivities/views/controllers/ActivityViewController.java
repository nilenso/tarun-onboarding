package com.nilenso.swiftactivities.views.controllers;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import java.util.UUID;

@Controller
public class ActivityViewController {
    @GetMapping("/activity")
    public String startActivityView() {
        return "newActivity";
    }

    @GetMapping("/activity/{activityId}")
    public String activityView(@PathVariable UUID activityId,
                               Model model) {
        model.addAttribute("activityId", activityId);
        return "activity";
    }

}