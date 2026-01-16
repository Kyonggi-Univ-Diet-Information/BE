package com.kyonggi.diet.auth.withDrawReason.domain;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum WithDrawReasonType {
    NO_NEED_RESTAURANT("더 이상 식당 정보가 필요하지 않아요"),
    MENU_SEARCH_DIFFICULT("메뉴를 찾는 과정이 불편해요"),
    COMMENT_DIFFICULT("댓글 작성이 불편해요"),
    OFFENSIVE_COMMENTS("불쾌한 댓글이 많아요"),
    ETC("기타");

    private final String description;
}
