package roomescape.reservation.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import roomescape.reservation.domain.Reservation;

import java.time.LocalDate;
import java.time.LocalTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ReservationRequest {
    private String name;
    private LocalDate date;
    private LocalTime time;

    public Reservation toReservation() {
        return new Reservation(null, name, date, time);
    }
}
