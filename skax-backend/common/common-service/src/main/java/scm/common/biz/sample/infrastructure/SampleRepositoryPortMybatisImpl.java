package scm.common.biz.sample.infrastructure;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import scm.common.app.exception.CustomException;
import scm.common.app.exception.ErrorCode;
import scm.common.biz.sample.domain.SampleItem;
import scm.common.biz.sample.infrastructure.mybatis.SampleItemDto;
import scm.common.biz.sample.infrastructure.mybatis.SampleRepositoryMybatis;
import scm.common.biz.sample.service.port.SampleRepositoryPort;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Repository
@RequiredArgsConstructor
public class SampleRepositoryPortMybatisImpl implements SampleRepositoryPort {

    private final SampleRepositoryMybatis sampleRepositoryMybatis;

    @Override
    public Optional<SampleItem> findById(Long id) {
        return sampleRepositoryMybatis.findById(id).map(SampleItemDto::toModel);
    }

    @Override
    public List<SampleItem> findAll() {
        return sampleRepositoryMybatis.findAll().stream()
                .map(SampleItemDto::toModel)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public SampleItem save(SampleItem item) {
        SampleItemDto dto = SampleItemDto.from(item);
        sampleRepositoryMybatis.insert(dto);
        return sampleRepositoryMybatis.findById(dto.getId())
                .map(SampleItemDto::toModel)
                .orElseThrow(() -> new CustomException(ErrorCode.NOT_FOUND_ELEMENT));
    }

    @Override
    public void update(Long id, SampleItem item) {
        int updated = sampleRepositoryMybatis.update(id, item.getName(), item.getDescription());
        if (updated == 0) {
            throw new CustomException(ErrorCode.NOT_FOUND_ELEMENT);
        }
    }

    @Override
    public void delete(Long id) {
        int deleted = sampleRepositoryMybatis.delete(id);
        if (deleted == 0) {
            throw new CustomException(ErrorCode.NOT_FOUND_ELEMENT);
        }
    }
}
