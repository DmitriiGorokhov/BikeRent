package com.gorokhov.controllers;

import com.gorokhov.models.Comment;
import com.gorokhov.services.CommentsService;
import com.gorokhov.util.ErrorResponse;
import com.gorokhov.util.exceptions.*;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Set;

@RestController
@RequestMapping("/comments")
public class CommentsController {

    private final CommentsService commentsService;

    @Autowired
    public CommentsController(CommentsService commentsService) {
        this.commentsService = commentsService;
    }

    @PostMapping()
    public ResponseEntity<HttpStatus> create(@RequestBody @Valid Comment comment, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            StringBuilder errorMessage = new StringBuilder();
            List<FieldError> errors = bindingResult.getFieldErrors();
            errors.forEach(e -> errorMessage.append(e.getField())
                                            .append(" - ")
                                            .append(e.getDefaultMessage())
                                            .append("; "));
            throw new CommentNotCreatedException(errorMessage.toString());
        }
        commentsService.save(comment);
        return ResponseEntity.status(HttpStatus.CREATED).body(HttpStatus.CREATED);
    }

    @GetMapping()
    public Set<Comment> getAll() {
        return commentsService.findAll();
    }

//    @GetMapping("/search")
//    public Set<Comment> getAllByDescriptionContaining(@RequestParam String description) {
//        return commentsService.findAllByDescriptionContaining(description);
//    }

    @GetMapping("/{id}")
    public Comment get(@PathVariable("id") long id) {
        return commentsService.findOne(id).orElseThrow(CommentNotFoundException::new);
    }

    @ExceptionHandler
    private ResponseEntity<ErrorResponse> handleException(CommentNotFoundException e) {
        ErrorResponse response = new ErrorResponse(e.getMessage());
        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler
    private ResponseEntity<ErrorResponse> handleException(CommentNotCreatedException e) {
        ErrorResponse response = new ErrorResponse(e.getMessage());
        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }
}