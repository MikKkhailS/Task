package ru.relex.repository;

import org.springframework.data.jpa.repository.Query;
import ru.relex.model.Operation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface OperationRepository extends JpaRepository<Operation, Integer> {
    @Query(value = """
            SELECT count(*) 
            FROM Operations
            WHERE created_at
            BETWEEN CAST(?1 AS TIMESTAMP) AND CAST(?2 AS TIMESTAMP)
            """, nativeQuery = true)
    int operationsNumberByDate(String startDate, String endDate);
}