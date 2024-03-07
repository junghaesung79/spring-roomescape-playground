package roomescape.domain.reservation.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/reservation")
public class ReservationWebController {

    @GetMapping
    public String reservationPage() {
        return "new-reservation";
    }
}