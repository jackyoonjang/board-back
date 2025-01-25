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
public class GetLatestBoardListResponseDto extends ResponseDto {
    
    private List<BoardListItem> latestList;

    // entity를 인수로 받아 프로토콜 메시지와 dto로 변환하는 생성자
    private GetLatestBoardListResponseDto(List<BoardListViewEntity> boardEntities) {
        super(ResponseCode.SUCCESS, ResponseMessage.SUCCESS);
        this.latestList = BoardListItem.getList(boardEntities);
    }

    // 요청이 성공시 사용하는 메소드로, 인수로 받은 값을 dto로 생성하고, 바디에 데이터를 담아 OK응답 프로토콜로 리턴. 
    public static ResponseEntity<GetLatestBoardListResponseDto> success(List<BoardListViewEntity> boardEntities){
        GetLatestBoardListResponseDto result = new GetLatestBoardListResponseDto(boardEntities);
        return ResponseEntity.status(HttpStatus.OK).body(result);
    }

    // 최신순 나열에 board가 없을 수도 있기 때문에 board가 없다고 오류를 보내면 안된다.
}
