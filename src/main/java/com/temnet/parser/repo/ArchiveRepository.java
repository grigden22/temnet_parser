package com.temnet.parser.repo;

import com.temnet.parser.domain.Archive;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.sql.Timestamp;

@Repository
public interface ArchiveRepository extends JpaRepository<Archive, Long> {

    Page<Archive> findByUsernameContainsAndCreatedAtGreaterThanEqualAndCreatedAtLessThanEqualAndTxt(String username, Timestamp createdAt, Timestamp createdAt2, String txt, Pageable pageable);
    Page<Archive> findAllByCreatedAtGreaterThanEqualAndCreatedAtLessThanEqualAndTxt(Timestamp createdAt, Timestamp createdAt2, String txt, Pageable pageable);

}
