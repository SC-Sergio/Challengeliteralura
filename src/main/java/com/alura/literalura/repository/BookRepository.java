package com.alura.literalura.repository;

import com.alura.literalura.model.Book;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BookRepository extends JpaRepository<Book, Long> {

    boolean existsByGutendexId(Long gutendexId);

    List<Book> findAllByOrderByTitleAsc();

    @Query("select distinct b from Book b join b.languages l where lower(l) = lower(:language) order by b.title")
    List<Book> findByLanguageIgnoreCase(@Param("language") String language);
}
