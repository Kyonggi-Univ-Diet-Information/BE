package com.kyonggi.diet.review.moderation.report;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum ReportReasonType {

    OFFENSIVE_LANGUAGE("욕설·비하 표현이 포함되어 있어요"),
    HATE_SPEECH("혐오·차별적인 표현이에요"),
    SEXUAL_CONTENT("음란하거나 선정적인 내용이에요"),
    SPAM_OR_ADVERTISEMENT("스팸 또는 광고성 내용이에요"),
    FALSE_INFORMATION("허위 정보가 포함되어 있어요"),
    COPYRIGHT_VIOLATION("저작권을 침해한 내용이에요"),
    PERSONAL_INFORMATION_EXPOSURE("개인정보가 노출되어 있어요"),
    IRRELEVANT_CONTENT("식당/리뷰와 무관한 내용이에요"),
    DUPLICATE_CONTENT("중복되거나 반복된 내용이에요"),
    ETC("기타");

    private final String description;
}
