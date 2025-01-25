package com.janggeol.board_back.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.janggeol.board_back.entity.BoardListViewEntity;

@Repository
public interface BoardListViewRepository extends JpaRepository<BoardListViewEntity,Integer>{
    
    // 작성날짜를 최신순으로 정렬
    List<BoardListViewEntity> findByOrderByWriteDatetimeDesc();
    // 특정 시간 이후에 작성된 게시글 중 좋아요, 댓글 수, 조회 수, 작성 시간을 기준으로 정렬하여 상위 3개의 게시글
    List<BoardListViewEntity> findTop3ByWriteDatetimeGreaterThanOrderByFavoriteCountDescCommentCountDescViewCountDescWriteDatetimeDesc(String writeDatetime);
    // title에 특정 문자열이 포함되었거나, content에 특정 문자열이 포함된 게시글 중 작성 시간(writeDatetime)을 기준으로 최신순으로 정렬
    List<BoardListViewEntity> findByTitleContainsOrContentContainsOrderByWriteDatetimeDesc(String title, String content);
    // 유저 이메일을 통해 최신날짜순으로 게시물 조회
    List<BoardListViewEntity> findByWriterEmailOrderByWriteDatetimeDesc(String writerEmail);
}
