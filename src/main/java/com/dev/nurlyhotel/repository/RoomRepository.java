package com.dev.nurlyhotel.repository;

import com.dev.nurlyhotel.model.Room;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RoomRepository extends JpaRepository<Room, Long> {
}
