package Energeenot.TestTaskBackspark.controller;

import Energeenot.TestTaskBackspark.service.SockService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@RestController
@RequestMapping("/api/socks")
public class SockController {

    private final SockService sockService;

    @Autowired
    public SockController(SockService sockService) {
        this.sockService = sockService;
    }

    @Operation(summary = "Registering socks income")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Socks income registered successfully"),
            @ApiResponse(responseCode = "400", description = "Wrong parameters")
    })
    @PostMapping("/income")
    public ResponseEntity<String> income(@RequestParam String color,
                                         @RequestParam int cottonPart,
                                         @RequestParam int quantity) {
        sockService.registerSocksIncome(color, cottonPart, quantity);
        return ResponseEntity.ok("Sock income registered successfully");
    }

    @Operation(summary = "Registering socks outcome")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Socks outcome registered successfully"),
            @ApiResponse(responseCode = "404", description = "Socks with parameters not found"),
            @ApiResponse(responseCode = "409", description = "Invalid quantity")
    })
    @PostMapping("/outcome")
    public ResponseEntity<String> outcome(@RequestParam String color,
                                          @RequestParam int cottonPart,
                                          @RequestParam int quantity) {
        sockService.registerSocksOutcome(color, cottonPart, quantity);
        return ResponseEntity.ok("Sock outcome registered successfully");
    }

    @Operation(summary = "Searching socks count with filters")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Search for count of socks was successfully completed"),
            @ApiResponse(responseCode = "400", description = "Invalid request, incorrect parameters")
    })
    @GetMapping
    public ResponseEntity<Integer> getSocksCount(@RequestParam(required = false) String color,
                                                 @RequestParam(required = false) String comparison,
                                                 @RequestParam(required = false) Integer cottonPart,
                                                 @RequestParam(required = false) Integer maxCottonPart) {
        int count = sockService.getSocksCount(color, comparison, cottonPart, maxCottonPart);
        return ResponseEntity.ok(count);
    }

    @Operation(summary = "Editing socks")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Sock edited successfully"),
            @ApiResponse(responseCode = "404", description = "Sock with this id was not found")
    })
    @PutMapping("/{id}")
    public ResponseEntity<String> editSocks(@PathVariable int id,
                                            @RequestParam(required = false) String color,
                                            @RequestParam(required = false) Integer cottonPart,
                                            @RequestParam(required = false) Integer quantity){
        sockService.editSock(id, color, cottonPart, quantity);
        return ResponseEntity.ok("Sock edited successfully");
    }

    @Operation(summary = "Loading a batch of socks")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "The batch of socks has been uploaded successfully"),
            @ApiResponse(responseCode = "400", description = "Error in the file format")
    })
    @PutMapping("/batch")
    public ResponseEntity<String> uploadSocksBatch(@RequestParam("file") MultipartFile file) throws IOException {
        sockService.saveSocksBatch(file);
        return ResponseEntity.ok("Socks upload successfully");

    }
}
