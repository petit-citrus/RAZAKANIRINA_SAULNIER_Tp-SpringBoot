
package com.campus.achat;

import java.net.URI;

import com.campus.achat.dto.AchatRequest;
import com.campus.achat.dto.AchatResponse;
import com.campus.achat.service.AchatService;

import jakarta.validation.Valid;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/achats")
public class AchatController {

    private final AchatService achatService;

    public AchatController(AchatService achatService) {
        this.achatService = achatService;
    }

    @PostMapping
    public ResponseEntity<AchatResponse> creer(
            @Valid @RequestBody AchatRequest request,
            Authentication authentication) {

        AchatResponse response = achatService.creer(
                request, authentication);

        return ResponseEntity
                .created(URI.create("/api/achats/" + response.code()))
                .body(response);
    }
}
