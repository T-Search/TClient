package de.tsearch.tclient.data;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@AllArgsConstructor
@Builder
public class PagedResponse<T> {
    private final List<T> data;
    private final boolean hasNext;
    private final String nextPage;
    private final int maxCursorFollows;
    private final int cursorFollows;
    private final int statusCode;
}
