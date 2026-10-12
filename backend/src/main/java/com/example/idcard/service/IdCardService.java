package com.example.idcard.service;

import com.example.idcard.common.BusinessException;
import com.example.idcard.dto.IdCardRequest;
import com.example.idcard.dto.IdCardResponse;
import com.example.idcard.dto.PageResponse;
import com.example.idcard.dto.StatsResponse;
import com.example.idcard.entity.IdCard;
import com.example.idcard.repository.IdCardRepository;
import com.example.idcard.util.IdCardValidator;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.Period;
import java.util.List;

@Service
public class IdCardService {

    private static final String FEMALE = "女";
    private static final String MALE = "男";
    private static final int MAX_PAGE_SIZE = 100;

    private final IdCardRepository repository;

    public IdCardService(IdCardRepository repository) {
        this.repository = repository;
    }

    /** 分页 + 姓名/身份证号模糊搜索，页码从 1 开始，按 id 倒序。 */
    @Transactional(readOnly = true)
    public PageResponse<IdCardResponse> page(String keyword, int page, int size) {
        int pageIndex = Math.max(page, 1) - 1;
        int pageSize = Math.min(Math.max(size, 1), MAX_PAGE_SIZE);
        Pageable pageable = PageRequest.of(pageIndex, pageSize, Sort.by(Sort.Direction.DESC, "id"));

        String kw = keyword == null ? "" : keyword.trim();
        Page<IdCard> result = kw.isEmpty()
                ? repository.findAll(pageable)
                : repository.findByNameContainingOrIdNumberContaining(kw, kw, pageable);

        List<IdCardResponse> list = result.getContent().stream().map(IdCardResponse::from).toList();
        return PageResponse.of(result, list);
    }

    @Transactional(readOnly = true)
    public IdCardResponse get(Long id) {
        return IdCardResponse.from(findOrThrow(id));
    }

    @Transactional
    public IdCardResponse create(IdCardRequest request) {
        String idNumber = checkIdNumber(request.idNumber(), null);
        if (repository.existsByIdNumber(idNumber)) {
            throw BusinessException.conflict("身份证号已存在：" + idNumber);
        }
        IdCard entity = new IdCard();
        apply(entity, request, idNumber);
        return IdCardResponse.from(repository.save(entity));
    }

    @Transactional
    public IdCardResponse update(Long id, IdCardRequest request) {
        IdCard entity = findOrThrow(id);
        String idNumber = checkIdNumber(request.idNumber(), id);
        apply(entity, request, idNumber);
        return IdCardResponse.from(repository.save(entity));
    }

    @Transactional
    public void delete(Long id) {
        repository.delete(findOrThrow(id));
    }

    /** 统计：总数、男女数量与占比、年龄段分布。 */
    @Transactional(readOnly = true)
    public StatsResponse stats() {
        long total = repository.count();
        long maleCount = repository.countByGender(MALE);
        long femaleCount = repository.countByGender(FEMALE);
        long otherCount = Math.max(total - maleCount - femaleCount, 0);

        long[] buckets = new long[5];
        for (LocalDate birthDate : repository.findAllBirthDates()) {
            buckets[bucketOf(Period.between(birthDate, LocalDate.now()).getYears())]++;
        }
        List<StatsResponse.AgeGroup> ageGroups = List.of(
                new StatsResponse.AgeGroup("0-17", buckets[0]),
                new StatsResponse.AgeGroup("18-30", buckets[1]),
                new StatsResponse.AgeGroup("31-45", buckets[2]),
                new StatsResponse.AgeGroup("46-60", buckets[3]),
                new StatsResponse.AgeGroup("60岁以上", buckets[4])
        );

        return new StatsResponse(
                total,
                maleCount,
                femaleCount,
                otherCount,
                ratio(maleCount, total),
                ratio(femaleCount, total),
                ageGroups
        );
    }

    private IdCard findOrThrow(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> BusinessException.notFound("身份证记录不存在：" + id));
    }

    /**
     * 规范化并校验身份证号：格式正确、未被其它记录占用。
     *
     * @param excludeId 修改时排除自身，新增时传 null
     */
    private String checkIdNumber(String rawIdNumber, Long excludeId) {
        String idNumber = IdCardValidator.normalize(rawIdNumber);
        if (!IdCardValidator.isValidFormat(idNumber)) {
            throw BusinessException.badRequest("身份证号格式不正确，应为 18 位（最后一位可为 X）");
        }
        boolean duplicated = (excludeId == null)
                ? repository.existsByIdNumber(idNumber)
                : repository.existsByIdNumberAndIdNot(idNumber, excludeId);
        if (duplicated) {
            throw BusinessException.conflict("身份证号已存在：" + idNumber);
        }
        return idNumber;
    }

    private void apply(IdCard entity, IdCardRequest request, String idNumber) {
        entity.setName(request.name().trim());
        entity.setIdNumber(idNumber);
        entity.setAddress(request.address() == null ? null : request.address().trim());

        String gender = request.gender() == null ? null : request.gender().trim();
        if (gender == null || gender.isEmpty()) {
            gender = IdCardValidator.deriveGender(idNumber);
        } else if (!MALE.equals(gender) && !FEMALE.equals(gender)) {
            throw BusinessException.badRequest("性别只能是 男 或 女");
        }
        entity.setGender(gender);

        LocalDate birthDate = request.birthDate() != null
                ? request.birthDate()
                : IdCardValidator.deriveBirthDate(idNumber);
        if (birthDate == null) {
            throw BusinessException.badRequest("身份证号中的出生日期无效，请手动填写出生日期");
        }
        entity.setBirthDate(birthDate);
    }

    private int bucketOf(int age) {
        if (age < 18) {
            return 0;
        }
        if (age <= 30) {
            return 1;
        }
        if (age <= 45) {
            return 2;
        }
        if (age <= 60) {
            return 3;
        }
        return 4;
    }

    private BigDecimal ratio(long part, long total) {
        if (total <= 0) {
            return BigDecimal.ZERO.setScale(4, RoundingMode.HALF_UP);
        }
        return BigDecimal.valueOf(part)
                .divide(BigDecimal.valueOf(total), 4, RoundingMode.HALF_UP);
    }
}
