package com.janggeol.board_back.dto.object;

import java.util.ArrayList;
import java.util.List;

import com.janggeol.board_back.entity.BoardListViewEntity;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class BoardListItem {
    private int boardNumber;
    private String title;
    private String content;
    private String boardTitleImage;
    private int favoriteCount;
    private int commentCount;
    private int viewCount;
    private String writeDatetime;
    private String writeNickname;
    private String writeProfileImage;

    // entity를 dto로 변환하기
    public BoardListItem(BoardListViewEntity boardListViewEntity){
        this.boardNumber = boardListViewEntity.getBoardNumber();
        this.title  = boardListViewEntity.getTitle();
        this.content = boardListViewEntity.getContent();
        this.boardTitleImage = boardListViewEntity.getTitleImage();
        this.favoriteCount = boardListViewEntity.getFavoriteCount();
        this.commentCount = boardListViewEntity.getCommentCount();
        this.viewCount = boardListViewEntity.getViewCount();
        this.writeDatetime = boardListViewEntity.getWriteDatetime();
        this.writeNickname = boardListViewEntity.getWriterNickname();
        this.writeProfileImage = boardListViewEntity.getWriterProfileImage();
    }

    // 리스트로 담겨있는 entities를 for문을 통해 하나씩 dto로 변환해 저장
    public static List<BoardListItem> getList(List<BoardListViewEntity> boardListViewEntities) {
        List<BoardListItem> list = new ArrayList<>();
        for (BoardListViewEntity boardListViewEntity : boardListViewEntities){
            BoardListItem boardListItem = new BoardListItem(boardListViewEntity);
            list.add(boardListItem);
        }
        return list;
    }
}
