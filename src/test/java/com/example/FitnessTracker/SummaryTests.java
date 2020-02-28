package com.example.FitnessTracker;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
public class SummaryTests {
    private final SummaryRepository summaryRepository;
    private final SummaryController summaryController;


    @Autowired
    public SummaryTests( SummaryController sc,SummaryRepository sr){
        summaryRepository = sr;
        summaryController =sc;
    }
    //-------------------------------------------------------------------------
    // Controller: SummaryController
    // Endpoints:
    //		  VERB | URL                          | METHOD
    //      -------+------------------------------+--------------
    //		  GET  | /summary/get                 | getSummary(integer)
    //-------------------------------------------------------------------------
    @Test
    void happyGetSummary(){
        Summary summary = new Summary();
        summary.setSummaryID(1);
        summary.setUserID(1);
        summary.setPace(420.2);
        summary.setDistance(10.0);
        summaryRepository.save(summary);

        String input = summaryController.getSummary(1);
        Assertions.assertEquals(input,"Total distance of this user:10.0\n" +
                "Longest run of this user:10.0\n" +
                "Fastest run of this user:07:0");
    }
    //passing in an invalid userID
    @Test
    void unhappyGetSummary(){
        String input = summaryController.getSummary(345678);
        Assertions.assertEquals(input,"No available data on user or user does not exist");

    }
    //user exists but no data in summary
    @Test
    void unhappy2GetSummary(){
        String input = summaryController.getSummary(2);
        Assertions.assertEquals(input,"No available data on user or user does not exist");
    }

}
