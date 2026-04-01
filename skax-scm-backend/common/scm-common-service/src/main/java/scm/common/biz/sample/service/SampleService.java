package scm.common.biz.sample.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import scm.common.app.exception.CustomException;
import scm.common.app.exception.ErrorCode;
import scm.common.biz.sample.controller.port.SampleServicePort;
import scm.common.biz.sample.controller.request.SampleItemRequest;
import scm.common.biz.sample.domain.SampleItem;
import scm.common.biz.sample.service.port.SampleRepositoryPort;

import java.util.List;

@Service
@RequiredArgsConstructor
public class SampleService implements SampleServicePort {

    private final SampleRepositoryPort sampleRepositoryPort;

    @Override
    @Transactional(readOnly = true)
    public List<SampleItem> findAll() {
        return sampleRepositoryPort.findAll();
    }

    @Override
    @Transactional(readOnly = true)
    public SampleItem findById(Long id) {
        return sampleRepositoryPort.findById(id)
                .orElseThrow(() -> new CustomException(ErrorCode.NOT_FOUND_ELEMENT));
    }

    @Override
    @Transactional
    public SampleItem create(SampleItemRequest request) {
        SampleItem item = SampleItem.forNew(request.getName(), request.getDescription());
        return sampleRepositoryPort.save(item);
    }

    @Override
    @Transactional
    public SampleItem update(Long id, SampleItemRequest request) {
        SampleItem patch = SampleItem.builder()
                .name(request.getName())
                .description(request.getDescription())
                .build();
        sampleRepositoryPort.update(id, patch);
        return findById(id);
    }

    @Override
    @Transactional
    public void delete(Long id) {
        sampleRepositoryPort.delete(id);
    }
}
