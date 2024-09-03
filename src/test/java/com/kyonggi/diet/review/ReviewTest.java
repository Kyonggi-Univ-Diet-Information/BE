package com.kyonggi.diet.review;
import com.kyonggi.diet.dietContent.DietContent;
import com.kyonggi.diet.member.MemberEntity;
import com.kyonggi.diet.restaurant.Restaurant;

import com.kyonggi.diet.review.DTO.ReviewDTO;
import com.kyonggi.diet.review.service.ReviewService;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.util.NoSuchElementException;

import static org.assertj.core.api.Assertions.*;

@SpringBootTest
@Transactional
//@Rollback(value = false)
public class ReviewTest {

    @Autowired ReviewRepository reviewRepository;
    @Autowired
    ReviewService reviewService;
    @Autowired EntityManager em;


    @Test
    public void 리뷰_등록() {
        //멤버 객체 생성 및 db저장
        MemberEntity member = new MemberEntity();
        member.setName("1");
        em.persist(member);

        //Diet 객체 생성 및 db저장
        DietContent dietContent = DietContent.builder()
                .date("1")
                .build();

        em.persist(dietContent);

        Restaurant restaurant =
                Restaurant
                        .builder()
                        .name("긱식")
                        .build();
        em.persist(restaurant);

        //강제 db 삽입
        em.flush();

        //영속성 해제
        em.clear();

        //리뷰 등록
        Review review = Review.builder().member(member).title("title").content("content").restaurant(restaurant).build();
        Long reviewId = reviewService.saveReview(review);
        System.out.println("reviewRating = " + review.getRating() + " id = " + review.getId());
        System.out.println("reviewId = " + reviewId);
        //검증
        assertThat(review.getId()).isEqualTo(reviewId);

        //db에 저장된 review 꺼내서 같은지 검증
        Long matchingReviewId = em.createQuery("select r.id from Review r where r.member = :member", Long.class)
                .setParameter("member", review.getMember())
                .getSingleResult();
        //검증
        assertThat(review.getId()).isEqualTo(matchingReviewId);
    }

    @Test
    public void 리뷰_수정() {
        //멤버 객체 생성 및 db저장
        MemberEntity member = new MemberEntity();
        member.setName("1");
        em.persist(member);

        //Diet 객체 생성 및 db저장
        DietContent dietContent = DietContent.builder()
                .date("1").build();
        em.persist(dietContent);

        Restaurant restaurant = Restaurant
                                .builder()
                                .name("긱식")
                                .build();
        em.persist(restaurant);

        //강제 db 삽입
        em.flush();

        //영속성 해제
        em.clear();

        //Review 객체 생성 및 db 삽입
        Review review = Review.builder().member(member).rating(4.9).title("title").content("content").restaurant(restaurant).build();
        Long reviewId = reviewService.saveReview(review);
        System.out.println("reviewRating = " + review.getRating() + " reviewTitle = "
                + review.getTitle() + " reviewContent = " + review.getContent());

        //db에 저장된 Review 추출
        Long matchingReviewId = em.createQuery("select r.id from Review r where r.member = :member", Long.class)
                .setParameter("member", review.getMember())
                .getSingleResult();

        //리뷰 수정
        ReviewDTO reviewDTO = ReviewDTO.builder().rating(4.3).title("After title").content("After content").build();
        reviewService.modifyReview(matchingReviewId, reviewDTO);


    }

    @Test
    public void 리뷰_삭제() {
        //멤버 객체 생성 및 db저장
        MemberEntity member = new MemberEntity();
        member.setName("1");
        em.persist(member);

        //Diet 객체 생성 및 db저장
        DietContent dietContent = DietContent.builder()
                .date("1").build();
        em.persist(dietContent);

        Restaurant restaurant = Restaurant
                                .builder()
                                .name("긱식")
                                .build();
        em.persist(restaurant);

        //강제 db 삽입
        em.flush();

        //영속성 해제
        em.clear();

        //리뷰 객체 생성
        Review review = Review.builder().member(member).rating(4.9).title("title").content("content").restaurant(restaurant).build();
        Long reviewId = reviewService.saveReview(review);

        //db에 저장된 Review 추출
        Long matchingReviewId = em.createQuery("select r.id from Review r where r.member = :member", Long.class)
                .setParameter("member", review.getMember())
                .getSingleResult();
        reviewService.deleteReview(matchingReviewId);
        try {
            try {
                System.out.println("rating = " + reviewService.findReview(matchingReviewId).getRating());
            } catch (NoSuchElementException e) {
                System.out.println("아이쿠...");
            }
        } catch (NullPointerException e) {
            System.out.println("널이지롱");
        }
        assertThat(em.find(Review.class,matchingReviewId)).isNull();

    }
}
