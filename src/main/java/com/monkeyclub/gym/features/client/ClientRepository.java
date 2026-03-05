package com.monkeyclub.gym.features.client;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ClientRepository extends JpaRepository<Client, UUID> {

    Optional<Client> findByDocument(String document);

    boolean existsByDocument(String document);

    boolean existsByDocumentAndIdNot(String document, UUID id);

    @Query("""
            SELECT c FROM Client c
            WHERE lower(concat(c.firstName, ' ', c.lastName)) LIKE lower(concat('%', :q, '%'))
               OR lower(c.document) LIKE lower(concat('%', :q, '%'))
               OR lower(c.phone) LIKE lower(concat('%', :q, '%'))
            ORDER BY c.firstName, c.lastName
            """)
    List<Client> search(String q);
}
