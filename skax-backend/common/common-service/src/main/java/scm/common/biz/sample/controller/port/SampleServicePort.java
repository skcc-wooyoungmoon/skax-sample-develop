package scm.common.biz.sample.controller.port;

import scm.common.biz.sample.controller.request.SampleItemRequest;
import scm.common.biz.sample.domain.SampleItem;

import java.util.List;

public interface SampleServicePort {

    List<SampleItem> findAll();

    SampleItem findById(Long id);

    SampleItem create(SampleItemRequest request);

    SampleItem update(Long id, SampleItemRequest request);

    void delete(Long id);
}
