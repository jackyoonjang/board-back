package com.janggeol.board_back.service.implement;

import java.text.SimpleDateFormat;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import com.janggeol.board_back.dto.request.board.PatchBoardRequestDto;
import com.janggeol.board_back.dto.request.board.PostBoardRequestDto;
import com.janggeol.board_back.dto.request.board.PostCommentRequestDto;
import com.janggeol.board_back.dto.response.ResponseDto;
import com.janggeol.board_back.dto.response.board.DeleteBoardResponseDto;
import com.janggeol.board_back.dto.response.board.GetBoardResponseDto;
import com.janggeol.board_back.dto.response.board.GetCommentListResponseDto;
import com.janggeol.board_back.dto.response.board.GetFavoriteListResponseDto;
import com.janggeol.board_back.dto.response.board.GetLatestBoardListResponseDto;
import com.janggeol.board_back.dto.response.board.GetSearchBoardListResponseDto;
import com.janggeol.board_back.dto.response.board.GetTop3BoardListResponseDto;
import com.janggeol.board_back.dto.response.board.GetUserBoardListResponseDto;
import com.janggeol.board_back.dto.response.board.IncreaseViewCountResponseDto;
import com.janggeol.board_back.dto.response.board.PatchBoardResponseDto;
import com.janggeol.board_back.dto.response.board.PostBoardResponseDto;
import com.janggeol.board_back.dto.response.board.PostCommentResponseDto;
import com.janggeol.board_back.dto.response.board.PutFavoriteResponseDto;
import com.janggeol.board_back.entity.BoardEntity;
import com.janggeol.board_back.entity.BoardListViewEntity;
import com.janggeol.board_back.entity.CommentEntity;
import com.janggeol.board_back.entity.FavoriteEntity;
import com.janggeol.board_back.entity.ImageEntity;
import com.janggeol.board_back.entity.SearchLogEntity;
import com.janggeol.board_back.repository.BoardListViewRepository;
import com.janggeol.board_back.repository.BoardRepository;
import com.janggeol.board_back.repository.CommentRepository;
import com.janggeol.board_back.repository.FavoriteRepository;
import com.janggeol.board_back.repository.ImageRepository;
import com.janggeol.board_back.repository.SearchLogRepository;
import com.janggeol.board_back.repository.UserRepository;
import com.janggeol.board_back.repository.resultSet.GetBoardResultSet;
import com.janggeol.board_back.repository.resultSet.GetCommentListResultSet;
import com.janggeol.board_back.repository.resultSet.GetFavoriteListResultSet;
import com.janggeol.board_back.service.BoardService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class BoardServiceImplement implements BoardService{
    
    private final UserRepository userRepository;
    private final BoardRepository boardRepository;
    private final ImageRepository imageRepository;
    private final FavoriteRepository favoriteRepository;
    private final CommentRepository commentRepository;
    private final SearchLogRepository searchLogRepository;
    private final BoardListViewRepository boardListViewRepository;
    

    @Override
    public ResponseEntity<? super GetBoardResponseDto> getBoard(Integer boardNumber) {
        
        GetBoardResultSet resultSet = null;
        List<ImageEntity> imageEntities = new ArrayList<>();
        try {
            resultSet = boardRepository.getBoard(boardNumber);
            if(resultSet == null) return GetBoardResponseDto.noExistBoard(); 
            
            imageEntities = imageRepository.findByBoardNumber(boardNumber);

        } catch (Exception e) {
            e.printStackTrace();
            return ResponseDto.databaseError();
        }
        return GetBoardResponseDto.success(resultSet, imageEntities);
        
    }

    @Override
    public ResponseEntity<? super GetFavoriteListResponseDto> getFavoriteList(Integer boardNumber) {

        List<GetFavoriteListResultSet> resultSets = new ArrayList<>();

        try {
            boolean existedBoard = boardRepository.existsByBoardNumber(boardNumber);
            if(!existedBoard) return GetFavoriteListResponseDto.noExistBoard();
            
            resultSets = favoriteRepository.getFavoriteList(boardNumber);
            
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseDto.databaseError();
        }
        return GetFavoriteListResponseDto.success(resultSets);
    }

    @Override
    public ResponseEntity<? super GetCommentListResponseDto> getCommentList(Integer boardNumber) {
        List<GetCommentListResultSet> resultSets = new ArrayList<>();

        try {
            boolean existedBoard = boardRepository.existsByBoardNumber(boardNumber);
            if(!existedBoard) return GetFavoriteListResponseDto.noExistBoard();
            
            resultSets = commentRepository.getCommentList(boardNumber);
            
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseDto.databaseError();
        }
        return GetCommentListResponseDto.success(resultSets);
    }

    @Override
    public ResponseEntity<? super GetLatestBoardListResponseDto> getLatestBoardList() {
        List<BoardListViewEntity> boardListViewEntities = new ArrayList<>();

        try {
            // 최신 시간순으로 jpa sql 문을 사용해서 데이터 가져오기
            boardListViewEntities = boardListViewRepository.findByOrderByWriteDatetimeDesc();
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseDto.databaseError();
        }
        return GetLatestBoardListResponseDto.success(boardListViewEntities);
    }

    @Override
    public ResponseEntity<? super GetTop3BoardListResponseDto> getTop3BoardList() {
        List<BoardListViewEntity> boardListViewEntities = new ArrayList<>();

        try {

            Date beforeWeek = Date.from(Instant.now().minus(7,ChronoUnit.DAYS));
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            String sevenDaysAgo = sdf.format(beforeWeek);
            boardListViewEntities = boardListViewRepository.findTop3ByWriteDatetimeGreaterThanOrderByFavoriteCountDescCommentCountDescViewCountDescWriteDatetimeDesc(sevenDaysAgo);

        } catch (Exception e) {
            e.printStackTrace();
            return ResponseDto.databaseError();
        }
        return GetTop3BoardListResponseDto.success(boardListViewEntities);
    }
    
    @Override
    public ResponseEntity<? super GetSearchBoardListResponseDto> getSearchBoardList(String searchWord, String preSearchWord) {

        List<BoardListViewEntity> boardListViewEntites = new ArrayList<>();
        try {
            // 검색 실행
            boardListViewEntites = boardListViewRepository.findByTitleContainsOrContentContainsOrderByWriteDatetimeDesc(searchWord, searchWord);

            // 검색기록 저장
            SearchLogEntity searchLogEntity = new SearchLogEntity(searchWord, preSearchWord, false);
            searchLogRepository.save(searchLogEntity);

            // preSearchWord에 값이 있으면 searchWord와 relation을 true로 설정.
            boolean relation = preSearchWord != null;
            if(relation) {
                searchLogEntity = new SearchLogEntity(searchWord, preSearchWord, relation);
                searchLogRepository.save(searchLogEntity);
            }

        } catch (Exception e) {
            e.printStackTrace();
            return ResponseDto.databaseError();
        }
        return GetSearchBoardListResponseDto.success(boardListViewEntites);
    }


    @Override
    public ResponseEntity<? super GetUserBoardListResponseDto> getUserBoardList(String email) {
        List<BoardListViewEntity> boardListViewEntites = new ArrayList<>();
        try {
            boolean existedUser = userRepository.existsByEmail(email);
            if(!existedUser) return GetUserBoardListResponseDto.noExistUser();
            boardListViewEntites = boardListViewRepository.findByWriterEmailOrderByWriteDatetimeDesc(email);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseDto.databaseError();
        }
        return GetUserBoardListResponseDto.success(boardListViewEntites);
    }


    @Override
    public ResponseEntity<? super PostBoardResponseDto> postBoard(PostBoardRequestDto dto, String email) {

        try {

            boolean existedEmail = userRepository.existsByEmail(email);
            if(!existedEmail)return PostBoardResponseDto.noExistUser();

            // 엔티티의 기본 생성자를 이용해서 엔티티 생성.
            BoardEntity  boardEntity = new BoardEntity(dto, email);
            boardRepository.save(boardEntity);

            // 저장해야 보드 넘버가 DB에서 생성
            int boardNumber = boardEntity.getBoardNumber();

            // 보드와 이미지엔티티를 처리할 배열 변수
            List<String> boardImageList = dto.getBoardImageList();
            List<ImageEntity> imageEntites = new ArrayList<>();
            
            // DB에 저장후 생성된 보드의 넘버와, 유저가 등록한 이미지의 이름을 imageEntity에 저장.
            for (String image: boardImageList){
                // 시퀀스넘버, 이미지 주소, 보드 넘버를 같이 가지고 있는 image엔티티
                ImageEntity imageEntity = new ImageEntity(boardNumber, image);
                imageEntites.add(imageEntity);
            }

            // 배열을 한번에 저장. 리스트형태로 갖고 있다가 한번에 sql문을 날린다.
            imageRepository.saveAll(imageEntites);
            
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseDto.databaseError();
        }
        
        return PostBoardResponseDto.success();
    }
    
    @Override
    public ResponseEntity<? super PostCommentResponseDto> postComment(PostCommentRequestDto dto, Integer boardNumber,String email) {
        try {
            
            BoardEntity boardEntity = boardRepository.findByBoardNumber(boardNumber);
            if(boardEntity == null) return PostCommentResponseDto.noExistBoard();

            boolean existedUser = userRepository.existsByEmail(email);
            if(!existedUser) return PostCommentResponseDto.noExistUser();

            CommentEntity commentEntity = new CommentEntity(dto,boardNumber,email);
            commentRepository.save(commentEntity);

            boardEntity.increaseCommentCount();
            boardRepository.save(boardEntity);

        } catch (Exception e) {
            e.printStackTrace();
            return ResponseDto.databaseError();
        }
        return PostCommentResponseDto.success();
    }

    @Override
    public ResponseEntity<? super PutFavoriteResponseDto> putFavorite(Integer boardNumber, String email) {
        
        try {
            
            boolean existedUser = userRepository.existsByEmail(email);
            if(!existedUser) return PutFavoriteResponseDto.noExistUser();

            BoardEntity boardEntity = boardRepository.findByBoardNumber(boardNumber);
            if(boardEntity == null) return PutFavoriteResponseDto.noExistBoard();

            FavoriteEntity favoriteEntity = favoriteRepository.findByBoardNumberAndUserEmail(boardNumber, email);
            if (favoriteEntity == null) {
                favoriteEntity = new FavoriteEntity(email, boardNumber);
                favoriteRepository.save(favoriteEntity);
                boardEntity.increaseFavoriteCount();
            }
            else {
                favoriteRepository.delete(favoriteEntity);
                boardEntity.decreaseFavoriteCount();
            }

            boardRepository.save(boardEntity);

        } catch (Exception e) {
            e.printStackTrace();
            return ResponseDto.databaseError();
        }
        return PutFavoriteResponseDto.success();   
    }

    @Override
    public ResponseEntity<? super PatchBoardResponseDto> patchBoard(PatchBoardRequestDto dto, Integer boardNumber, String email) {
        try {

            BoardEntity boardEntity = boardRepository.findByBoardNumber(boardNumber);
            if(boardEntity == null) return PatchBoardResponseDto.noExistBoard();

            boolean existedUser = userRepository.existsByEmail(email);
            if(!existedUser) return PatchBoardResponseDto.noExistUser();

            String writerEmail = boardEntity.getWriterEmail();
            boolean isWriter = writerEmail.equals(email);
            if(!isWriter) return PatchBoardResponseDto.noPermission();

            boardEntity.patchBoard(dto);
            boardRepository.save(boardEntity);

            // 기존에 있던 이미지 삭제 및 다시 저장하기
            imageRepository.deleteByBoardNumber(boardNumber);
            List<String> boardImageList = dto.getBoardImageList();
            List<ImageEntity> imageEntities = new ArrayList<>();

            for(String image: boardImageList) {
                ImageEntity imageEntity = new ImageEntity(boardNumber, image);
                imageEntities.add(imageEntity);
            }

            imageRepository.saveAll(imageEntities);

        } catch (Exception e) {
            e.printStackTrace();
            return ResponseDto.databaseError();
        }
        return PatchBoardResponseDto.success();
    }

    @Override
    public ResponseEntity<? super IncreaseViewCountResponseDto> increaseViewCount(Integer boardNumber) {
        try {
            BoardEntity boardEntity = boardRepository.findByBoardNumber(boardNumber);
            if(boardEntity == null) return IncreaseViewCountResponseDto.noExistBoard();

            boardEntity.increaseViewCount();
            boardRepository.save(boardEntity);

        } catch (Exception e) {
            e.printStackTrace();
            return ResponseDto.databaseError();
        }
        return IncreaseViewCountResponseDto.success();
    }

    @Override
    public ResponseEntity<? super DeleteBoardResponseDto> deleteBoard(Integer boardNumber, String email) {
        try {
            boolean existedUser = userRepository.existsByEmail(email);
            if(!existedUser) return DeleteBoardResponseDto.noExistUser();

            BoardEntity boardEntity = boardRepository.findByBoardNumber(boardNumber);
            if(boardEntity == null) return DeleteBoardResponseDto.noExistBoard();

            String writerEmail = boardEntity.getWriterEmail();
            boolean isWriter = writerEmail.equals(email);
            if(!isWriter) return DeleteBoardResponseDto.noPermission();

            imageRepository.deleteByBoardNumber(boardNumber);
            commentRepository.deleteByBoardNumber(boardNumber);
            favoriteRepository.deleteByBoardNumber(boardNumber);

            boardRepository.delete(boardEntity);
            
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseDto.databaseError();
        }
        return DeleteBoardResponseDto.success();
    }

}
