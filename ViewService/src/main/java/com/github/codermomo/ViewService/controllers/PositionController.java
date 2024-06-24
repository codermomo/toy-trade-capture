package com.github.codermomo.ViewService.controllers;

import com.github.codermomo.ViewService.models.Position;
import com.github.codermomo.ViewService.services.PositionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/position")
public class PositionController {

    @Autowired
    private PositionService positionService;

    @GetMapping(value = "/getPositions", params = "bookIds")
    ResponseEntity<List<Position>> getPositionsByBookIds(@RequestParam("bookIds") List<String> bookIds) {
        return new ResponseEntity<>(positionService.aggregatePositionsByBookIds(bookIds), HttpStatus.OK);
    }
}
