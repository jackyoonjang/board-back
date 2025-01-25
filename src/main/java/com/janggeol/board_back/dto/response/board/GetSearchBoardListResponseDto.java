package com.janggeol.board_back.dto.response.board;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import com.janggeol.board_back.common.ResponseCode;
import com.janggeol.board_back.common.ResponseMessage;
import com.janggeol.board_back.dto.object.BoardListItem;
import com.janggeol.board_back.dto.response.ResponseDto;
import com.janggeol.board_back.entity.BoardListViewEntity;

import lombok.Getter;

@Getter
public class GetSearchBoardListResponseDto extends ResponseDto{

    private List<BoardListItem> searchList;

    private GetSearchBoardListResponseDto(List<BoardListViewEntity> boardListViewEntities) {
        super(ResponseCode.SUCCESS, ResponseMessage.SUCCESS);
        this.searchList = BoardListItem.getList(boardListViewEntities);
    }

    public static ResponseEntity<GetSearchBoardListResponseDto> success(List<BoardListViewEntity> boardListViewEntites) {
        GetSearchBoardListResponseDto result = new GetSearchBoardListResponseDto(boardListViewEntites);
        return ResponseEntity.status(HttpStatus.OK).body(result);
    }
    
}
