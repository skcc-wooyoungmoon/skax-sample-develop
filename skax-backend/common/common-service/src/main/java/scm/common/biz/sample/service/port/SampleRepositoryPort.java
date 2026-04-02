package scm.common.biz.sample.service.port;

import scm.common.biz.sample.domain.SampleItem;

import java.util.List;
import java.util.Optional;

public interface SampleRepositoryPort {

    Optional<SampleItem> findById(Long id);

    List<SampleItem> findAll();

    SampleItem save(SampleItem item);

    void update(Long id, SampleItem item);

    void delete(Long id);
}
