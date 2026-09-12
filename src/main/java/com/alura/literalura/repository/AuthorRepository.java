package com.alura.literalura.repository;

import com.alura.literalura.model.Author;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface AuthorRepository extends JpaRepository<Author, Long> {

    Optional<Author> findByNameIgnoreCase(String name);

    List<Author> findAllByOrderByNameAsc();

    @Query("select a from Author a where a.birthYear is not null and a.birthYear <= :year " +
            "and (a.deathYear is null or a.deathYear >= :year) order by a.name")
    List<Author> findAliveInYear(@Param("year") int year);
}
