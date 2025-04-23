package io.baji.stvh.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import io.baji.stvh.dto.StudentScoreDto;
import io.baji.stvh.entity.Score;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.springframework.stereotype.Repository;

@Repository
public interface ScoreMapper extends BaseMapper<Score> {

    @Select("select score.*, test_info.test_time as testTime, test_info.id as testInfoId, test_info.test_name as testName from score, test_info\n" +
            "where score.test_info_id = test_info.id\n" +
            "and score.user_id = #{userId}\n" +
            "order by test_time desc \n" +
            "limit 1")
    StudentScoreDto getUserRecentlyScore(@Param("userId") Integer userId);

    @Select("select score.*, test_info.test_time as testTime, test_info.id as testInfoId, test_info.test_name as testName from score, test_info\n" +
            "where score.test_info_id = test_info.id\n" +
            "and score.user_id = #{userId} and score.test_info_id = #{testInfoId}")
    StudentScoreDto getStuScore(@Param("userId") Integer userId, @Param("testInfoId") Integer testInfoId);
}
