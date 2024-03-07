package com.xz.partnerbackend.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.xz.partnerbackend.mapper.TagMapper;
import com.xz.partnerbackend.model.domain.Tag;
import com.xz.partnerbackend.service.TagService;
import org.springframework.stereotype.Service;

/**
* @author 96055
* @description 针对表【tag(标签)】的数据库操作Service实现
* @createDate 2024-03-06 18:29:39
*/
@Service
public class TagServiceImpl extends ServiceImpl<TagMapper, Tag>
    implements TagService {

}




