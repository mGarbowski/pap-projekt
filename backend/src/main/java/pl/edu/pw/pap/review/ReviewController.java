package pl.edu.pw.pap.review;


import lombok.RequiredArgsConstructor;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.Link;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import pl.edu.pw.pap.comment.CommentController;
import pl.edu.pw.pap.comment.commentNotFoundException;
import pl.edu.pw.pap.security.UserPrincipal;
import pl.edu.pw.pap.user.User;
import pl.edu.pw.pap.user.UserController;
import pl.edu.pw.pap.user.UserRepository;
import pl.edu.pw.pap.user.userNotFoundException;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@RestController
@RequiredArgsConstructor
public class ReviewController {

    private final ReviewService reviewService;
    private final UserRepository userRepository;



    @GetMapping("/api/courses/{courseId}/users/{username}/reviews")
    public EntityModel<Review> getReview(@PathVariable Long courseId, @PathVariable String username) {
        var maybeUser = userRepository.findByUsername(username);
        if (maybeUser.isEmpty()) {
            throw new userNotFoundException("No user with username " + username);
        }
        User user = maybeUser.get();
        Optional<Review> maybeReview = reviewService.getReviewById(user.getId(), courseId);
        if (maybeReview.isEmpty()) {
            throw new reviewNotFoundException("No review of course " + courseId + " by " + username);
        }
        Review review = maybeReview.get();
        Link selfLink = linkTo(methodOn(ReviewController.class).getReview(courseId, username)).withSelfRel();
        Link userLink = linkTo(methodOn(UserController.class).getUser(username)).withRel("user");
        Link commentsLink = linkTo(methodOn(CommentController.class).getCommentsForReview(courseId, user.getId())).withRel("comments");
        // TODO: Add link from CourseController
//        Link courseLink = linkTo(method)
        Link[] links = {selfLink, userLink, commentsLink};
        return EntityModel.of(review, links);
    }


    @GetMapping("/api/courses/{courseId}/reviews")
    public CollectionModel<EntityModel<Review>> getCourseReviews(@PathVariable Long courseId) {
        var reviews = reviewService.getCourseReviews(courseId);
        List<EntityModel<Review>> reviewModelList = new ArrayList<>();
        for (Review review: reviews) {
            reviewModelList.add(getReview(review.getId().courseId, review.getUser().getUsername()));
        }
        // TODO: Add course link to collection model of reviews of a course
//        Link courseLink = linkTo(methodOn())

        return CollectionModel.of(
                reviewModelList
        );
    }


    @GetMapping("/api/users/{username}/reviews")
    public CollectionModel<EntityModel<Review>> getUserReviews(@PathVariable String username){

        var maybeUser = userRepository.findByUsername(username);
        if (maybeUser.isEmpty()){
            throw new userNotFoundException("No user with username " + username);

        }
        User user = maybeUser.get();
        List<EntityModel<Review>> reviewModelList = new ArrayList<>();
        for (Review review: user.getReviews()) {
            reviewModelList.add(getReview(review.getCourse().getId(), review.getUser().getUsername()));
        }
        Link selfLink = linkTo(methodOn(ReviewController.class).getUserReviews(username)).withSelfRel();
        Link userLink = linkTo(methodOn(UserController.class).getUser(username)).withRel("user");


        Link[] linkList = {selfLink, userLink};
        return CollectionModel.of(
                reviewModelList,
                linkList
        );

    }


    @DeleteMapping("/api/courses/{courseId}/users/{username}/reviews")
    public ResponseEntity<Review> deleteReview(Long courseId, String username) {
        reviewService.deleteReview(courseId, username);
        return ResponseEntity.noContent().build();
    }


    @ExceptionHandler({commentNotFoundException.class, userNotFoundException.class, reviewNotFoundException.class})
    public ResponseEntity<Exception> handleEntityNotFound(Exception e) { return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e);}
}


