package comfenalco.api.filter.sms.controller;

import comfenalco.api.filter.sms.dto.ProcessRequest;
import comfenalco.api.filter.sms.dto.ProcessResponse;
import comfenalco.api.filter.sms.service.SmsProcessingService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/sms")
@RequiredArgsConstructor
public class SmsProcessingController {
    private final SmsProcessingService smsProcessingService;

    @PostMapping(
            value = "/process"
    )
    public ResponseEntity<ProcessResponse> process(
            @RequestBody ProcessRequest request) {

        ProcessResponse response =
                smsProcessingService.process(request);

        return ResponseEntity.ok(response);
    }

    @PostMapping("/release")
    public ResponseEntity<ProcessResponse> release(
            @RequestBody ProcessRequest request) {

        return ResponseEntity.ok(
                smsProcessingService.release(request)
        );
    }
}
