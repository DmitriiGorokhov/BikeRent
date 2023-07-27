package com.gorokhov.services;

import com.gorokhov.models.Client;
import com.gorokhov.models.Comment;
import com.gorokhov.repositories.ClientsRepository;
import com.gorokhov.repositories.CommentsRepository;
import com.gorokhov.util.exceptions.ClientNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

@Service
public class CommentsService {

    private final CommentsRepository commentsRepository;
    private final ClientsRepository clientsRepository;

    @Autowired
    public CommentsService(CommentsRepository commentsRepository, ClientsRepository clientsRepository) {
        this.commentsRepository = commentsRepository;
        this.clientsRepository = clientsRepository;
    }

    @Transactional(readOnly = true)
    public Optional<Comment> findOne(long id) {
        return commentsRepository.findById(id);
    }

    @Transactional(readOnly = true)
    public Set<Comment> findAll() {
        return new HashSet<>(commentsRepository.findAll());
    }

//    @Transactional(readOnly = true)
//    public Set<Comment> findAllByDescriptionContaining(String description) {
//        return commentsRepository.findAllByDescriptionContaining(description);
//    }

    @Transactional
    public Comment save(Comment comment) {
        Client client = clientsRepository.findById(comment.getClient().getId())
                                        .orElseThrow(ClientNotFoundException::new);
        comment = commentsRepository.save(comment);
        client.getComments().add(comment);
        return comment;
    }
}