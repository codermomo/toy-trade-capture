package com.github.codermomo.ViewService.daos;

import com.github.codermomo.CommonLibrary.models.Trade;
import com.github.codermomo.ViewService.models.Position;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface TradeRepository extends CrudRepository<Trade, UUID> {
    // JPQL, which match Entity name and class members then convert them into a SQL
    @Query("SELECT new com.github.codermomo.ViewService.models.Position(t.bookId, t.instrumentId, SUM(t.quantity)) "
    + "FROM trade AS t WHERE t.bookId = ?1 GROUP BY t.bookId, t.instrumentId")
    List<Position> aggregatePositionsByBookId(String bookId);
}
