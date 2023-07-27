package com.gorokhov.repositories;

import com.gorokhov.models.Client;
import com.gorokhov.models.Comment;
import com.gorokhov.util.exceptions.CommentNotFoundException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

@DataJpaTest
public class CommentsRepositoryTest {

    private final CommentsRepository commentsRepository;
    private final ClientsRepository clientsRepository;

    @Autowired
    public CommentsRepositoryTest(CommentsRepository commentsRepository, ClientsRepository clientsRepository) {
        this.commentsRepository = commentsRepository;
        this.clientsRepository = clientsRepository;
    }

    @Test
    public void givenNewComment_whenFindById_thenReturnCommentWithCorrectId() {
        String email = "tom@email.com";
        String name = "Tom";
        Client client = new Client(email, name);
        client = clientsRepository.save(client);

        String description = "I like Java";
        Comment comment = new Comment(client, description);
        long expectedId = commentsRepository.save(comment).getId();

        Comment found = commentsRepository.findById(expectedId)
                .orElseThrow(CommentNotFoundException::new);

        Assertions.assertEquals(expectedId, found.getId());
    }

//    @Test
//    public void givenNewComment_whenFindAllByDescriptionContaining_thenReturnCorrectComments() {
//        String email = "bob@email.com";
//        String name = "Bob";
//        Client client = new Client(email, name);
//        client = clientsRepository.save(client);
//
//        String description1 = "I like Spring";
//        Comment comment1 = new Comment(client, description1);
//        commentsRepository.save(comment1);
//
//        String description2 = "I hate vegetables";
//        Comment comment2 = new Comment(client, description2);
//        commentsRepository.save(comment2);
//
//        String description3 = "My cat likes sleep";
//        Comment comment3 = new Comment(client, description3);
//        commentsRepository.save(comment3);
//
//        Set<Comment> expected = new HashSet<>();
//        Collections.addAll(expected, comment1, comment3);
//
//        Set<Comment> found = commentsRepository.findAllByDescriptionContaining("like");
//
//        Assertions.assertTrue(found.containsAll(expected) && found.size() == expected.size());
//    }

    @Test
    public void givenNewComment_whenFindAllComments_thenReturnMoreThanZero() {
        String email = "jerry@email.com";
        String name = "Jerry";
        Client client = new Client(email, name);
        client = clientsRepository.save(client);

        String description = "Have enjoy!";
        Comment comment = new Comment(client, description);
        commentsRepository.save(comment);

        Set<Comment> found = new HashSet<>(commentsRepository.findAll());

        Assertions.assertTrue(found.size() > 0);
    }
}