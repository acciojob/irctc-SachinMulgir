package com.driver.services;


import com.driver.EntryDto.BookTicketEntryDto;
import com.driver.model.Passenger;
import com.driver.model.Ticket;
import com.driver.model.Train;
import com.driver.repository.PassengerRepository;
import com.driver.repository.TicketRepository;
import com.driver.repository.TrainRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class TicketService {

    @Autowired
    TicketRepository ticketRepository;

    @Autowired
    TrainRepository trainRepository;

    @Autowired
    PassengerRepository passengerRepository;


    public Integer bookTicket(BookTicketEntryDto bookTicketEntryDto) throws Exception{

        //Check for validity
        //1. Check if all passengers exist or not:
        for( int id : bookTicketEntryDto.getPassengerIds()){
            Optional<Passenger> passengerOptional = this.passengerRepository.findById(id);
            if( passengerOptional.isEmpty() ){
                throw new Exception("Passenger with id : " + id + " doesn't exist");
            }
        }

        //2.check bookingPersonId:
        Optional<Passenger> optionalPassenger = this.passengerRepository.findById(bookTicketEntryDto.getBookingPersonId());
        if( optionalPassenger.isEmpty() ){
            throw new Exception("Booking person does not exist!!");
        }
        Passenger bookingPerson = optionalPassenger.get();

        //3.Train:
        Optional<Train> optionalTrain = this.trainRepository.findById(bookTicketEntryDto.getTrainId());
        if( optionalPassenger.isEmpty() ){
            throw new Exception("Train does not exist!!");
        }
        Train train = optionalTrain.get();

        //4. "from" and "to" station:
        String fromStation = String.valueOf(bookTicketEntryDto.getFromStation());
        String toStation = String.valueOf(bookTicketEntryDto.getToStation());
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


        //5. No. of seats:
        //Use bookedTickets List from the TrainRepository to get bookings done against that train
        //In-case the there are insufficient tickets
        //throw new Exception("Less tickets are available")
        List<Ticket> bookedTickets = train.getBookedTickets();
        int totalSeatsInTrain = train.getNoOfSeats();

        int bookedSeats = 0;
        for( Ticket t : bookedTickets ){
            bookedSeats += t.getPassengersList().size();
        }

        int remainingSeats = totalSeatsInTrain - bookedSeats;
        if( remainingSeats < bookTicketEntryDto.getNoOfSeats() ){
            throw new Exception("Less tickets are available");
        }

        //otherwise book the ticket, calculate the price and other details
        //Save the information in corresponding DB Tables
        //Fare System : Check problem statement
        int singleFair = (endIdx - startIdx) * 300;             //300 for each next station:

        Ticket ticket = new Ticket();
        ticket.setFromStation(bookTicketEntryDto.getFromStation());
        ticket.setToStation(bookTicketEntryDto.getToStation());
        ticket.setTrain(train);
        ticket.setTotalFare(bookTicketEntryDto.getNoOfSeats()*singleFair);
        Ticket savedTicket = this.ticketRepository.save(ticket);

        //Save the bookedTickets in the train Object
        //Also in the passenger Entity change the attribute bookedTickets by using the attribute bookingPersonId.
        train.getBookedTickets().add(savedTicket);

        bookingPerson.getBookedTickets().add(savedTicket);


       //And the end return the ticketId that has come from db
       return ticket.getTicketId();

    }
}
