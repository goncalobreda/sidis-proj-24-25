package com.example.readerserviceCommand.api;

import com.example.readerserviceCommand.model.Reader;
import com.example.readerserviceCommand.service.CreateReaderRequest;
import com.example.readerserviceCommand.service.EditReaderRequest;
import com.example.readerserviceCommand.service.ReaderServiceImpl;
import com.example.readerserviceCommand.messaging.RabbitMQProducer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/readers")
class ReaderController {

    private final ReaderServiceImpl readerService;
    private final RabbitMQProducer rabbitMQProducer;

    @Autowired
    public ReaderController(ReaderServiceImpl readerService, RabbitMQProducer rabbitMQProducer) {
        this.readerService = readerService;
        this.rabbitMQProducer = rabbitMQProducer;
    }

    @PostMapping("/internal/register")
    public ResponseEntity<Void> registerReader(@RequestBody Reader reader) {
        try {
            readerService.syncReceivedReader(reader);
            rabbitMQProducer.sendSyncMessage(reader);
            return ResponseEntity.status(HttpStatus.CREATED).build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
        }
    }

    @PatchMapping("/readers/{id1}/{id2}/update")
    public ResponseEntity<Reader> updateReader(@PathVariable("id1") String id1, @PathVariable("id2") String id2,
                                               @RequestBody EditReaderRequest editRequest) {
        String readerID = id1 + "/" + id2;
        Reader updatedReader = readerService.partialUpdate(readerID, editRequest, 0); // Replace version handling
        return ResponseEntity.ok(updatedReader);
    }
}
