package com.svalero.transportFleet.service;

import com.svalero.transportFleet.domain.Feedback;
import com.svalero.transportFleet.domain.Route;
import com.svalero.transportFleet.domain.User;
import com.svalero.transportFleet.dto.FeedbackInDto;
import com.svalero.transportFleet.dto.FeedbackModifyInDto;
import com.svalero.transportFleet.dto.FeedbackOutDto;
import com.svalero.transportFleet.exception.FeedbackNotFoundException;
import com.svalero.transportFleet.repository.FeedbackRepository;
import org.modelmapper.ModelMapper;
import org.modelmapper.TypeToken;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class FeedbackService {

    @Autowired
    private FeedbackRepository feedbackRepository;
    @Autowired
    private ModelMapper modelMapper;

    public Feedback add(FeedbackInDto feedbackInDto, Route route, User user) {
        Feedback feedback = new Feedback();
        feedback.setRating(feedbackInDto.getRating());
        feedback.setComment(feedbackInDto.getComment());
        feedback.setRegisterDate(feedbackInDto.getRegisterDate());
        feedback.setVisible(feedbackInDto.isVisible());
        feedback.setHelpfulCount(feedbackInDto.getHelpfulCount());
        feedback.setRecommend(feedbackInDto.isRecommend());
        feedback.setRoute(route);
        feedback.setUser(user);

        return feedbackRepository.save(feedback);
    }

    public void delete(long id) throws FeedbackNotFoundException {
        Feedback feedback = feedbackRepository.findById(id)
                .orElseThrow(FeedbackNotFoundException::new);
        feedbackRepository.delete(feedback);
    }

    public List<FeedbackOutDto> findAll(String username, String routeName, Float rating) {
        List<Feedback> allFeedbacks;

        if (username != null && !username.isEmpty()) {
            allFeedbacks = feedbackRepository.findByUserUsername(username);
        } else if (routeName != null && !routeName.isEmpty()) {
            allFeedbacks = feedbackRepository.findByRoute_Name(routeName);
        } else if (rating != null) {
            allFeedbacks = feedbackRepository.findByRatingGreaterThan(rating);
        } else {
            allFeedbacks = feedbackRepository.findAll();
        }

        return modelMapper.map(allFeedbacks, new TypeToken<List<FeedbackOutDto>>() {}.getType());
    }

    public Feedback getFeedbackById(long id) throws FeedbackNotFoundException {
        return feedbackRepository.findById(id)
                .orElseThrow(FeedbackNotFoundException::new);
    }

    public Feedback modify(long id, FeedbackModifyInDto feedbackInDto, Route route, User user) throws FeedbackNotFoundException {
        Feedback existingFeedback = feedbackRepository.findById(id)
                .orElseThrow(FeedbackNotFoundException::new);

        modelMapper.map(feedbackInDto, existingFeedback);
        existingFeedback.setId(id);
        existingFeedback.setUser(user);
        existingFeedback.setRoute(route);

        return feedbackRepository.save(existingFeedback);
    }
}
