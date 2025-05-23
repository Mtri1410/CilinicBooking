package com.example.bai1.Service;

import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

import org.springframework.stereotype.Service;

import org.apache.commons.codec.binary.Hex; // cho Hex.encodeHexString

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Value;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.*;

@Service
public class VnPayService {
    @Value("${vnpay.tmn-code}")
    private String vnpTmnCode;

    @Value("${vnpay.hash-secret}")
    private String vnpHashSecret;

    @Value("${vnpay.pay-url}")
    private String vnpPayUrl;

    @Value("${vnpay.return-url}")
    private String vnpReturnUrl;

    public String createPaymentUrl(HttpServletRequest req, long amount, String orderInfo, String id)
            throws UnsupportedEncodingException {
        return createPaymentUrl(req, amount, orderInfo, id, null, null, null);
    }

    public String createPaymentUrl(HttpServletRequest req, long amount, String orderInfo, String id,
            String pe, String note, Long userid)
            throws UnsupportedEncodingException {
        String vnp_Version = "2.1.0";
        String vnp_Command = "pay";
        String orderType = "other";
        String vnp_TxnRef = "DH" + System.currentTimeMillis();
        String vnp_IpAddr = req.getRemoteAddr();
        Map<String, String> vnp_Params = new HashMap<>();
        vnp_Params.put("vnp_Version", vnp_Version);
        vnp_Params.put("vnp_Command", vnp_Command);
        vnp_Params.put("vnp_TmnCode", vnpTmnCode);
        vnp_Params.put("vnp_Amount", String.valueOf(amount * 100));
        vnp_Params.put("vnp_CurrCode", "VND");
        vnp_Params.put("vnp_TxnRef", vnp_TxnRef);

        // Thêm logic để kiểm tra optionalValue
        String orderInfoValue = "action=" + orderInfo + "&amount=" + amount + "&id=" + id;
        if (pe != null && !pe.isEmpty()) {
            orderInfoValue += "&pe=" + pe;
        }

        if (note != null && !note.isEmpty()) {
            orderInfoValue += "&note=" + URLEncoder.encode(note, "UTF-8");
        }
        if (pe != null && !pe.isEmpty()) {
            orderInfoValue += "&userid=" + userid;
        }
        vnp_Params.put("vnp_OrderInfo", orderInfoValue);

        vnp_Params.put("vnp_OrderType", orderType);
        vnp_Params.put("vnp_Locale", "vn");
        vnp_Params.put("vnp_ReturnUrl", vnpReturnUrl);
        vnp_Params.put("vnp_IpAddr", vnp_IpAddr);
        vnp_Params.put("vnp_CreateDate", new SimpleDateFormat("yyyyMMddHHmmss").format(new Date()));

        List<String> fieldNames = new ArrayList<>(vnp_Params.keySet());
        Collections.sort(fieldNames);
        StringBuilder hashData = new StringBuilder();
        StringBuilder query = new StringBuilder();

        for (String field : fieldNames) {
            String value = vnp_Params.get(field);
            if (value != null && !value.isEmpty()) {
                hashData.append(field).append('=').append(URLEncoder.encode(value, "UTF-8"));
                query.append(field).append('=').append(URLEncoder.encode(value, "UTF-8"));
                if (!field.equals(fieldNames.get(fieldNames.size() - 1))) {
                    hashData.append('&');
                    query.append('&');
                }
            }
        }

        String secureHash = hmacSHA512(vnpHashSecret, hashData.toString());
        query.append("&vnp_SecureHash=").append(secureHash);
        return vnpPayUrl + "?" + query.toString();
    }

    private String hmacSHA512(String key, String data) {
        try {
            Mac hmac512 = Mac.getInstance("HmacSHA512");
            SecretKeySpec secretKey = new SecretKeySpec(key.getBytes(StandardCharsets.UTF_8), "HmacSHA512");
            hmac512.init(secretKey);
            byte[] bytes = hmac512.doFinal(data.getBytes(StandardCharsets.UTF_8));
            return Hex.encodeHexString(bytes);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
