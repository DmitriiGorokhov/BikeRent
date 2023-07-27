package com.gorokhov.repositories;

import com.gorokhov.models.Comment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Set;

@Repository
public interface CommentsRepository extends JpaRepository<Comment, Long> {
//    Set<Comment> findAllByDescriptionContaining(String description);
}