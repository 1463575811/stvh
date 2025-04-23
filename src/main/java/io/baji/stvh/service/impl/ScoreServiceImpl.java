package io.baji.stvh.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import io.baji.stvh.entity.Score;
import io.baji.stvh.mapper.ScoreMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Transactional
@Service
public class ScoreServiceImpl extends ServiceImpl<ScoreMapper, Score> {
}
