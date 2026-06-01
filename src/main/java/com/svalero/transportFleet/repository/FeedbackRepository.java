package com.svalero.transportFleet.repository;

import com.svalero.transportFleet.domain.Feedback;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface FeedbackRepository extends CrudRepository<Feedback, Long> {
    List<Feedback> findAll();
    List<Feedback> findByRoute_Id(long routeId);
    List<Feedback> findByUser_Id(long userId);
    List<Feedback> findByUserUsername(String username);
    List<Feedback> findByRoute_Name(String routeName);
    List<Feedback> findByRatingGreaterThan(float rating);
}
