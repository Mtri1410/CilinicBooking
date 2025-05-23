package com.example.bai1.Controller;

import java.io.UnsupportedEncodingException;
import java.util.HashMap;

import jakarta.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import java.util.Map;
import com.example.bai1.Service.VnPayService;
import com.example.bai1.Model.Orders;
import com.example.bai1.Service.Doctor.OrderService;
import com.example.bai1.Model.Appointments;
import com.example.bai1.Service.Doctor.AppointmentsService;

@Controller
@RequestMapping("/api/vnpay")
public class VnPayController {
    @Autowired
    private VnPayService vnPayService;
    @Autowired
    private OrderService orderService;
    @Autowired
    private AppointmentsService appointmentsService;

    @GetMapping("/pay")
    public ResponseEntity<?> createPayment(HttpServletRequest request) throws UnsupportedEncodingException {
        String paymentUrl = vnPayService.createPaymentUrl(request, 50000, "Thanh toán đơn hàng test", "");
        Map<String, String> response = new HashMap<>();
        response.put("url", paymentUrl);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/return")
    public String paymentReturn(HttpServletRequest request) {
        String responseCode = request.getParameter("vnp_ResponseCode");
        String orderInfo = request.getParameter("vnp_OrderInfo");
        Map<String, String> parsedInfo = parseQueryString(orderInfo);
        String action = parsedInfo.get("action");
        String amountStr = parsedInfo.get("amount");
        String orderId = parsedInfo.get("id");
        String period = parsedInfo.get("pe");
        String note = parsedInfo.get("note");
        String userid = parsedInfo.get("userid");
        Long amount = amountStr != null ? Long.parseLong(amountStr) : null;
        if ("00".equals(responseCode)) {
            if ("order".equals(action) && orderId != null) {
                try {
                    Orders order = orderService.getorderbyorderid(Long.parseLong(orderId));
                    if (order != null) {
                        order.setStatus("PAID");
                        orderService.save(order);
                    }
                } catch (Exception e) { /* ignore */ }
            }
            if ("pickschedule".equals(action) && orderId != null) {
                try {
                    Appointments appt = appointmentsService.getAppointmentById(Long.parseLong(orderId));
                    if (appt != null) {
                        appt.setStatus(Appointments.Status.PAID);
                        appointmentsService.save(appt);
                    }
                } catch (Exception e) { /* ignore */ }
            }
            String redirectUrl = "/"; // mặc định

            switch (action) {
                case "giahanMembership":
                    redirectUrl = "/doctor/upmembership?note=giahan";
                    break;
                case "nangcapMembership":
                    redirectUrl = "/doctor/upmembership?note=nangcap";
                    break;
                case "pickschedule":
                    redirectUrl = "/doctor/appointment?note=pick";
                    break;
                default:
                    redirectUrl = "/thankyou";
            }
            if (redirectUrl.contains("?")) {
                redirectUrl += "&orderId=" + orderId + "&amount=" + amount + "&period=" + period + "&schedulenote="
                        + note
                        + "&userid=" + userid;
            } else {
                redirectUrl += "?orderId=" + orderId + "&amount=" + amount + "&period=" + period + "&schedulenote="
                        + note + "&userid=" + userid;
            }
            return "redirect:" + redirectUrl;
        } else {
            return "redirect:/payment-fail";
        }
    }

    private Map<String, String> parseQueryString(String query) {
        Map<String, String> map = new HashMap<>();
        if (query == null || query.isBlank())
            return map;

        String[] pairs = query.split("&");
        for (String pair : pairs) {
            String[] keyValue = pair.split("=");
            if (keyValue.length == 2) {
                map.put(keyValue[0].trim(), keyValue[1].trim());
            }
        }
        return map;
    }

}
