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

        //3.Train:
        Optional<Train> optionalTrain = this.trainRepository.findById(bookTicketEntryDto.getTrainId());
        if( optionalPassenger.isEmpty() ){
            throw new Exception("Train does not exist!!");
        }



        //Use bookedTickets List from the TrainRepository to get bookings done against that train
        //In-case the there are insufficient tickets
        //throw new Exception("Less tickets are available");
        //otherwise book the ticket, calculate the price and other details
        //Save the information in corresponding DB Tables
        //Fare System : Check problem statement
        //In-case the train doesn't pass through the requested stations
        //throw new Exception("Invalid stations");
        //Save the bookedTickets in the train Object
        //Also in the passenger Entity change the attribute bookedTickets by using the attribute bookingPersonId.
       //And the end return the ticketId that has come from db

       return null;

    }
}
