package com.gorokhov.controllers;

import com.gorokhov.models.Client;
import com.gorokhov.models.Comment;
import com.gorokhov.services.CommentsService;
import com.gorokhov.util.JsonUtil;
import com.gorokhov.util.exceptions.CommentNotCreatedException;
import com.gorokhov.util.exceptions.CommentNotFoundException;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.*;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.Matchers.hasSize;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.verify;
import static org.mockito.internal.verification.VerificationModeFactory.times;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(CommentsController.class)
public class CommentsControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private CommentsService commentsService;

    @Test
    public void givenComment_whenPostComment_thenReturnStatusCreated() {
        long clientId = 1L;
        String email = "tom@email.com";
        String name = "Tom";
        Client client = new Client(email, name);
        client.setId(clientId);

        long commentId = 1L;
        String description = "I am Tom";
        Comment comment = new Comment(client, description);
        comment.setId(commentId);

        given(commentsService.save(Mockito.any())).willReturn(comment);

        try {
            mockMvc.perform(post("/comments")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(JsonUtil.toJson(comment)))
                    .andExpect(status().isCreated());
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        verify(commentsService, times(1)).save(Mockito.any());
        reset(commentsService);
    }

    @Test
    public void givenComment_whenPostComment_thenThrowCommentNotCreatedException() {
        long id = 2L;
        String email = "django@email.com";
        String name = "Django";
        Client client = new Client(email, name);
        client.setId(id);

        String description = null;
        Comment comment = new Comment(client, description);

        String errorMessage = "description - Необходимо указать описание; ";

        try {
            mockMvc.perform(post("/comments")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(JsonUtil.toJson(comment)))
                    .andExpect(result ->
                            assertTrue(result.getResolvedException() instanceof CommentNotCreatedException))
                    .andExpect(result ->
                            assertEquals(errorMessage,
                                    Objects.requireNonNull(result.getResolvedException()).getMessage()))
                    .andExpect(status().isBadRequest());
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        reset(commentsService);
    }

    @Test
    public void givenComment_whenGetComment_thenReturnJson() {
        long clientId = 3L;
        String email = "mike@email.com";
        String name = "Mike";
        Client client = new Client(email, name);
        client.setId(clientId);

        long commentId = 3L;
        String description = "I am Mike";
        Comment comment = new Comment(client, description);
        comment.setId(commentId);

        given(commentsService.findOne(commentId)).willReturn(Optional.of(comment));

        try {
            mockMvc.perform(get("/comments/3")
                    .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(jsonPath("$.id", is(comment.getId()), long.class))
                    .andExpect(jsonPath("$.description", is(comment.getDescription()), String.class))
                    .andExpect(jsonPath("$.client.id", is(client.getId()), long.class))
                    .andExpect(jsonPath("$.client.email", is(client.getEmail()), String.class))
                    .andExpect(jsonPath("$.client.name", is(client.getName()), String.class));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        verify(commentsService, times(1)).findOne(commentId);
        reset(commentsService);
    }

    @Test
    public void givenComments_whenGetComments_thenReturnJsonArray() {
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

        Set<Comment> comments = new LinkedHashSet<>();
        Collections.addAll(comments, comment1, comment2);

        given(commentsService.findAll()).willReturn(comments);

        try {
            mockMvc.perform(get("/comments")
                    .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(jsonPath("$", hasSize(2)))
                    .andExpect(jsonPath("$[0].id", is(comment1.getId()), long.class))
                    .andExpect(jsonPath("$[0].description", is(comment1.getDescription()), String.class))
                    .andExpect(jsonPath("$[0].client.id", is(client.getId()), long.class))
                    .andExpect(jsonPath("$[0].client.email", is(client.getEmail()), String.class))
                    .andExpect(jsonPath("$[0].client.name", is(client.getName()), String.class))
                    .andExpect(jsonPath("$[1].id", is(comment2.getId()), long.class))
                    .andExpect(jsonPath("$[1].description", is(comment2.getDescription()), String.class))
                    .andExpect(jsonPath("$[1].client.id", is(client.getId()), long.class))
                    .andExpect(jsonPath("$[1].client.email", is(client.getEmail()), String.class))
                    .andExpect(jsonPath("$[1].client.name", is(client.getName()), String.class));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        verify(commentsService, times(1)).findAll();
        reset(commentsService);
    }

    @Test
    public void givenComment_whenGetNonExistentComment_thenThrowCommentNotFoundException() {
        long id = 6L;
        String errorMessage = "Комментарий не был найден";

        given(commentsService.findOne(id)).willThrow(new CommentNotFoundException());

        try {
            mockMvc.perform(get("/comments/6")
                    .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(result ->
                            assertTrue(result.getResolvedException() instanceof CommentNotFoundException))
                    .andExpect(result ->
                            assertEquals(errorMessage,
                                    Objects.requireNonNull(result.getResolvedException()).getMessage()))
                    .andExpect(status().isBadRequest());
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        reset(commentsService);
    }
}