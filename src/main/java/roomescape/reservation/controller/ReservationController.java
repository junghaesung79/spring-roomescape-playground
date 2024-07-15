package roomescape.reservation.controller;

import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import roomescape.reservation.domain.Reservation;
import roomescape.reservation.dto.ReservationRequest;
import roomescape.reservation.dto.ReservationResponse;

import java.net.URI;
import java.util.List;

@Controller
public class ReservationController {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @GetMapping("/reservation")
    public String reservation() { return "reservation"; }

    @PostMapping("/reservations")
    public ResponseEntity<ReservationResponse> addReservation(@Valid @RequestBody ReservationRequest request) {
        String sql = "INSERT INTO reservation(name, date, time) VALUES (?, ?, ?)";
        jdbcTemplate.update(sql, request.getName(), request.getDate(), request.getTime());

        Long newId = jdbcTemplate.queryForObject("SELECT count(*) from reservation", Long.class);
        Reservation newReservation = new Reservation(newId, request.getName(), request.getDate(), request.getTime());
        ReservationResponse response = new ReservationResponse(newReservation);

        URI location = URI.create(String.format("/reservations/%d", newId));
        return ResponseEntity.created(location).body(response);
    }

    @GetMapping("/reservations")
    public ResponseEntity<List<Reservation>> getReservations() {
        String sql = "SELECT id, name, date, time FROM reservation";
        List<Reservation> reservations = jdbcTemplate.query(sql, (resultSet, rowNum) -> (
            new Reservation(
                resultSet.getLong("id"),
                resultSet.getString("name"),
                resultSet.getDate("date").toLocalDate(),
                resultSet.getTime("time").toLocalTime()
            )
        ));
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
