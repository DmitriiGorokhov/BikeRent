package com.gorokhov.services;

import com.gorokhov.models.Client;
import com.gorokhov.models.Comment;
import com.gorokhov.repositories.ClientsRepository;
import com.gorokhov.repositories.CommentsRepository;
import com.gorokhov.util.exceptions.ClientNotFoundException;
import com.gorokhov.util.exceptions.ClientNotUpdatedException;
import com.gorokhov.util.exceptions.CommentNotFoundException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class CommentsServiceTest {

    @Mock
    private CommentsRepository commentsRepository;

    @Mock
    private ClientsRepository clientsRepository;

    @InjectMocks
    private CommentsService commentsService;

    @Test
    public void givenComment_whenSaveComment_thenReturnComment() {
        long clientId = 1L;
        String email = "tom@email.com";
        String name = "Tom";
        Client client = new Client(email, name);
        client.setId(clientId);

        long commentId = 1L;
        String description = "I am Tom";
        Comment comment = new Comment(client, description);
        comment.setId(commentId);


        given(clientsRepository.findById(clientId)).willReturn(Optional.of(client));
        given(commentsRepository.save(comment)).willReturn(comment);

        Set<Comment> comments = new HashSet<>();
        Collections.addAll(comments, comment);
        client.setComments(comments);

        Comment saved = commentsService.save(comment);

        assertNotNull(saved);
        assertEquals(comment, saved);
        verify(clientsRepository, times(1)).findById(clientId);
        verify(commentsRepository, times(1)).save(comment);
        reset(commentsRepository);
    }

    @Test
    public void givenComment_whenSaveComment_thenThrowClientNotFoundException() {
        long clientId = 2L;
        String email = "django@email.com";
        String name = "Django";
        Client client = new Client(email, name);
        client.setId(clientId);

        long commentId = 2L;
        String description = "I am Django";
        Comment comment = new Comment(client, description);
        comment.setId(commentId);

        given(clientsRepository.findById(clientId)).willThrow(ClientNotFoundException.class);

        assertThrows(ClientNotFoundException.class, () -> commentsService.save(comment));
        verify(clientsRepository, times(1)).findById(clientId);
        reset(clientsRepository);
    }

    @Test
    public void givenComment_whenGetById_thenReturnComment() {
        long clientId = 3L;
        String email = "mike@email.com";
        String name = "Mike";
        Client client = new Client(email, name);
        client.setId(clientId);

        long commentId = 3L;
        String description = "I am Mike";
        Comment comment = new Comment(client, description);
        comment.setId(commentId);

        given(commentsRepository.findById(commentId)).willReturn(Optional.of(comment));

        Comment found = commentsService.findOne(commentId).orElseThrow(CommentNotFoundException::new);

        assertNotNull(found);
        assertEquals(comment, found);
        verify(commentsRepository, times(1)).findById(commentId);
        reset(commentsRepository);
    }

    @Test
    public void givenComments_whenGetAllComments_thenReturnCommentsList() {
        long clientId = 4L;
        String email = "jerry@email.com";
        String name = "Jerry";
        Client client = new Client(email, name);
        client.setId(clientId);

        long commentId1 = 4L;
        String description1 = "I am Jerry";
        Comment comment1 = new Comment(client, description1);
        comment1.setId(commentId1);

        long commentId2 = 5L;
        String description2 = "I like Hibernate!";
        Comment comment2 = new Comment(client, description2);
        comment2.setId(commentId2);

        Set<Comment> comments = new HashSet<>();
        Collections.addAll(comments, comment1, comment2);

        given(commentsRepository.findAll()).willReturn(List.of(comment1, comment2));

        Set<Comment> found = commentsService.findAll();

        assertNotNull(found);
        assertEquals(2, found.size());
        verify(commentsRepository, times(1)).findAll();
        reset(commentsRepository);
    }
}