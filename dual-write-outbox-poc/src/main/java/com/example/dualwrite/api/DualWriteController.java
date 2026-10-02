package com.example.dualwrite.api;

import com.example.dualwrite.broker.BrokerFailureMode;
import com.example.dualwrite.service.NaiveRegistrationService;
import com.example.dualwrite.service.OutboxRegistrationService;
import com.example.dualwrite.service.OutboxRelayService;
import com.example.dualwrite.service.SystemStateService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

@RestController
@RequestMapping("/api")
public class DualWriteController {

    private final NaiveRegistrationService naiveService;
    private final OutboxRegistrationService outboxService;
    private final OutboxRelayService relayService;
    private final SystemStateService stateService;

    public DualWriteController(NaiveRegistrationService naiveService,
                               OutboxRegistrationService outboxService,
                               OutboxRelayService relayService,
                               SystemStateService stateService) {
        this.naiveService = naiveService;
        this.outboxService = outboxService;
        this.relayService = relayService;
        this.stateService = stateService;
    }

    @PostMapping("/registrations/naive/database-first")
    @ResponseStatus(HttpStatus.CREATED)
    public RegistrationResult naiveDatabaseFirst(@Valid @RequestBody RegistrationRequest request,
                                                  @RequestParam(defaultValue = "false") boolean fail) {
        return naiveService.databaseFirst(request, fail);
    }

    @PostMapping("/registrations/naive/message-first")
    @ResponseStatus(HttpStatus.CREATED)
    public RegistrationResult naiveMessageFirst(@Valid @RequestBody RegistrationRequest request,
                                                 @RequestParam(defaultValue = "false") boolean fail) {
        return naiveService.messageFirst(request, fail);
    }

    @PostMapping("/registrations/outbox")
    @ResponseStatus(HttpStatus.CREATED)
    public RegistrationResult outbox(@Valid @RequestBody RegistrationRequest request,
                                     @RequestParam(defaultValue = "false") boolean fail) {
        return outboxService.register(request, fail);
    }

    @PostMapping("/outbox/relay")
    public RelayResult relay(@RequestParam(defaultValue = "NONE") BrokerFailureMode failure) {
        return relayService.relay(failure);
    }

    @GetMapping("/state")
    public SystemStateResponse state() {
        return stateService.state();
    }

    @DeleteMapping("/state")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void reset() {
        stateService.reset();
    }
}
