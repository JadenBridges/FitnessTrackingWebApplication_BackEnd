package com.example.FitnessTracker.controllers;

import com.example.FitnessTracker.model.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
public class ActivityController {
    @Autowired
    private ActivityRepository activityRepository;
    @Autowired
    private PostRepository postRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private SummaryController summaryController;

    //---------------------------------------------------------------
    // Method:  createActivity
    // Purpose: To create and store an activity
    // Inputs:  Activity
    // Output:  void
    //---------------------------------------------------------------
    @PostMapping("/activity/create")
    public int createActivity(@RequestBody Activity activity) {
        if(!userRepository.findById(activity.getUserID()).isPresent()){
            return -1;
        }
        else if(activity.getDistance() < 0 || activity.getHours() < 0 || activity.getMinutes() < 0 || activity.getSeconds() < 0){
            return -1;
        }
        else{
            activityRepository.save(activity);
            summaryController.updateSummary(activity);
            postRepository.save(new Post(activity, 0));
            return 1;
        }
    }

    //---------------------------------------------------------------
    // Method:  updateActivity
    // Purpose: To update an activity
    // Inputs:  activityID, Activity
    // Output:  void
    //---------------------------------------------------------------
    @PutMapping("/activity/update")
    public int updateActivity(@RequestParam int activityID, @RequestBody Activity activity) {
        if (activity.getDistance() < 0 || activity.getHours() < 0 || activity.getMinutes() < 0 || activity.getSeconds() < 0) {
            return -1;
        } else if (activityRepository.findById(activityID).isPresent()) {
            Activity oldActivity = activityRepository.findById(activityID).get();
            oldActivity.setUserID(activity.getUserID());
            oldActivity.setTitle(activity.getTitle());
            oldActivity.setDescription(activity.getDescription());
            oldActivity.setDistance(activity.getDistance());
            oldActivity.setHours(activity.getHours());
            oldActivity.setMinutes(activity.getMinutes());
            oldActivity.setSeconds(activity.getSeconds());
            activityRepository.save(oldActivity);
            return 1;
        }

        return -1;
    }
}
