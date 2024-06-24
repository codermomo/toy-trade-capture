package com.github.codermomo.ViewService.services;

import com.github.codermomo.ViewService.daos.TradeRepository;
import com.github.codermomo.ViewService.models.Position;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class PositionService {

    @Autowired
    private TradeRepository tradeRepository;

    public List<Position> aggregatePositionsByBookIds(List<String> bookIds) {
        return bookIds.stream()
                .flatMap(bookId -> tradeRepository.aggregatePositionsByBookId(bookId).stream())
                .collect(Collectors.toList());
    }
}
