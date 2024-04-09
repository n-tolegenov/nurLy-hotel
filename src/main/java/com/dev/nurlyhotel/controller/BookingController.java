package com.dev.nurlyhotel.controller;

import com.dev.nurlyhotel.dto.BookingDTO;
import com.dev.nurlyhotel.dto.RoomDTO;
import com.dev.nurlyhotel.exception.InvalidBookingRequestException;
import com.dev.nurlyhotel.exception.ResourceNotFoundException;
import com.dev.nurlyhotel.model.BookedRoom;
import com.dev.nurlyhotel.model.Room;
import com.dev.nurlyhotel.service.IBookingService;
import com.dev.nurlyhotel.service.IRoomService;
import com.dev.nurlyhotel.service.RoomService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

// @CrossOrigin позволяет выполнять запросы между разными источниками
@CrossOrigin("http://localhost:5173")
@RequiredArgsConstructor
@RestController
@RequestMapping("/bookings")
public class BookingController {
    private final IBookingService bookingService;
    private final IRoomService roomService;

    @GetMapping("/all-bookings")
    public ResponseEntity<List<BookingDTO>> getAllBookings(){
        List<BookedRoom> bookings = bookingService.getAllBookings();
        List<BookingDTO> bookingsDTO = new ArrayList<>();
        for(BookedRoom booking: bookings){
            BookingDTO bookingDTO = getBookingDTO(booking);
            bookingsDTO.add(bookingDTO);
        }
        return ResponseEntity.ok(bookingsDTO);
    }

    @GetMapping("/confirmation/{confirmationCode}")
    public ResponseEntity<?> getBookingByConfirmationCode(@PathVariable String confirmationCode){
        try{
            BookedRoom booking = bookingService.findByBookingConfirmationCode(confirmationCode);
            BookingDTO bookingDTO = getBookingDTO(booking);
            return ResponseEntity.ok(bookingDTO);
        }catch (ResourceNotFoundException ex){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ex.getMessage());
        }
    }

    @PostMapping("/room/{roomId}/booking")
    public ResponseEntity<?> saveBooking(@PathVariable Long roomId,
                                         @RequestBody BookedRoom bookingRequest){
        try{
            String confirmationCode = bookingService.saveBooking(roomId, bookingRequest);
            return ResponseEntity.ok("Room booked successfully! Your booking confirmation code is: " + confirmationCode);
        }catch (InvalidBookingRequestException e){
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @DeleteMapping("/booking/{bookingId}/delete")
    public void cancelBooking(@PathVariable Long bookingId){
        bookingService.cancelBooking(bookingId);
    }

    private BookingDTO getBookingDTO(BookedRoom booking) {
        Room room = roomService.getRoomById(booking.getRoom().getId()).get();
        RoomDTO roomDTO = new RoomDTO(
                room.getId(),
                room.getRoomType(),
                room.getRoomPrice());
        return new BookingDTO(
                booking.getBookingId(), booking.getCheckInDate(),
                booking.getCheckOutDate(), booking.getGuestFullName(),
                booking.getGuestEmail(), booking.getNumOfAdults(),
                booking.getNumOfChildren(), booking.getTotalNumOfGuest(),
                booking.getBookingConfirmationCode(), roomDTO);
    }
}
