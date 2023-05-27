package com.driver.controllers;

import com.driver.EntryDto.AddTrainEntryDto;
import com.driver.EntryDto.SeatAvailabilityEntryDto;
import com.driver.model.Station;
import com.driver.services.TrainService;
import io.swagger.models.auth.In;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalTime;
import java.util.List;

@RestController
@RequestMapping("/train")
public class TrainController {

    @Autowired
    TrainService trainService;

    @PostMapping("/add")
    public Integer addTrain(@RequestBody AddTrainEntryDto train){

        //We need to return the trainId of the newly added train
        Integer trainId = trainService.addTrain(train);
        return trainId;

    }

    @GetMapping("/calculate-avaiable-seats")
    public ResponseEntity checkSeatAvailability(@RequestBody SeatAvailabilityEntryDto seatAvailabilityEntryDto) {
        try{
            Integer count = trainService.calculateAvailableSeats(seatAvailabilityEntryDto);
            return new ResponseEntity(count, HttpStatus.OK);
        }
        catch (Exception e){
            return new ResponseEntity(e.getMessage(), HttpStatus.BAD_REQUEST);
        }

    }

    @GetMapping("/calculate-people-onboarding")
    public ResponseEntity calculatePeopleOnBoarding(@RequestParam("trainId")Integer trainId, @RequestParam("station") Station station){

        try{
            Integer count = trainService.calculatePeopleBoardingAtAStation(trainId,station);
            return new ResponseEntity(count, HttpStatus.FOUND);
        }catch (Exception e){
            return new ResponseEntity(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    @GetMapping("/calculate-oldest-person-travelling/{trainId}")
    public ResponseEntity calculateOldestPersonTravelling(@PathVariable("trainId")Integer trainId){

        //We need to find out the oldest person Travellign

        try{
            Integer age = trainService.calculateOldestPersonTravelling(trainId);
            return new ResponseEntity(age, HttpStatus.FOUND);
        }catch (Exception e){
            return new ResponseEntity(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    @GetMapping("get-list-of-trains-arriving-in-a-range-of-time")
    public List<Integer> calculateListOfTrainIdsAtAStationInAParticularTimeRange(@RequestParam("station")Station station,
                                                                                 @RequestParam("startTime")LocalTime startTime
                                                                                 ,@RequestParam("endTime")LocalTime endTime){

        return trainService.trainsBetweenAGivenTime(station,startTime,endTime);
    }
}
