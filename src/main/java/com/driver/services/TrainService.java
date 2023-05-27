package com.driver.services;

import com.driver.EntryDto.AddTrainEntryDto;
import com.driver.EntryDto.SeatAvailabilityEntryDto;
import com.driver.model.Passenger;
import com.driver.model.Station;
import com.driver.model.Ticket;
import com.driver.model.Train;
import com.driver.repository.TrainRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class TrainService {

    @Autowired
    TrainRepository trainRepository;

    public Integer addTrain(AddTrainEntryDto trainEntryDto){

        //Add the train to the trainRepository
        Train train = new Train();
        train.setNoOfSeats(trainEntryDto.getNoOfSeats());
        train.setDepartureTime(trainEntryDto.getDepartureTime());

        //and route String logic to be taken from the Problem statement.
        train.setRoute(getRoute(trainEntryDto.getStationRoute()));

        //Save the train and return the trainId that is generated from the database.
        Train savedTrain = this.trainRepository.save(train);

        //Avoid using the lombok library
        return savedTrain.getTrainId();
    }

    private String getRoute(List<Station> stationRoute) {
        StringBuilder sb = new StringBuilder();
        for( Station s : stationRoute ){
            sb.append(s);
            sb.append(",");
        }
        return sb.toString();
    }

    public Integer calculateAvailableSeats(SeatAvailabilityEntryDto seatAvailabilityEntryDto) throws Exception {


        // Check Train:
        Optional<Train> optionalTrain = this.trainRepository.findById(seatAvailabilityEntryDto.getTrainId());
        if( optionalTrain.isEmpty() ){
            throw new Exception("Train does not exist!!");
        }
        Train train = optionalTrain.get();


        // Check "FROM" and "TO" stations:
        String fromStation = String.valueOf(seatAvailabilityEntryDto.getFromStation());
        String toStation = String.valueOf(seatAvailabilityEntryDto.getToStation());
        String route = train.getRoute();

        boolean isFromStationPresent = false;
        boolean isToStationPresent = false;
        int startIdx = -1;                                 //store the index of starting point: HELP TO CALCULATE FAIR
        int endIdx = -1;                                   //store the index of ending point:
        String[] stops = route.split(",");

        for( int i = 0 ; i < stops.length ; i++ ){
            String stop = stops[i];
            if( stop.equals(fromStation) ){
                isFromStationPresent = true;
                startIdx = i;
            }
            if( stop.equals(toStation) ){
                isToStationPresent = true;
                endIdx = i;
            }
        }

        //In-case the train doesn't pass through the requested stations
        //throw new Exception("Invalid stations");
        if( !isFromStationPresent || !isToStationPresent ){
            throw new Exception("Invalid stations");
        }

        //Calculate the total seats available
        //Suppose the route is A B C D
        //And there are 2 seats available in total in the train
        //and 2 tickets are booked from A to C and B to D.
        //The seat is available only between A to C and A to B. If a seat is empty between 2 station
        // it will be counted to our final ans
        //even if that seat is booked post the destStation or before the boardingStation
        //In-short : a train has totalNo of seats and there are tickets from and to different locations
        //We need to find out the available seats between the given 2 stations.



       return null;
    }

    public Integer calculatePeopleBoardingAtAStation(Integer trainId,Station station) throws Exception{

        String boardingStation = String.valueOf(station);

        Optional<Train> optionalTrain = this.trainRepository.findById(trainId);
        if( optionalTrain.isEmpty() ){
            throw new Exception("Train does not exist!!");
        }
        Train train = optionalTrain.get();

        String route = train.getRoute();
        String[] stops = route.split(",");


        boolean isStation = false;
        for( String stop : stops ){
            if( stop.equals(boardingStation) )isStation = true;
        }

        //if the trainId is not passing through that station
        //throw new Exception("Train is not passing from this station");
        if( !isStation ){
            throw new Exception("Train is not passing from this station");
        }

        //We need to find out the number of people who will be boarding a train from a particular station
        //  in a happy case we need to find out the number of such people.
        int personCount = 0;
        List<Ticket> bookedTickets = train.getBookedTickets();
        for( Ticket t : bookedTickets ){
            String startStation = String.valueOf(t.getFromStation());
            if( startStation.equals(boardingStation) ){
                personCount += t.getPassengersList().size();
            }
        }

        return personCount;
    }

    public Integer calculateOldestPersonTravelling(Integer trainId) throws Exception{
        int maxAge = 0;
        Optional<Train> optionalTrain = this.trainRepository.findById(trainId);
//        if( optionalTrain.isEmpty() ){
//            throw new Exception("Train does not exist!!");
//        }
        Train train = optionalTrain.get();

        List<Ticket> bookedTickets = train.getBookedTickets();
        for( Ticket t : bookedTickets ){
                List<Passenger> passengerList = t.getPassengersList();
                for( Passenger p : passengerList ){
                    if( p.getAge() > maxAge )maxAge = p.getAge();
            }
        }

        //Throughout the journey of the train between any 2 stations
        //We need to find out the age of the oldest person that is travelling the train
        //If there are no people travelling in that train you can return 0

        return maxAge;
    }

    public List<Integer> trainsBetweenAGivenTime(Station station, LocalTime startTime, LocalTime endTime){

        //When you are at a particular station you need to find out the number of trains that will pass through a given station
        //between a particular time frame both start time and end time included.
        //You can assume that the date change doesn't need to be done ie the travel will certainly happen with the same date (More details
        //in problem statement)
        //You can also assume the seconds and milli seconds value will be 0 in a LocalTime format.

        return null;
    }

}
