package com.dev.nurlyhotel.controller;

import com.dev.nurlyhotel.dto.RoomDTO;
import com.dev.nurlyhotel.model.Room;
import com.dev.nurlyhotel.service.IRoomService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.math.BigDecimal;
import java.sql.SQLException;
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

}
