package roomescape.domain.reservation.controller;

import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import roomescape.domain.reservation.Reservation;
import roomescape.domain.reservation.dto.ReservationRequest;
import roomescape.domain.reservation.dto.ReservationResponse;
import roomescape.domain.time.ReservationTime;

import java.net.URI;
import java.sql.Time;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

@Controller
public class ReservationController {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @GetMapping("/reservation")
    public String reservation() { return "new-reservation"; }

    @PostMapping("/reservations")
    public ResponseEntity<ReservationResponse> addReservation(@Valid @RequestBody ReservationRequest request) {
        String sql = "INSERT INTO reservation(name, date, time_id) VALUES (?, ?, ?)";
        jdbcTemplate.update(sql, request.getName(), request.getDate(), request.getTimeId());

        Long newId = jdbcTemplate.queryForObject("SELECT count(*) from reservation", Long.class);

        String timeSql = "SELECT time FROM time WHERE id = ?";
        java.sql.Time time = jdbcTemplate.queryForObject(timeSql, java.sql.Time.class, request.getTimeId());

        Reservation newReservation = new Reservation(newId, request.getName(), request.getDate(), request.getTimeId(), time);
        ReservationResponse response = new ReservationResponse(newReservation);

        URI location = URI.create(String.format("/reservations/%d", newId));
        return ResponseEntity.created(location).body(response);
    }

    @GetMapping("/reservations")
    public ResponseEntity<List<Reservation>> getReservations() {
        String sql = "SELECT" +
            "r.id as reservation_id," +
            "r.name," +
            "r.date," +
            "t.id as time_id," +
            "t.time as time_value " +
            "FROM reservation as r INNER JOIN time as t ON r.time_id = t.id";

        List<Reservation> reservations = jdbcTemplate.query(sql, (resultSet, rowNum) -> {
            Long reservationId = resultSet.getLong("reservation_id");
            String name = resultSet.getString("name");
            LocalDate date = resultSet.getDate("date").toLocalDate();
            Long timeId = resultSet.getLong("time_id");
            Time timeValue = resultSet.getTime("time_value");

            return new Reservation(reservationId, name, date, timeId, timeValue);
        });

        return ResponseEntity.ok(reservations);
    }

    @DeleteMapping("/reservations/{id}")
    public ResponseEntity<Void> deleteReservation(@PathVariable Long id) {
        String sql = "DELETE FROM reservation WHERE id = ?";
        int rowsAffected = jdbcTemplate.update(sql, id);
        if (rowsAffected == 0) {
            return ResponseEntity.badRequest().build();
        }

        return ResponseEntity.noContent().build();
    }

}
