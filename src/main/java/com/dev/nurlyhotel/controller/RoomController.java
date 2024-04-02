package com.dev.nurlyhotel.controller;

import com.dev.nurlyhotel.dto.BookingDTO;
import com.dev.nurlyhotel.dto.RoomDTO;
import com.dev.nurlyhotel.exception.PhotoRetrievealException;
import com.dev.nurlyhotel.model.BookedRoom;
import com.dev.nurlyhotel.model.Room;
import com.dev.nurlyhotel.service.BookingService;
import com.dev.nurlyhotel.service.IRoomService;
import lombok.RequiredArgsConstructor;
import org.apache.tomcat.util.codec.binary.Base64;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.math.BigDecimal;
import java.sql.Blob;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

@RestController
/*
   @RequiredArgsConstructor
   Generates a constructor with required arguments.
   Required arguments are final fields and fields with constraints such as @NonNull.
*/
@RequiredArgsConstructor
@RequestMapping("/rooms")
public class RoomController {

    private final IRoomService roomService;
    private final BookingService bookingService;

    /*
    Response Entity охватывает весь HTTP-ответ, включая Response Body, HTTP-заголовки ответа (response headers), и код состояния HTTP.
    Это позволяет включать в ответ дополнительную информацию о состоянии, типе контента, дате, безопасности и других аспектах HTTP-протокола.
    */
    @PostMapping("/add/new-room")
    public ResponseEntity<RoomDTO> addNewRoom(
            @RequestParam("photo") MultipartFile photo,
            @RequestParam("roomType") String roomType,
            @RequestParam("roomPrice") BigDecimal roomPrice) throws SQLException, IOException {

        Room savedRoom = roomService.addNewRoom(photo, roomType, roomPrice);
        RoomDTO roomDTO = new RoomDTO(savedRoom.getId(), savedRoom.getRoomType(), savedRoom.getRoomPrice());
        return ResponseEntity.ok(roomDTO);
    }

    @GetMapping("/room/types")
    public List<String> getRoomTypes(){
        return roomService.getAllRoomTypes();
    }

    @GetMapping("/all-rooms")
    public ResponseEntity<List<RoomDTO>> getAllRooms() throws SQLException {
        List<Room> rooms = roomService.getAllRooms(); // roomRepository.findAll()
        List<RoomDTO> roomDTOS = new ArrayList<>();
        for(Room room: rooms){
            byte[] photoBytes = roomService.getRoomPhotoByRoomId(room.getId());
            if(photoBytes != null && photoBytes.length > 0){
                String base64Photo = Base64.encodeBase64String(photoBytes);
                RoomDTO roomDTO = getRoomDTO(room);
                roomDTO.setPhoto(base64Photo);
                roomDTOS.add(roomDTO);
            }
        }
        return ResponseEntity.ok(roomDTOS);
    }

    private RoomDTO getRoomDTO(Room room) {
        List<BookedRoom> bookings = getAllBookingsByRoomId(room.getId());
//        List<BookingDTO> bookingInfo = bookings
//                .stream()
//                .map(booking -> new BookingDTO(booking.getBookingId(),
//                        booking.getCheckInDate(),
//                        booking.getCheckOutDate(),
//                        booking.getBookingConfirmationCode()))
//                .toList();
        byte[] photoBytes = null;
        Blob photoBlob = room.getPhoto();
        if(photoBlob != null){
            try {
                photoBytes = photoBlob.getBytes(1, (int) photoBlob.length());
            }catch (SQLException e){
                throw new PhotoRetrievealException("Error retrieving photo");
            }
        }
        return new RoomDTO(room.getId(),
                room.getRoomType(), room.getRoomPrice(),
                room.isBooked(), photoBytes);
    }

    private List<BookedRoom> getAllBookingsByRoomId(Long roomId) {
        return bookingService.getAllBookingsByRoomId(roomId);
    }

}
