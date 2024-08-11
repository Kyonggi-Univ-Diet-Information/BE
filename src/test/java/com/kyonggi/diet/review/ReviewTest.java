package com.kyonggi.diet.review;

import com.kyonggi.diet.diet.Diet;
import com.kyonggi.diet.member.Member;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import static org.assertj.core.api.Assertions.*;

@SpringBootTest
@Transactional
//@Rollback(value = false)
public class ReviewTest {

    @Autowired ReviewRepository reviewRepository;
    @Autowired ReviewService reviewService;
    @Autowired EntityManager em;

    @Test
    public void 리뷰_등록() {
        //멤버 객체 생성 및 db저장
        Member member = new Member();
        member.setName("1");
        em.persist(member);

        //Diet 객체 생성 및 db저장
        Diet diet = new Diet();
        diet.setDate("1");
        em.persist(diet);

        //강제 db 삽입
        em.flush();

        //영속성 해제
        em.clear();

        //리뷰 등록
        Review review = Review.createReview(member, 4.9, "title", "content");
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
        Member member = new Member();
        member.setName("1");
        em.persist(member);

        //Diet 객체 생성 및 db저장
        Diet diet = new Diet();
        diet.setDate("1");
        em.persist(diet);

        //강제 db 삽입
        em.flush();

        //영속성 해제
        em.clear();

        //Review 객체 생성 및 db 삽입
        Review review = Review.createReview(member, 4.9, "title", "content");
        Long reviewId = reviewService.saveReview(review);
        System.out.println("reviewRating = " + review.getRating() + " reviewTitle = "
                + review.getTitle() + " reviewContent = " + review.getContent());

        //db에 저장된 Review 추출
        Long matchingReviewId = em.createQuery("select r.id from Review r where r.member = :member", Long.class)
                .setParameter("member", review.getMember())
                .getSingleResult();

        //리뷰 수정
        ReviewDTO reviewDTO = new ReviewDTO();
        reviewDTO.setRating(4.3);
        reviewDTO.setTitle("After title");
        reviewDTO.setContent("After content");;
        Review afterReview = reviewService.modifyReview(reviewService.findReview(matchingReviewId), reviewDTO);
        System.out.println("afterReviewRating = " + afterReview.getRating() + " afterReviewTitle = "
                + afterReview.getTitle() + " afterReviewContent = " + afterReview.getContent());

        //검증
        assertThat(afterReview.getRating()).isEqualTo(4.3);
        assertThat(afterReview.getTitle()).isEqualTo("After title");
        assertThat(afterReview.getContent()).isEqualTo("After content");
    }

    @Test
    public void 리뷰_삭제() {
        //멤버 객체 생성 및 db저장
        Member member = new Member();
        member.setName("1");
        em.persist(member);

        //Diet 객체 생성 및 db저장
        Diet diet = new Diet();
        diet.setDate("1");
        em.persist(diet);

        //강제 db 삽입
        em.flush();

        //영속성 해제
        em.clear();

        //리뷰 객체 생성
        Review review = Review.createReview(member, 4.9, "title", "content");
        Long reviewId = reviewService.saveReview(review);

        //db에 저장된 Review 추출
        Long matchingReviewId = em.createQuery("select r.id from Review r where r.member = :member", Long.class)
                .setParameter("member", review.getMember())
                .getSingleResult();
        System.out.println("rating = " + reviewService.findReview(matchingReviewId).getRating());
        reviewService.deleteReview(reviewService.findReview(matchingReviewId));
        try {
            System.out.println("rating = " + reviewService.findReview(matchingReviewId).getRating());
        } catch (NullPointerException e) {
            System.out.println("널이지롱");
        }
        assertThat(em.find(Review.class,matchingReviewId)).isNull();

    }
}
